package com.starsofocean.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starsofocean.reggie.common.CustomException;
import com.starsofocean.reggie.domain.Category;
import com.starsofocean.reggie.domain.Dish;
import com.starsofocean.reggie.domain.Setmeal;
import com.starsofocean.reggie.mapper.CategoryMapper;
import com.starsofocean.reggie.service.CategoryService;
import com.starsofocean.reggie.service.DishService;
import com.starsofocean.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        //查询当前分类是否关联菜品，如何有关联则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapperQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapperQueryWrapper.eq(Dish::getCategoryId,ids);
        long dishCount = dishService.count(dishLambdaQueryWrapperQueryWrapper);
        if(dishCount>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联套餐，如何有关联则抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        long setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        super.removeById(ids);
    }
}
