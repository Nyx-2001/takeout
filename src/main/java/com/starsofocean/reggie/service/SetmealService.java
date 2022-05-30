package com.starsofocean.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starsofocean.reggie.domain.Setmeal;
import com.starsofocean.reggie.dto.SetmealDto;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐
    public void saveWithSetmealDish(SetmealDto setmealDto);
    //根据id查询套餐
    public SetmealDto getSetmealDish(Long id);
    //修改套餐
    public void updateSetmealDish(SetmealDto setmealDto);
    //单一或者批量处理套餐状态
    public void updateStatus(Long[] ids, int status);
    //单一或者批量删除套餐
    public void deleteSetmeal(Long[] ids);
}
