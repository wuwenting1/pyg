package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    @GetMapping("/user/showName")
    public Map<String,String> showName(){
        /** 获取用户登录名 */
        String loginName = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Map<String, String> map = new HashMap<>();
        map.put("loginName", loginName);
        return map;
    }
}
