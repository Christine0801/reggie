package com.zijun.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zijun.reggie.dto.SetmealDto;
import com.zijun.reggie.entity.Setmeal;
import com.zijun.reggie.mapper.SetmealMapper;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

  void saveWithDish(SetmealDto setmealDto);

  void delete(List<Long> list);
}
