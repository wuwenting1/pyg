package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;
    @Value("${sms.url}")
    private String url;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void save(User user) {
        try {
            //DigestUtils.md5Hex的方式进行MD5加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }
    //获取短信验证，码的方法
    public boolean sendSms(String phone){
        //动态生成短信验证码
        /** 生成6位随机数 */
        try {
            String code = UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .replaceAll("[a-z|A-Z]","")
                    .substring(0, 6);
            System.out.println("验证码：" + code);
            //"SMS_11480310"
            //调用阿里大于短信接口
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            Map<String,String> params = new HashMap<>();
            params.put("phone",phone);
            params.put("signName",signName);
            params.put("templateCode",templateCode);
            params.put("templateParam","{\"number\":\"" + code + "\"}");
            String content = httpClientUtils.sendPost(url, params);
            // 把json字符串转化成Map
            Map<String,Object> map = JSON.parseObject(content, Map.class);
            /** 存入Redis中(90秒) */
            redisTemplate.boundValueOps(phone).set(code,90, TimeUnit.SECONDS);
            return (boolean) map.get("success");
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
    //判断对象验证码是否正确
    public boolean checkSmsCode(String phone, String code){
       String redisCode = (String) redisTemplate.boundValueOps(phone).get();
       return StringUtils.isNoneBlank(redisCode) && code.equals(redisCode);
    }
   //DigestUtils.md5Hex的方式进行MD5加密
   /* public static void main(String[] args) {
        System.out.println(DigestUtils.md5Hex("1231231"));
    }*/

}
