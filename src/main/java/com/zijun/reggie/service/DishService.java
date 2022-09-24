package com.zijun.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zijun.reggie.dto.DishDto;
import com.zijun.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

  void saveWithFlavor(DishDto dishDto);

  void updateWithFlavor(DishDto dishDto);
}
