package com.zijun.reggie.utils;

import org.apache.commons.lang.RandomStringUtils;

public class ValidateCodeGenerator {

  public static String generateCode(int length) {

    String code = RandomStringUtils.randomAlphanumeric(length);

    return code.toUpperCase();
  }

}
