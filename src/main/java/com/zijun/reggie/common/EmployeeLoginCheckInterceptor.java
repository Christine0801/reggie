package com.zijun.reggie.common;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zijun.reggie.entity.Employee;
import com.zijun.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class EmployeeLoginCheckInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  private EmployeeService employeeService ;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate ;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    Integer employeeId = (Integer) redisTemplate.opsForValue().get("employee");
    String json = "";
    if (employeeId != null) {
      LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
      wrapper.eq(Employee::getId, employeeId.longValue());
      Employee employee = employeeService.getOne(wrapper);

      if (employee == null) {
        json = JSONObject.toJSONString(R.error("NOTLOGIN"));
        response(response, json);
        return false;
      }
    } else {
      json = JSONObject.toJSONString(R.error("NOTLOGIN"));
      response(response, json);
      return false;
    }

    return true;
  }


  public void response(HttpServletResponse response, String json) {

    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=utf-8");

    PrintWriter writer = null;

    try {
      writer = response.getWriter();
      writer.write(json);
    }catch (IOException ex) {
      log.error("写回json失败");
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
}
