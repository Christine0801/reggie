package com.zijun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zijun.reggie.common.R;
import com.zijun.reggie.dto.SetmealDto;
import com.zijun.reggie.entity.Dish;
import com.zijun.reggie.entity.Setmeal;
import com.zijun.reggie.entity.SetmealDish;
import com.zijun.reggie.service.CategoryService;
import com.zijun.reggie.service.DishService;
import com.zijun.reggie.service.SetmealDishService;
import com.zijun.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

  @Autowired
  private SetmealService setmealService ;

  @Autowired
  private SetmealDishService setmealDishService ;

  @Autowired
  private CategoryService categoryService ;

  @PostMapping
  public R<String> save(@RequestBody SetmealDto setmealDto) {

    setmealService.saveWithDish(setmealDto);

    return R.success("新增成功");
  }

  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {

    Page<Setmeal> setmealPage = new Page<>(page, pageSize);
    Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

    LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);

    setmealService.page(setmealPage, wrapper);

    BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
    List<SetmealDto> setmealDtos = setmealPage.getRecords().stream().map(item -> {
      SetmealDto setmealDto = new SetmealDto();
      BeanUtils.copyProperties(item, setmealDto);
      setmealDto.setCategoryName(categoryService.getById(item.getCategoryId()).getName());
      return setmealDto;
    }).collect(Collectors.toList());

    setmealDtoPage.setRecords(setmealDtos);

    return R.success(setmealDtoPage);
  }

  @GetMapping("/{id}")
  public R<SetmealDto> getById(@PathVariable("id") Long id) {

    Setmeal setmeal = setmealService.getById(id);

    SetmealDto setmealDto = new SetmealDto();
    BeanUtils.copyProperties(setmeal, setmealDto);
    LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(SetmealDish::getSetmealId, id);
    List<SetmealDish> list = setmealDishService.list(wrapper);

    setmealDto.setSetmealDishes(list);

    String categoryName = categoryService.getById(setmeal.getCategoryId()).getName();

    setmealDto.setCategoryName(categoryName);

    return R.success(setmealDto);
  }

  @DeleteMapping
  public R<String> delete(String ids) {

    String[] idArr = ids.split(",");
    List<Long> list = Arrays.stream(idArr).map(item -> {
      return Long.parseLong(item);
    }).collect(Collectors.toList());

    setmealService.delete(list);

    return R.success("删除成功");
  }

  @PostMapping("/status/{status}")
  public R<String> discontinued(@PathVariable("status") int status, String ids) {

    List<Setmeal> setmeals = Arrays.stream(ids.split(",")).map(item -> {
      Setmeal setmeal = new Setmeal();
      setmeal.setId(Long.parseLong(item));
      setmeal.setStatus(status);
      ;
      return setmeal;
    }).collect(Collectors.toList());

    setmealService.updateBatchById(setmeals);

    return R.success("修改成功");
  }

  @PutMapping
  @Transactional
  public R<String> update(@RequestBody SetmealDto setmealDto) {
    setmealService.updateById(setmealDto);

    LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
    setmealDishService.remove(wrapper);

    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream().map(item -> {
      item.setSetmealId(setmealDto.getId());
      return item;
    }).collect(Collectors.toList());

    setmealDishService.saveBatch(setmealDishes);

    return R.success("修改成功");
  }

  @GetMapping("/list")
  public R<List<Setmeal>> list(Long categoryId, int status) {

    LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Setmeal::getCategoryId, categoryId);
    wrapper.eq(Setmeal::getStatus, status);
    List<Setmeal> setmeals = setmealService.list(wrapper);

    return R.success(setmeals);
  }
}
