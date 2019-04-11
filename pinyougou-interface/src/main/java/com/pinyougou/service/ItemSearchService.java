package com.pinyougou.service;
import java.util.Map; /**
 *商品搜索服务接口
 * @date 2019-03-28 16:27:34
 * @version 1.0
 */
public interface ItemSearchService {

    Map<String,Object> search(Map<String, Object> params);
}
