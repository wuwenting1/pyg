package com.pinyougou.search.service.impl;
/**
 * 搜索商品的实现类
 *
 * */
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        //定义map封装返回的数据
        Map<String,Object> data = new HashMap<>();

        //判断是否有keywords
        String keywords = (String) params.get("keywords");

        Integer page = (Integer) params.get("page");
        if (page == null){
            page = 1;
        }
        Integer rows = (Integer) params.get("rows");
        if (rows == null){
            rows = 10;
        }

        if (StringUtils.isNoneBlank(keywords)){//高亮查询
            //创建高亮查询的对象
            HighlightQuery highlightQuery = new SimpleHighlightQuery();
            //创建高亮查询的选项对象
            HighlightOptions highlightOptions = new HighlightOptions();
            //设置高亮的域
            highlightOptions.addField("title");
            //设置高亮的前缀
            highlightOptions.setSimplePrefix("<font color='red'>");
            //设置高亮的后置
            highlightOptions.setSimplePostfix("</font>");
            //设置高亮选项
            highlightQuery.setHighlightOptions(highlightOptions);
            //创建查询的条件对象
            Criteria criteria = new Criteria("keywords").is(keywords);
            highlightQuery.addCriteria(criteria);
            /** 过滤查询 */
            //分类过滤查询
            String category = (String) params.get("category");
            //判断是否为空
            if(StringUtils.isNoneBlank(category)){
                //创建过滤查询对象
                Criteria criteria1 = new Criteria("category").is(category);
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
            //品牌过滤查询
            String brand = (String) params.get("brand");
            //判断是否为空
            if(StringUtils.isNoneBlank(brand)){
                //创建过滤查询对象
                Criteria criteria1 = new Criteria("brand").is(brand);
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
            //规格过滤查询
            Map<String,String> specMap = (Map<String,String>) params.get("spec");
            //判断是否为空
            if(specMap != null && specMap.size() > 0){
                //循环获取map集合中的key
                for (String key : specMap.keySet()) {
                    //创建过滤查询对象
                    Criteria criteria1 = new Criteria("spec_"+key).is(specMap.get(key));
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
            }
            //价格过滤查询
            String price = (String) params.get("price");
            //判断是否为空
            if(StringUtils.isNoneBlank(price)){
                //需要判断0-500 ， 1000-2000 ，3000-*
                String[] priceArr = price.split("-");
                //起始值为0时
                if (!priceArr[0].equals("0")){
                    //创建过滤查询对象
                    Criteria criteria1 = new Criteria("price").greaterThanEqual(priceArr[0]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                //最后一个为星号的*
                if (!priceArr[1].equals("*")){
                    //创建过滤查询对象
                    Criteria criteria1 = new Criteria("price").lessThanEqual(priceArr[1]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
            }
            /** 设置查询结果分页 */
            highlightQuery.setOffset((page - 1) * rows);
            highlightQuery.setRows(rows);
            /** 排序查询 */
            String sortValue = (String) params.get("sort");
            String sortField = (String) params.get("sortField");
            if (StringUtils.isNoneBlank(sortValue) && StringUtils.isNoneBlank(sortField)){
                //创建排序对象
               Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ? Sort.Direction.ASC : Sort.Direction.DESC,sortField);
               highlightQuery.addSort(sort);
            }
            /** 分页查询，得到高亮分页查询对象 */
            HighlightPage<SolrItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery,SolrItem.class);
            /** 循环高亮项集合 */
            for (HighlightEntry<SolrItem> he : highlightPage.getHighlighted()) {
                //获取检索的原实体
                SolrItem solrItem = he.getEntity();
                /** 判断高亮集合及集合中第一个Field的高亮内容 */
                if (he.getHighlights() != null && he.getHighlights().size() > 0){
                    /** 设置高亮的结果 */
                    solrItem.setTitle(he.getHighlights().get(0).getSnipplets().get(0));
                }
            }
            //获取分页的总页数
             data.put("totalPages",highlightPage.getTotalPages());
            //获取分页的总记录数
            data.put("total",highlightPage.getTotalElements());
            //获取分页的内容数据
            data.put("rows", highlightPage.getContent());
        }else {//简单查询
            /** 设置查询结果分页 */
            Query query = new SimpleQuery("*:*");

            query.setOffset((page - 1) * rows);
            query.setRows(rows);
            ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
            //获取分页的总页数
            data.put("totalPages",scoredPage.getTotalPages());
            //获取分页的总记录数
            data.put("total",scoredPage.getTotalElements());
            //获取分页的内容数据
            data.put("rows", scoredPage.getContent());
        }
        return data;
    }

    /**
     * 添加或修改索引
     * 通过消息文件服务器实现
     * @param solrItems
     */
    public  void saveOrUpdate(List<SolrItem> solrItems){
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }
    /**
     * 删除索引
     * 通过消息文件服务器实现
     * @param
     */
    public void delete(List<Long> goodsIds){
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("goodsId").is(goodsIds);
        query.addCriteria(criteria);
        UpdateResponse updateResponse = solrTemplate.delete(query);
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }
}
