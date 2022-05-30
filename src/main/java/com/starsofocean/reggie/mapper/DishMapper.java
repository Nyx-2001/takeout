package com.starsofocean.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starsofocean.reggie.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
