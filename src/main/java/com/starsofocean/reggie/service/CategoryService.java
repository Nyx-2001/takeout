package com.starsofocean.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starsofocean.reggie.domain.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
