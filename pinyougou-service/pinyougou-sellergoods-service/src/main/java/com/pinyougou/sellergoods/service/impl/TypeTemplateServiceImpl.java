package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;
    @Override
    public void save(TypeTemplate typeTemplate) {
       try{
          typeTemplateMapper.insertSelective(typeTemplate);
       }catch (Exception ex){
           throw new RuntimeException(ex);
       }
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            typeTemplateMapper.deleteAll(ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TypeTemplate findOne(Serializable id) {
        try {
            return typeTemplateMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        try{
            PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    typeTemplateMapper.findAll(typeTemplate);
                }
            });
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Map<String, Object>> findTypeTemplateList() {
        try {
            return typeTemplateMapper.findTypeTemplateList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map> findSpecByTemplateId(Long id) {
        try {
            TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
            String specIds = typeTemplate.getSpecIds();
            /**
             * [{"id":33,"text":"电视屏幕尺寸"}]
             * 获取模版中所有的规格，转化成  List<Map>
             */
            List<Map> mapList = JSON.parseArray(specIds, Map.class);
            for (Map map : mapList) {
               Long specId = Long.valueOf(map.get("id").toString());
               List<SpecificationOption> options = specificationOptionMapper.findSpecOptionBySpecId(specId);
               map.put("options",options);
            }
            return mapList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
