package com.zijun.reggie.dto;

import com.zijun.reggie.entity.Setmeal;
import com.zijun.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
