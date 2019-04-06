package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods,Integer page,Integer rows){
         //设置状态码为0
          goods.setAuditStatus("0");
        try{
            if (goods.getGoodsName() != null && StringUtils.isNoneBlank(goods.getGoodsName())){
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return goodsService.findByPage(goods,page,rows);
    }
    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus("audit_status",ids,status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            goodsService.updateStatus("is_delete", ids,"1");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
