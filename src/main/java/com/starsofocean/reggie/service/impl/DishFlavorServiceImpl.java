package com.starsofocean.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starsofocean.reggie.domain.DishFlavor;
import com.starsofocean.reggie.mapper.DishFlavorMapper;
import com.starsofocean.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
