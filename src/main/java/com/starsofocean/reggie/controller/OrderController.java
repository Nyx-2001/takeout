package com.starsofocean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starsofocean.reggie.common.R;
import com.starsofocean.reggie.domain.Orders;
import com.starsofocean.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 管理端查看订单
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> list(int page, int pageSize, Long number){
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper=new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.like(number!=null,Orders::getNumber,number);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,ordersLambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 管理端修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("修改配送状态成功");
    }

    /**
     *用户订单状态查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper=new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,ordersLambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 用户再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        Orders order = ordersService.getById(orders);
        long orderId = IdWorker.getId();
        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        order.setStatus(2);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        ordersService.save(order);
        return R.success("再来一单成功");
    }
}
