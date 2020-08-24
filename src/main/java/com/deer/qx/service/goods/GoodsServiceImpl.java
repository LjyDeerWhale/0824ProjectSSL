package com.deer.qx.service.goods;

import com.deer.qx.dao.goods.GoodsDao;
import com.deer.qx.model.goods.Goods_info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public List<Goods_info> selectAll() {
        return goodsDao.selectAll();
    }
}