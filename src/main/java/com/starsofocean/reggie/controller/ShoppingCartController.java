package com.starsofocean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starsofocean.reggie.common.BaseContext;
import com.starsofocean.reggie.common.R;
import com.starsofocean.reggie.domain.ShoppingCart;
import com.starsofocean.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或套餐进入购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart){
        //将用户id与购物车商品绑定
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //查询是否已添加
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if(shoppingCartServiceOne!=null){
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber()+1);
            shoppingCartService.updateById(shoppingCartServiceOne);
            return R.success("添加购物车成功");
        }
        shoppingCartService.save(shoppingCart);
        return R.success("添加购物车成功");
    }

    /**
     * 移除菜品或套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        //将用户id与购物车商品绑定
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if(shoppingCartServiceOne.getNumber()==1){
            shoppingCartService.removeById(shoppingCartServiceOne);
            return R.success("删除成功");
        }
        shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber()-1);
        shoppingCartService.updateById(shoppingCartServiceOne);
        return R.success("添加购物车成功");
    }

    /**
     * 获取购物车信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(shoppingCartList);
    }

    /**
     * 清理购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();
        //根据用户id删除购物车内容
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("清除成功");
    }
}
