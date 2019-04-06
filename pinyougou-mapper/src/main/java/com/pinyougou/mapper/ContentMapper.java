package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Content;

import java.util.List;

/**
 * ContentMapper 数据访问接口
 * @date 2019-03-28 16:29:14
 * @version 1.0
 */
public interface ContentMapper extends Mapper<Content>{

    @Select("select * from tb_content where status = #{status} and category_id = #{categoryId} order by sort_order")
    List<Content> findContextByCategoryId(@Param("categoryId") Long categoryId, @Param("status")String status);
}