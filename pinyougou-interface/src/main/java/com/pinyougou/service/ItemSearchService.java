package com.pinyougou.service;
import com.pinyougou.solr.SolrItem;

import java.util.List;
import java.util.Map; /**
 *商品搜索服务接口
 * @date 2019-03-28 16:27:34
 * @version 1.0
 */
public interface ItemSearchService {

    Map<String,Object> search(Map<String, Object> params);
    // 添加或修改索引
    void saveOrUpdate(List<SolrItem> solrItems);


    void delete(List<Long> goodsId);
}
