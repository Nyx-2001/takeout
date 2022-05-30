package com.starsofocean.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starsofocean.reggie.domain.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
