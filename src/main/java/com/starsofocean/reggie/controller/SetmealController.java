package com.starsofocean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starsofocean.reggie.common.R;
import com.starsofocean.reggie.domain.Category;
import com.starsofocean.reggie.domain.Setmeal;
import com.starsofocean.reggie.dto.SetmealDto;
import com.starsofocean.reggie.service.CategoryService;
import com.starsofocean.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealService setmealService;
    @Resource
    private CategoryService categoryService;

    /**
     * 分页展示套餐
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,setmealLambdaQueryWrapper);
        Page<SetmealDto> dtoPageInfo=new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");
        List<Setmeal> setmealList=pageInfo.getRecords();
        List<SetmealDto> dtoList=setmealList.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Category::getId,item.getCategoryId());
            Category category = categoryService.getOne(lambdaQueryWrapper);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPageInfo.setRecords(dtoList);
        return R.success(dtoPageInfo);
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("新增套餐成功");
    }
    @GetMapping("/{id}")
    public R<SetmealDto> show(@PathVariable Long id){
        SetmealDto setmealDish = setmealService.getSetmealDish(id);
        return R.success(setmealDish);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmealDish(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 单一或批量处理套餐状态
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(Long[] ids,@PathVariable int status){
        setmealService.updateStatus(ids,status);
        return R.success("修改状态成功");
    }

    /**
     * 单一或批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        List<Setmeal> setmealList = setmealService.listByIds(Arrays.stream(ids).toList());
        for(Setmeal setmeal:setmealList){
            if (setmeal.getStatus()!=0)
                return R.error("套餐启售中,删除失败...");
        }
        setmealService.deleteSetmeal(ids);
        return R.success("删除成功");
    }

    /**
     * 套餐分类展示
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Long categoryId,int status){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,categoryId);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,status);
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);
        return R.success(setmealList);
    }
}
