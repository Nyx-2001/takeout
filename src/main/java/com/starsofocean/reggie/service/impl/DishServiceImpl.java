package com.starsofocean.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starsofocean.reggie.domain.Dish;
import com.starsofocean.reggie.domain.DishFlavor;
import com.starsofocean.reggie.dto.DishDto;
import com.starsofocean.reggie.mapper.DishMapper;
import com.starsofocean.reggie.service.DishFlavorService;
import com.starsofocean.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品以及口味
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表
        this.save(dishDto);
        Long dishId = dishDto.getId();//菜品id
        List<DishFlavor> flavors=dishDto.getFlavors();//菜品口味
        //以stream流形式将口味集合中的每个口味元素赋上菜品id
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 获取菜品信息及对应口味
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto=new DishDto();
        //根据id获取菜品信息
        Dish dish = this.getById(id);
        //将菜品信息拷贝给dto
        BeanUtils.copyProperties(dish,dishDto);
        //根据菜品id查询菜品口味
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(flavorLambdaQueryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 更新菜品和口味
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish基本信息
        this.updateById(dishDto);
        //先删除当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(flavorLambdaQueryWrapper);
        //再新增修改后的口味信息
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 单一或者批量处理商品状态
     * @param ids
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, int status) {
        //通过stream流将数组ids的值全部取出并查出对应全部id的全部菜品信息并封装在集合中
        List<Dish> dishList = this.listByIds(Arrays.stream(ids).toList());
        //通过stream流修改菜品集合中的所有status并返回集合中
        dishList=dishList.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        //根据id批量更新
        this.updateBatchById(dishList);
    }

    /**
     * 单一或者批量删除商品以及对应的口味
     * @param ids
     */
    @Transactional
    @Override
    public void deleteDish(Long[] ids) {
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.in(DishFlavor::getDishId, Arrays.stream(ids).toList());
        dishFlavorService.remove(flavorLambdaQueryWrapper);
        this.removeByIds(Arrays.stream(ids).toList());
    }
}
