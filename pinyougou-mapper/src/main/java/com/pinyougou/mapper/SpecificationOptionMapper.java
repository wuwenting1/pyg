package com.pinyougou.mapper;

import com.pinyougou.pojo.Specification;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.SpecificationOption;

import java.util.List;

/**
 * SpecificationOptionMapper 数据访问接口
 * @date 2019-03-28 16:29:14
 * @version 1.0
 */
public interface SpecificationOptionMapper extends Mapper<SpecificationOption>{
    void save(Specification specification);
    @Select("select * FROM tb_specification_option where spec_id = #{specId}")
    List<SpecificationOption> findSpecOptionBySpecId(Long specId);
}