package com.zijun.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zijun.reggie.common.R;
import com.zijun.reggie.dto.DishDto;
import com.zijun.reggie.entity.Dish;
import com.zijun.reggie.entity.DishFlavor;
import com.zijun.reggie.mapper.DishMapper;
import com.zijun.reggie.service.DishFlavorService;
import com.zijun.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

  @Autowired
  private DishFlavorService dishFlavorService ;

  @Override
  @Transactional
  public void saveWithFlavor(DishDto dishDto) {
    this.save(dishDto);   // MyBatisPlus 会主键回填，其实就是先生成主键给对象，然后再保存对象，所以保存后的dto对象有dishId

    List<DishFlavor> flavors = dishDto.getFlavors().stream().map(item -> {
      item.setDishId(dishDto.getId());
      return item;
    }).collect(Collectors.toList());

    dishFlavorService.saveBatch(flavors);
  }

  @Override
  public void updateWithFlavor(DishDto dishDto) {
    // 更新菜品信息
    this.updateById(dishDto);

    /**
     * 更新菜品口味信息：
     *    1.删除旧的所有口味
     *    2.添加新的所有口味
     */
    LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(DishFlavor::getDishId, dishDto.getId());
    dishFlavorService.remove(wrapper);

    List<DishFlavor> flavors = dishDto.getFlavors().stream().map(item -> {
      item.setDishId(dishDto.getId());
      return item;
    }).collect(Collectors.toList());

    dishFlavorService.saveBatch(flavors);
  }
}
