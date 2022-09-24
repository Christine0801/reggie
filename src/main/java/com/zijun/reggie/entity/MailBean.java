package com.zijun.reggie.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MailBean implements Serializable {

  private String recipient ;    // 收件人邮箱
  private String subject ;      // 邮件主题
  private String content ;      // 邮件内容

}

