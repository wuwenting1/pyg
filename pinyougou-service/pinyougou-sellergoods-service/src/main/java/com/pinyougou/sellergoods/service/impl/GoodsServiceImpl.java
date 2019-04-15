package com.pinyougou.sellergoods.service.impl;

import java.util.*;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;

@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Override
    public void save(Goods goods) {
        try {
            // 设置未申核状态
            goods.setAuditStatus("0");
            goodsMapper.insertSelective(goods);
            // 为商品描述对象设置主键id
            goods.getGoodsDesc().setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goods.getGoodsDesc());
            //启用规格
            if (goods.getIsEnableSpec().equals("1")){
            /** 迭代所有的SKU具体商品集合，往SKU表插入数据 */
            for (Item item : goods.getItems()) {
                //定义SKU商品的标题
                StringBuilder title = new StringBuilder();
                title.append(goods.getGoodsName());
                //把规格选项JSON字符串转化成Map集合
                Map<String,Object> spec = JSON.parseObject(item.getSpec());
                for (Object value : spec.values()) {
                    //拼接规格选项到SKU商品标题
                    title.append(" " + value);
                }
                //设置SKU商品的标题
                item.setTitle(title.toString());
                /** 设置SKU商品其它属性 */
                setItemInfo(item, goods);
                itemMapper.insertSelective(item);
            }
      }else {//不启用规格
                //创建新的item
                Item item = new Item();
                //设置标题
                item.setTitle(goods.getGoodsName());
                item.setPrice(goods.getPrice());
                item.setIsDefault("1");
                item.setNum(9999);
                item.setStatus("0");
                item.setSpec("{ }");
                //设置其他属性
                setItemInfo(item,goods);
                itemMapper.insertSelective(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void setItemInfo(Item item, Goods goods) {
        //设置SKU商品图片地址 ;取第一张图片
        List<Map> list = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (list != null && list.size() > 0){
            item.setImage((String) list.get(0).get("url"));
        }
        /*设置SKU商品的分类(三级分类);设置SKU商品的创建时间;设置SKU商品的修改时间 */
        item.setCategoryid(goods.getCategory3Id());
        item.setCreateTime(new Date());
        item.setUpdateTime(item.getCreateTime());
        /*设置SPU商品的编号;设置商家编号;设置商品分类名称;设置商家店铺名称 */
        item.setGoodsId(goods.getId());
        item.setSellerId(goods.getSellerId());
        item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName());
        item.setBrand(brandMapper.selectByPrimaryKey(goods.getBrandId()).getName());
        item.setSeller(sellerMapper.selectByPrimaryKey(goods.getSellerId()).getNickName());
    }

    @Override
    public void update(Goods goods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Goods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Goods> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Goods goods, int page, int rows) {
        try{
            PageInfo<Map<String,Object>> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    goodsMapper.findAll(goods);
                }
            });
            for (Map<String, Object> map : pageInfo.getList()) {
                ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");
                ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                map.put("category2Name", itemCat1 != null ? itemCat2.getName() : "");
                ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                map.put("category3Name", itemCat1 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateStatus(String columnName, Long[] ids, String status) {
        try {
            goodsMapper.updateStatus(columnName,ids,status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getGoods(Long goodsId) {
        try {
            Map<String,Object> dataModel = new HashMap<>();
            //根据主键id查询goods
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            //根据主键id查询goodsDesc
            GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            dataModel.put("goodsDesc",goodsDesc);
            //查询item
            /** 查询SKU数据 */
            Example example = new Example(Item.class) ;
            /** 查询条件对象 */
            Example.Criteria criteria = example.createCriteria();
            /** 状态码为：1 */
            criteria.andEqualTo("status", "1");
            /** 条件: SPU ID */
            criteria.andEqualTo("goodsId", goodsId);
            /** 按是否默认降序(保证第一个为默认) */
            example.orderBy("isDefault").desc();
            /** 根据条件查询SKU商品数据 */
            List<Item> itemList = itemMapper.selectByExample(example);
            //需要将itemList转为Json字符串
            dataModel.put("itemList",JSON.toJSONString(itemList));
            //查询商品分类
            //一级分类
            ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            dataModel.put("itemCat1",itemCat1);
            //二级分类
            ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            dataModel.put("itemCat2",itemCat2);
            //三级分类
            ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
            dataModel.put("itemCat3",itemCat3);
            return dataModel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Item> findItemByGoodsId(Long[] goodsIds){
        Example example = new Example(Item.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("goodsId", Arrays.asList(goodsIds));
        return itemMapper.selectByExample(example);
    }
}
