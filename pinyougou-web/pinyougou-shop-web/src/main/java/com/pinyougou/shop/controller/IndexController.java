package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @RequestMapping("/showLoginName")
    @ResponseBody
    public Map<String,Object> showLoginName(){
        SecurityContext context = SecurityContextHolder.getContext();
        String loginName = context.getAuthentication().getName();
        System.out.println(loginName);
        Map<String,Object> map = new HashMap<>();
        map.put("loginName",loginName);
        return map;
    }


    @RequestMapping("/login")
    public String login (String username, String password, String code, HttpServletRequest request){
        try {
            if ("post".equalsIgnoreCase(request.getMethod())){
             String oldCode =(String) request.getSession().getAttribute(VerifyController.VERIFY_CODE);
                System.out.println(oldCode);
             if (code.equalsIgnoreCase(oldCode)){
                 UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password);
                 Authentication authenticate = authenticationManager.authenticate(token);
                 if (authenticate.isAuthenticated()){
                     SecurityContextHolder.getContext().setAuthentication(authenticate);
                     return "redirect:/admin/index.html";
                 }
              }
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        return "redirect:/shoplogin.html";
    }
}
