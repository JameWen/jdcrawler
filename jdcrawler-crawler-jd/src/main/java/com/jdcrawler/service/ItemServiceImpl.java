package com.jdcrawler.service;

import com.jdcrawler.dao.ItemDao;
import com.jdcrawler.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;

    @Override
    public List<Item> findAll(Item item) {
        Example example = Example.of(item);
        return this.itemDao.findAll(example);
    }

    @Override
    @Transactional
    public void save(Item item) {
        this.itemDao.save(item);
    }
}
