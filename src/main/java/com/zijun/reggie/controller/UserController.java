package com.zijun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zijun.reggie.common.CustomException;
import com.zijun.reggie.common.R;
import com.zijun.reggie.entity.User;
import com.zijun.reggie.service.UserService;
import com.zijun.reggie.utils.SendCodeEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private SendCodeEmail sendCodeEmail ;

  @Autowired
  private UserService userService ;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate ;

  @GetMapping("/email/{email:.+}")
  public R<String> getCode(@PathVariable("email") String email) {

    sendCodeEmail.sendCode(email);

    return null;
  }

  @PostMapping("login")
  public R<String> login(@RequestBody Map<String, Object> param) {

    log.info(param.toString());

    // 从redis中获取code，比对用户传来的code
    String code = (String) redisTemplate.opsForValue().get(param.get("email").toString());
    if (!code.equalsIgnoreCase(param.get("code").toString())) {
      throw new CustomException("验证码错误");
    }

    // 校验成功，先判断用户有没有注册，没有则为其注册
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(param.get("email") != null, User::getEmail, param.get("email").toString());
    User user = userService.getOne(wrapper);
    if (user == null) {
      User newUser = new User();
      newUser.setEmail(param.get("phone").toString());
      userService.save(newUser);
      redisTemplate.opsForValue().set("user", newUser.getId(), 1, TimeUnit.HOURS);
    }
    redisTemplate.opsForValue().set("user", user.getId(), 1, TimeUnit.HOURS);

    return R.success("success");
  }

  @PostMapping("/logout")
  public R<String> logout() {

    redisTemplate.delete("user");

    return R.success("退出成功");
  }

  @GetMapping("/current")
  public R<User> getCurrentUser() {

    Integer userId = (Integer) redisTemplate.opsForValue().get("user");

    User user = userService.getById(userId);

    return R.success(user);
  }
}
