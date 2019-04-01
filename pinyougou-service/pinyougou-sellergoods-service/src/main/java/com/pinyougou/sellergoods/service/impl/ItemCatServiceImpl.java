package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.ItemCatService")
@Transactional
public class ItemCatServiceImpl implements ItemCatService{
    @Autowired
    private ItemCatMapper itemCatMapper;

    @Override
    public void save(ItemCat itemCat) {
        try {
            itemCatMapper.insertSelective(itemCat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ItemCat itemCat) {
        try {
            itemCatMapper.updateByPrimaryKeySelective(itemCat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Long[] ids) {
        try{
            /** 定义List集合 */
            List<Long> idLists = new ArrayList<>();
            for (Long id : ids){
                /** 添加id */
                idLists.add(id);
                /** 递归查询 */
                findLeafNode(id, idLists);
            }
            /** 批量删除商品类目 */
            itemCatMapper.deleteById(idLists);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    private void findLeafNode(Long id, List<Long> idLists) {
        List<ItemCat> itemCatLists = itemCatMapper.findItemCatByParentId(id);
        if (itemCatLists != null && itemCatLists.size() > 0) {
            for (ItemCat itemCat : itemCatLists) {
                idLists.add(itemCat.getId());
                findLeafNode(itemCat.getId(), idLists);
            }
        }
    }
    @Override
    public ItemCat findOne(Serializable id) {
        return null;
    }

    @Override
    public List<ItemCat> findAll() {
        return null;
    }

    @Override
    public List<ItemCat> findByPage(ItemCat itemCat, int page, int rows) {
        return null;
    }

    @Override
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        return itemCatMapper.findItemCatByParentId(parentId);
    }
}
