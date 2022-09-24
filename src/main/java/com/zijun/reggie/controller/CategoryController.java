package com.zijun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zijun.reggie.common.R;
import com.zijun.reggie.entity.Category;
import com.zijun.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@ResponseBody
@RequestMapping("/category")
public class CategoryController {

  @Autowired
  private CategoryService categoryService ;

  @PostMapping
  public R<String> save(@RequestBody Category category) {
    categoryService.save(category);

    return R.success("新增成功");
  }

  @GetMapping("/page")
  public R<Page> page(int page, int pageSize) {

    Page<Category> pageInfo = new Page(page, pageSize);

    LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
    wrapper.orderByAsc(Category::getSort);

    categoryService.page(pageInfo, wrapper);

    return R.success(pageInfo);
  }

  @DeleteMapping
  public R<String> delete(Long ids) {

    categoryService.remove(ids);

    return R.success("删除成功");
  }

  @PutMapping
  public R<String> update(@RequestBody Category category) {

    categoryService.updateById(category);

    return R.success("修改成功");
  }

  @GetMapping("/list")
  public R<List<Category>> list(String type) {

    LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(StringUtils.isNotBlank(type), Category::getType, type);
    wrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);

    List<Category> list = categoryService.list(wrapper);

    return R.success(list);
  }

}
