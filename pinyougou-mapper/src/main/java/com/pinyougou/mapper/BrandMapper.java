package com.pinyougou.mapper;

import com.pinyougou.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand>{
    /** 分页查询全部品牌*/
    List<Brand> findAll(Brand brand);

    void deleteAll(Serializable[] ids);
    @Select("SELECT id ,name as text from tb_brand")
    List<Map<String,Object>> findBrandList();
}
