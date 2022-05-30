package com.starsofocean.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starsofocean.reggie.domain.Dish;
import com.starsofocean.reggie.dto.DishDto;

public interface DishService extends IService<Dish> {
    //新增菜品信息
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应口味
    public DishDto getByIdWithFlavor(Long id);
    //修改菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);
    //单一或者批量处理商品状态
    public void updateStatus(Long[] ids, int status);
    //单一或者批量删除商品
    public void deleteDish(Long[] ids);
}
