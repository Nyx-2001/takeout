package com.starsofocean.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starsofocean.reggie.common.BaseContext;
import com.starsofocean.reggie.common.CustomException;
import com.starsofocean.reggie.domain.*;
import com.starsofocean.reggie.mapper.OrdersMapper;
import com.starsofocean.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        //查询用户购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车信息错误,下单失败");
        }
        //查询用户信息
        User user = userService.getById(userId);
        //查询地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook==null){
            throw new CustomException("地址信息有误,下单失败");
        }
        //生成订单号
        long orderId = IdWorker.getId();
        //保证多线程计算不会出错
        AtomicInteger amount=new AtomicInteger(0);
        //商品总数量
        AtomicInteger sumNum=new AtomicInteger(0);
        //计算购物车金额和商品总数量以及返回订单详情信息
        List<OrderDetail> orderDetailList=shoppingCartList.stream().map((item)->{
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            orderDetail.setImage(item.getImage());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            sumNum.addAndGet(item.getNumber());
            return orderDetail;
        }).collect(Collectors.toList());
        //向订单表插入数据
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setAddressBookId(addressBookId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setSumNum(sumNum.get());
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        orders.setAddress((addressBook.getProvinceName()!=null?addressBook.getProvinceName():"")
                         +(addressBook.getCityName()!=null?addressBook.getCityName():"")
                         +(addressBook.getDistrictName()!=null?addressBook.getDistrictName():"")
                         +(addressBook.getDetail()!=null?addressBook.getDetail():""));
        orders.setConsignee(addressBook.getConsignee());
        this.save(orders);
        //订单明细表插入数据
        orderDetailService.saveBatch(orderDetailList);
        //清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}
