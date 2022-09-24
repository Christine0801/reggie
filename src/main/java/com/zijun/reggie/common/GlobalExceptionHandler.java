package com.zijun.reggie.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

  /**
   * 完整性约束异常，一般是碰到了唯一约束，外键约束...等等情况导致插入数据失败
   * @return
   */
  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {

    // 违反了唯一性约束
    if (ex.getMessage().contains("Duplicate entry")) {
      String fieldName = ex.getMessage().split(" ")[2];

      return R.error(fieldName + "已存在");
    }

    return R.error("未知错误");
  }

  @ExceptionHandler(CustomException.class)
  public R<String> exceptionHandler(CustomException ex) {

    return R.error(ex.getMessage());
  }
}
