package com.jdcrawler.util;

import lombok.extern.log4j.Log4j2;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;
@Log4j2
@Component
public class HttpUtils {
    private PoolingHttpClientConnectionManager cm;

    public HttpUtils() {
        this.cm = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        cm.setMaxTotal(200);
        //设置每个主机的并发量
        cm.setDefaultMaxPerRoute(20);
    }

    //获取内容
    public String getHtml(String url) {
        // 获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        CloseableHttpResponse response = null;
        try {
            // 声明httpGet请求对象
            HttpGet httpGet = new HttpGet(url);
            // 设置请求参数RequestConfig
            httpGet.setConfig(this.getConfig());
            //添加请求头信息
            // 浏览器表示
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
            // 传输的类型
            httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
            // 使用HttpClient发起请求，返回response
            response = httpClient.execute(httpGet);
            // 解析response返回数据
            if (response.getStatusLine().getStatusCode() == 200) {
                if (null != response.getEntity()) {
                    return EntityUtils.toString(response.getEntity());
                }
            }
        } catch (Exception e) {
            log.error(String.format("html url：%s",url));
            log.error("错误异常：",e);
        } finally {
            try {
                if (response != null) {
                    // 关闭连接
                    response.close();
                }
                // 不能关闭，现在使用的是连接管理器
                // httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //获取图片
    public String getImage(String url) {
        // 获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        CloseableHttpResponse response = null;
        try {
            // 声明httpGet请求对象
            HttpGet httpGet = new HttpGet(url);
            // 设置请求参数RequestConfig
            httpGet.setConfig(this.getConfig());
            //添加请求头信息
            // 浏览器表示
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
            // 传输的类型
            httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
            // 使用HttpClient发起请求，返回response
            response = httpClient.execute(httpGet);
            // 解析response返回数据
            if (response.getStatusLine().getStatusCode() == 200) {
                //获取文件类型
                String extName = url.substring(url.lastIndexOf("."));
                //使用uuid生成图片名
                String imageName = UUID.randomUUID().toString() + extName;
                //声明输出的文件
                OutputStream outputStream = new FileOutputStream(new File("images/"+imageName));
                //使用响应体输出文件
                response.getEntity().writeTo(outputStream);
                //返回生成的图片名
                return imageName;
            }
        } catch (Exception e) {
            log.error(String.format("image url：%s",url));
            log.error("错误异常：",e);
        } finally {
            try {
                if (response != null) {
                    // 关闭连接
                    response.close();
                }
                // 不能关闭，现在使用的是连接管理器
                // httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(1000) //设置创建连接的超时时间
                .setConnectionRequestTimeout(500) //设置获取连接的超时时间
                .setSocketTimeout(2000) //设置连接的超时时间
                .build();
        return config;
    }
}
