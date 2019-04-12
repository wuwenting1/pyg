package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 商品详情控制器
 */
@Controller
public class ItemController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    /**
     * 查询商品详情
     */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId")Long goodsId, Model model){
        //System.out.println("goodsId:" + goodsId);
        Map<String,Object> dataModel = goodsService.getGoods(goodsId);
        model.addAllAttributes(dataModel);
        //model.addAttribute("name","张三");
        return "item";
    }
}
