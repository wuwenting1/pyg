package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SellerMapper;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Service(interfaceName = "com.pinyougou.service.SellerService")
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerMapper sellerMapper;
    @Override
    public void save(Seller seller) {
        try {
            seller.setCreateTime(new Date());
            seller.setStatus("0");
            sellerMapper.insertSelective(seller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Seller seller) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Seller findOne(Serializable id) {
        return sellerMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Seller> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Seller seller, int page, int rows) {
        PageInfo<Seller> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                sellerMapper.findAll(seller);
            }
        });
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public void updateStatus(String sellerId,String status) {
        try {
            sellerMapper.updateStatus(sellerId,status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
