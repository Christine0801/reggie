package com.zijun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zijun.reggie.common.R;
import com.zijun.reggie.dto.DishDto;
import com.zijun.reggie.entity.Category;
import com.zijun.reggie.entity.Dish;
import com.zijun.reggie.entity.DishFlavor;
import com.zijun.reggie.service.CategoryService;
import com.zijun.reggie.service.DishFlavorService;
import com.zijun.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

  @Autowired
  private DishService dishService ;

  @Autowired
  private CategoryService categoryService ;

  @Autowired
  private DishFlavorService dishFlavorService ;

  @PostMapping
  public R<String> save(@RequestBody DishDto dishDto) {

    dishService.saveWithFlavor(dishDto);

    return R.success("新增成功");
  }

  /**
   * 分页
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    Page<Dish> pageInfo = new Page<>(page, pageSize);
    Page<DishDto> dishDtoPage = new Page<>();

    LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Dish::getIsDeleted, 0);
    wrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);

    dishService.page(pageInfo, wrapper);

    BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

    List<DishDto> dtos = pageInfo.getRecords().stream().map(item -> {

      DishDto dishDto = new DishDto();
      BeanUtils.copyProperties(item, dishDto);
      Category category = categoryService.getById(item.getCategoryId());
      dishDto.setCategoryName(category.getName());
      return dishDto;

    }).collect(Collectors.toList());

    dishDtoPage.setRecords(dtos);

    return R.success(dishDtoPage);
  }

  /**
   * 根据id获取菜品
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<DishDto> getById(@PathVariable("id") Long id) {

    LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Dish::getId, id);
    Dish dish = dishService.getOne(wrapper);

    DishDto dishDto = new DishDto();
    BeanUtils.copyProperties(dish, dishDto);
    dishDto.setCategoryName(categoryService.getById(dish.getCategoryId()).getName());
    LambdaQueryWrapper<DishFlavor> wrapper1 = new LambdaQueryWrapper<>();
    wrapper1.eq(DishFlavor::getDishId, dish.getId());
    dishDto.setFlavors(dishFlavorService.list(wrapper1));

    return R.success(dishDto);
  }

  @PutMapping
  public R<String> update(@RequestBody DishDto dishDto) {

    dishService.updateWithFlavor(dishDto);

    return R.success("修改成功");
  }

  /**
   * 停售和批量停售
   * @param status
   * @param ids
   * @return
   */
  @PostMapping("/status/{status}")
  public R<String> discontinued(@PathVariable("status") int status, String ids) {

    String[] idArr = ids.split(",");

    List<Dish> dishes = Arrays.stream(idArr).map(item -> {
      Dish dish = new Dish();
      dish.setId(Long.parseLong(item));
      dish.setStatus(status);
      return dish;
    }).collect(Collectors.toList());

    dishService.updateBatchById(dishes);

    return R.success("修改成功");
  }

  /**
   * 删除或批量删除（软删除）
   * @param ids
   * @return
   */
  @DeleteMapping
  public R<String> logicDelete(String ids) {

    String[] idArr = ids.split(",");

    List<Dish> dishes = Arrays.stream(idArr).map(item -> {
      Dish dish = new Dish();
      dish.setId(Long.parseLong(item));
      dish.setIsDeleted(1);
      return dish;
    }).collect(Collectors.toList());

    dishService.updateBatchById(dishes);

    return R.success("删除成功");
  }

  @GetMapping("/list")
  public R<List<DishDto>> list(Dish dish) {

    LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
    wrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);

    List<Dish> dishes = dishService.list(wrapper);

    List<DishDto> dishDtos = dishes.stream().map(item -> {
      DishDto dishDto = new DishDto();
      BeanUtils.copyProperties(item, dishDto);
      LambdaQueryWrapper<DishFlavor> wrapper1 = new LambdaQueryWrapper<>();
      wrapper1.eq(DishFlavor::getDishId, item.getId());
      List<DishFlavor> list = dishFlavorService.list(wrapper1);
      dishDto.setFlavors(list);
      return dishDto;
    }).collect(Collectors.toList());

    return R.success(dishDtos);
  }
}
