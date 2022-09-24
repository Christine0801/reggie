package com.zijun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zijun.reggie.common.R;
import com.zijun.reggie.entity.Employee;
import com.zijun.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

  @Autowired
  private EmployeeService employeeService ;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate ;

  /**
   * 员工登录
   * @param employee
   * @return
   */
  @PostMapping("/login")
  public R<Employee> login (@RequestBody Employee employee) {
    String password = employee.getPassword();
    password = DigestUtils.md5DigestAsHex(password.getBytes());

    LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Employee::getUsername, employee.getUsername());
    Employee one = employeeService.getOne(wrapper);

    if (one == null) {
      return R.error("用户不存在");
    }
    if (!one.getPassword().equals(password)) {
      return R.error("密码错误");
    }
    if (one.getStatus() == 0) {
      return R.error("该员工账号已禁用");
    }

    // 将当前登录用户ID存入Redis
    redisTemplate.opsForValue().set("employee", one.getId());

    return R.success(one);
  }

  /**
   * 员工退出
   * @return
   */
  @PostMapping("/logout")
  public R<String> logout () {
    // 删除employeeID
    redisTemplate.delete("employee");
    return R.success("success");
  }

  /**
   * 新增员工
   * @param employee
   * @return
   */
  @PostMapping
  public R<String> save( @RequestBody Employee employee) {
    // 从Redis中取出EmployeeID
    /*Integer empId = (Integer) redisTemplate.opsForValue().get("employee");
    employee.setCreateTime(LocalDateTime.now());
    employee.setUpdateTime(LocalDateTime.now());
    employee.setCreateUser(empId.longValue());
    employee.setUpdateUser(empId.longValue());*/

    String password = DigestUtils.md5DigestAsHex("123456".getBytes());
    employee.setPassword(password);

    // 新增用户
    employeeService.save(employee);

    return R.success("添加成功");
  }

  /**
   * 分页查询
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {;
    Page<Employee> pageInfo = new Page(page, pageSize);

    LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(StringUtils.isNotBlank(name), Employee::getName, name);

    employeeService.page(pageInfo, wrapper);

    return R.success(pageInfo);
  }

  /**
   * 更新员工信息
   * @param employee
   * @return
   */
  @PutMapping
  public R<String> update(@RequestBody Employee employee) {
    /*Integer empId = (Integer) redisTemplate.opsForValue().get("employee");
    employee.setUpdateUser(empId.longValue());
    employee.setUpdateTime(LocalDateTime.now());*/

    employeeService.updateById(employee);

    return R.success("更新成功");
  }

  @GetMapping("/{id}")
  public R<Employee> getById(@PathVariable("id") Long id) {

    return R.success(employeeService.getById(id));
  }
}

