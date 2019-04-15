package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {
   @Reference(timeout = 10000)
   private UserService userService;
   @PostMapping("/save")
    public boolean save(@RequestBody User user,String code){
       try {
           boolean ok = userService.checkSmsCode(user.getPhone(),code);
           if(ok) {
               user.setCreated(new Date());
               user.setUpdated(user.getCreated());
               userService.save(user);
           }
           return true;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
   }
   @GetMapping("/sendSms")
    public boolean sendSms(String phone){
       try {
           userService.sendSms(phone);
           return true;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
   }
}
