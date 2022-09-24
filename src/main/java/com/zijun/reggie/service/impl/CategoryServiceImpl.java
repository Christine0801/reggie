package com.zijun.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zijun.reggie.common.CustomException;
import com.zijun.reggie.entity.Category;
import com.zijun.reggie.entity.Dish;
import com.zijun.reggie.entity.Setmeal;
import com.zijun.reggie.mapper.CategoryMapper;
import com.zijun.reggie.service.CategoryService;
import com.zijun.reggie.service.DishService;
import com.zijun.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

  @Autowired
  private DishService dishService ;

  @Autowired
  private SetmealService setmealService ;

  @Override
  public void remove(Long id) {

    LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Dish::getCategoryId, id);
    int count = dishService.count(wrapper);
    if (count > 0) {
      // 当前分类下有关联菜品，抛出自定义异常
      throw new CustomException("当前分类下有关联菜品，不能删除");
    }

    LambdaQueryWrapper<Setmeal> wrapper1 = new LambdaQueryWrapper<>();
    wrapper1.eq(Setmeal::getCategoryId, id);
    int count1 = setmealService.count(wrapper1);
    if (count1 > 0) {
      // 当前分类下有关联套餐，抛出自定义异常
      throw new CustomException("当前分类下有关联套餐，不能删除");
    }

    super.removeById(id);
  }
}
