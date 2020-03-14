package com.jdcrawler.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcrawler.pojo.Item;
import com.jdcrawler.service.ItemService;
import com.jdcrawler.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@Log4j2
@Component
public class ItemTask {

    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private ItemService itemService;

    public static final ObjectMapper MAPPER = new ObjectMapper();

    //当下载任务完成后，间隔多久再执行
    @Scheduled(fixedDelay = 100 * 1000)
    public void itemTask() throws Exception {
        //声明需要解析的初始地址
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&s=61&click=0&page=";

        //按照页面对手机的搜索结果进行遍历解析
        for (int i = 1; i < 10; i = i + 2) {
            //循环只要为了遍历分页数据,手动拼接页数
            String html = httpUtils.getHtml(url + i);
            this.parse(html);
            log.info(String.format("第%d页数据抓取完毕", i));
        }
        log.info("数据抓取完毕");
    }

    //解析页面,获取商品数据并存储
    private void parse(String html) throws Exception {
        //使用jsoup解析页面
        Document document = Jsoup.parse(html);
        //获取商品数据
        Elements spus = document.select("div#J_goodsList > ul > li");
        //遍历商品spu数据
        for (Element spuEle : spus) {
            //获取商品spu
            Long spu = Long.parseLong(spuEle.attr("data-spu"));
            Elements skus = spuEle.select("li.ps-item img");
            for (Element skuEle : skus) {
                //获取商品sku
                long sku = Long.parseLong(skuEle.attr("data-sku"));

                //判断商品是否被抓取过，可以根据sku判断
                Item item = new Item();
                //商品sku
                item.setSku(sku);
                List<Item> list = this.itemService.findAll(item);
                //判断是否查询到结果
                if (list.size() > 0) {
                    //如果有结果，表示商品已爬取过，进行下一个
                    continue;
                }

                //保存商品数据
                //商品spu
                item.setSpu(spu);
                //商品url地址
                item.setUrl(String.format("https://item.jd.com/%d.html", sku));
                //创建时间
                item.setCreated(new Date());
                //修改时间
                item.setUpdated(new Date());
                //获取商品标题
                String itemHtml = this.httpUtils.getHtml(item.getUrl());
                item.setTitle(Jsoup.parse(itemHtml).select("div.sku-name").text());
                //获取商品价格
                String itemPriceHtml = this.httpUtils.getHtml(String.format("https://p.3.cn/prices/mgets?skuIds=J_%d", sku));
                //解析json数据获取商品价格
                //得到的价格扩大100倍，防止小数的出现
                double price = MAPPER.readTree(itemPriceHtml).get(0).get("p").asDouble() * 100;
                item.setPrice(price);
                //获取图片地址
                String attr = skuEle.attr("data-lazy-img");
                if (StringUtils.isBlank(attr)) {
                    attr = skuEle.attr("data-lazy-img-slave");
                }
                String pic = String.format("https:%s", attr.replace("/n9/", "/n1/"));
                //下载图片
                String picName = this.httpUtils.getImage(pic);
                item.setPic(picName);
                //保存商品数据
                this.itemService.save(item);
            }
        }
    }
}
