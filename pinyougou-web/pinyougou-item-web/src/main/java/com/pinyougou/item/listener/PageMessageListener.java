package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class PageMessageListener implements SessionAwareMessageListener<TextMessage>{
    //引入goodsService
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${page.dir}")
    private String pageDir;
    @Override
    public void onMessage(TextMessage message, Session session) throws JMSException {
        try {
            String goodsId = message.getText();
            // 根据模版文件获取模版对象
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate("item.ftl");
            Map<String, Object> dataModel  = goodsService.getGoods(Long.valueOf(goodsId));
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pageDir+ goodsId + ".html"),"UTF-8");
            template.process(dataModel ,out);
            out.close();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
}
