package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.service.ItemService;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMessageListener implements SessionAwareMessageListener<ObjectMessage>{
    //引入ItemService
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    //引入ItemSearchService
    @Reference(timeout = 10000)
    private ItemSearchService searchService;
    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        Long[] goodsIds = ( Long[])objectMessage.getObject();
        List<Item> itemList = goodsService.findItemByGoodsId(goodsIds);
        System.out.println("=========导入数据==========");
        List<SolrItem> solrItems = new ArrayList<>();
        for (Item item1 : itemList) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setTitle(item1.getTitle());
            solrItem.setPrice(item1.getPrice());
            solrItem.setImage(item1.getImage());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setCategory(item1.getCategory());
            solrItem.setBrand(item1.getBrand());
            solrItem.setSeller(item1.getSeller());
            solrItem.setUpdateTime(item1.getUpdateTime());
            //需要将Json格式的字符串转换成map集合
            Map specMap = JSON.parseObject(item1.getSpec(), Map.class);
            solrItem.setSpecMap(specMap);
            solrItems.add(solrItem);
            //System.out.println(item1.getTitle());
        }
        searchService.saveOrUpdate(solrItems);
    }
}
