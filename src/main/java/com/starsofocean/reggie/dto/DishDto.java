package com.starsofocean.reggie.dto;

import com.starsofocean.reggie.domain.Dish;
import com.starsofocean.reggie.domain.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
