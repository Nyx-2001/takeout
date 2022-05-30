package com.starsofocean.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starsofocean.reggie.domain.Setmeal;
import com.starsofocean.reggie.domain.SetmealDish;
import com.starsofocean.reggie.dto.SetmealDto;
import com.starsofocean.reggie.mapper.SetmealMapper;
import com.starsofocean.reggie.service.SetmealDishService;
import com.starsofocean.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     *新增套餐
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithSetmealDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     *展示套餐修改页
     * @param id
     */
    @Transactional
    @Override
    public SetmealDto getSetmealDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     *修改套餐
     * @param setmealDto
     */
    @Transactional
    @Override
    public void updateSetmealDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        //根据套餐id删除原有关系表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);
        //新增关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 单个或批量调整套餐状态
     * @param ids
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, int status) {
        //通过stream流将数组ids的值全部取出并查出对应全部id的全部菜品信息并封装在集合中
        List<Setmeal> setmealList = this.listByIds(Arrays.stream(ids).toList());
        //通过stream流修改菜品集合中的所有status并返回集合中
        setmealList=setmealList.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        //根据id批量更新
        this.updateBatchById(setmealList);
    }

    /**
     * 单个或批量删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void deleteSetmeal(Long[] ids) {
        LambdaQueryWrapper<SetmealDish> flavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.in(SetmealDish::getSetmealId, Arrays.stream(ids).toList());
        setmealDishService.remove(flavorLambdaQueryWrapper);
        this.removeByIds(Arrays.stream(ids).toList());
    }
}
