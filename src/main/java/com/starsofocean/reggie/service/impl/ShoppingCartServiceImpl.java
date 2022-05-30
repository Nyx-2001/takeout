package com.starsofocean.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starsofocean.reggie.domain.ShoppingCart;
import com.starsofocean.reggie.mapper.ShoppingCartMapper;
import com.starsofocean.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
