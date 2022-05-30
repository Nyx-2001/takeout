package com.starsofocean.reggie.dto;

import com.starsofocean.reggie.domain.SetmealDish;
import com.starsofocean.reggie.domain.Setmeal;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
