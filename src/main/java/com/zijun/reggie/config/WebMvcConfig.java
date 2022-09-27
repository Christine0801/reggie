package com.zijun.reggie.config;

import com.zijun.reggie.common.EmployeeLoginCheckInterceptor;
import com.zijun.reggie.common.JacksonObjectMapper;
import com.zijun.reggie.common.UserLoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

  @Autowired
  private UserLoginCheckInterceptor userLoginCheckInterceptor ;

  @Autowired
  private EmployeeLoginCheckInterceptor employeeLoginCheckInterceptor ;

  /**
   * 拓展 Mvc 框架的消息转换器，我们需要将Long类型数据转换成字符串，保证前端js处理数据时不丢失精度
   * 因为 js 处理整形数据类型最大长度为16，而Long类型为19位
   * @param converters
   */
  @Override
  protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    // 创建消息转换器对象
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
            new MappingJackson2HttpMessageConverter();
    // 设置对象转换器，底层使用Jackson将Java对象转换为JSON
    mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
    // 将对象转换器添加到mvc框架消息转换器容器中，并且放在最前面
    converters.add(0, mappingJackson2HttpMessageConverter);
  }

  @Override
  protected void addInterceptors(InterceptorRegistry registry) {
    String[] userPath = {
            "/user/current",
            "/order/submit",
            "/order/userPage",
            "/addressBook/**",
            "/shoppingCart/**"
    };
    String[] employeePath = {
            "/employee/**",
//            "/setmeal/**",
            "/dish/**",
            "/category/**"
    };
    registry.addInterceptor(userLoginCheckInterceptor).addPathPatterns(userPath);
    registry.addInterceptor(employeeLoginCheckInterceptor).addPathPatterns(employeePath)
            .excludePathPatterns("/employee/login", "/category/list", "/dish/list");
  }
}
