package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    //注入JmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue solrQueue;
    @Autowired
    private ActiveMQQueue solrDeleteQueue;
    @Autowired
    private ActiveMQTopic pageTopic;
    @Autowired
    private ActiveMQTopic pageDeleteTopic;
    /**
     * 添加商品
     * @param goods
     * @return
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Goods goods){
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String username = context.getAuthentication().getName();
            goods.setSellerId(username);
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 分页查询
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods,Integer page,Integer rows){

            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);
        try{
            if (goods.getGoodsName() != null && StringUtils.isNoneBlank(goods.getGoodsName())){
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return goodsService.findByPage(goods,page,rows);
    }

    /**
     * 是否上架
     * @param ids
     * @param status
     * @return
     */
    @GetMapping("/updateMarketable")
    public boolean updateMarketable(Long[] ids,String status){
        try {
            goodsService.updateStatus("is_marketable",ids,status);

            if ("1".equals(status)){
                //通过activemq消息服务器来创建solr库索引
                jmsTemplate.send(solrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
                //通过activemq消息服务器来创建html静态页面
                for (Long goodsId : ids) {
                    jmsTemplate.send(pageTopic, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(goodsId.toString());
                        }
                    });
                }
            }else {
                //删除solr商品
                jmsTemplate.send(solrDeleteQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
                //删除商品详情的html页面
                jmsTemplate.send(pageDeleteTopic, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
