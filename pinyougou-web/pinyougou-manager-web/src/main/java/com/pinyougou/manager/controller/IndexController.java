package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {
    @RequestMapping("showLoginName")
    public Map<String,Object> showLoginName(){
        SecurityContext context = SecurityContextHolder.getContext();
        String loginName = context.getAuthentication().getName();
        System.out.println(loginName);
        Map<String,Object> map = new HashMap<>();
        map.put("loginName",loginName);
        return map;
    }
}
