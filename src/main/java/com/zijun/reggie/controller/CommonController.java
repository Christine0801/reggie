package com.zijun.reggie.controller;

import com.zijun.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

  @Value("${reggie.path}")
  private String basePath;

  @PostMapping("/upload")
  public R<String> upload(MultipartFile file) {

    // 获取文件原文件名
    String originalFilename = file.getOriginalFilename();
    // 截取文件后缀名
    String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

    String fileName = UUID.randomUUID().toString() + suffix;

    File dir = new File(basePath);
    // 如果文件夹不存在则创建文件夹
    if (!dir.exists()) {
      dir.mkdir();
    }

    try {
      // 将文件转存在指定位置
      file.transferTo(new File(basePath + fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    // 将文件名返回出去
    return R.success(fileName);

  }

  @GetMapping("/download")
  public void download(String name, HttpServletResponse response) {
    try {
      FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

      int len = 0;
      byte[] bytes = new byte[1024];
      ServletOutputStream outputStream = response.getOutputStream();

      while ((len = fileInputStream.read(bytes)) != -1) {
        outputStream.write(bytes, 0, len);
        outputStream.flush();
      }

      // 释放资源
      fileInputStream.close();
      outputStream.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
