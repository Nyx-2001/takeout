package com.starsofocean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starsofocean.reggie.common.R;
import com.starsofocean.reggie.domain.Category;
import com.starsofocean.reggie.domain.Dish;
import com.starsofocean.reggie.domain.DishFlavor;
import com.starsofocean.reggie.dto.DishDto;
import com.starsofocean.reggie.service.CategoryService;
import com.starsofocean.reggie.service.DishFlavorService;
import com.starsofocean.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 分页查询菜品信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dtoPageInfo=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,dishLambdaQueryWrapper);
        /**
         * 对象拷贝
         * 这里将records排除是因为这里record里面只有分类id没有分类名称，
         * 我们需要将每条record单独拿出来处理，将分类id对应的名称查出来再给到dto对应的record
         */
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");
        //获取records对应的集合
        List<Dish> dishList=pageInfo.getRecords();
        //将dish中的records集合以stream流的形式进行处理，最终以集合形式返回给到dto集合
        List<DishDto> dtoList= dishList.stream().map((item)-> {
            DishDto dishDto = new DishDto();
            //将record拷贝给dto
            BeanUtils.copyProperties(item, dishDto);
            //获取每条record的分类id
            Long categoryId = item.getCategoryId();
            //通过分类id查询对应分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //获取对应分类对象的名称并给到dto
                dishDto.setCategoryName(category.getName());
            }
            //返回dto对象
            return dishDto;
        }).collect(Collectors.toList());
        //将dto的records给到page
        dtoPageInfo.setRecords(dtoList);
        return R.success(dtoPageInfo);
    }

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 查询菜品信息和对应口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

    /**
     * 单一或者批量处理商品状态
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(Long[] ids,@PathVariable int status){
      dishService.updateStatus(ids,status);
      return R.success("修改成功");
    }

    /**
     * 单一或者批量删除商品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        List<Dish> dishList = dishService.listByIds(Arrays.stream(ids).toList());
        for(Dish dish:dishList){
            if(dish.getStatus()!=0)
                return R.error("菜品启售中,删除失败...");
        }
        dishService.deleteDish(ids);
        return R.success("删除成功");
    }

    /**
     *菜品分类展示
     * @param categoryId
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Long categoryId,int status){
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
//        dishLambdaQueryWrapper.eq(Dish::getStatus,status);
//        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
//        return R.success(dishList);
//    }

    /**
     *菜品分类展示
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId,int status){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
        dishLambdaQueryWrapper.eq(Dish::getStatus,status);
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
        List<DishDto> dtoList=dishList.stream().map((item)->{
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(flavorLambdaQueryWrapper);
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dtoList);
    }
}
