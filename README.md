# jdcrawler
只负责爬取京东的手机信息，没有回显，用来学习爬虫技术，入门学习

---

#  技术栈
使用Spring Boot+Spring Data JPA和定时任务进行开发
采用了httpclient和Jsoup进行爬取和解析数据

---

# 本地环境
Ieda
jdk1.8
maven

---

# 用法
## 需要创建一张数据库表，用来存放爬取回来的数据
```sql
CREATE TABLE `jd_item` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `spu` bigint(15) DEFAULT NULL COMMENT '商品集合id',
  `sku` bigint(15) DEFAULT NULL COMMENT '商品最小品类单元id',
  `title` varchar(100) DEFAULT NULL COMMENT '商品标题',
  `price` bigint(10) DEFAULT NULL COMMENT '商品价格',
  `pic` varchar(200) DEFAULT NULL COMMENT '商品图片',
  `url` varchar(200) DEFAULT NULL COMMENT '商品详情地址',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `sku` (`sku`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='京东商品表';
```
## 需要修改为自己的数据库
```java
#DB Configuration:
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/crawler
spring.datasource.username=root
spring.datasource.password=root
```
## Idea需要添加Lombok插件才能运行起来
因为使用了Lombok消除样板式代码和简化了生成log的代码
打开Settings-Plugins，搜索Lombok安装后重启才有效

---

# 联系我，相互探讨
Email: 745808741@qq.com
