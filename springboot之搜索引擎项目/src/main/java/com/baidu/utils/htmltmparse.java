package com.baidu.utils;

import com.baidu.pojo.Product;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;

@Component
public class htmltmparse{

    public static void main(String[] args) throws Exception {
        new htmltmparse().parsebk("page_34.html").forEach(System.out::println);
    }
    public ArrayList<Product> parsebk(String keywords) throws Exception {
        //获取请求
        String url = "https://www.niefengjun.cn/"+keywords;
        HttpGet httpGet = new HttpGet(url);
        // 设置请求信息
        //伪装浏览器
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");


        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);
        //System.out.println(document.html());

        Elements elements=document.getElementsByClass("post");
        //System.out.println(elements);

        ArrayList<Product> goodList1=new ArrayList<>();
        for (Element el:elements){
            String title=el.getElementsByClass("post_title").eq(0).text();
            String content=el.getElementsByClass("post_content").text();
            String read=el.getElementsByClass("post_sub").eq(1).text();
            String link=el.getElementsByTag("a").attr("href");
            Product product =new Product();
            product.setTitle(title);
            product.setContent(content);
            product.setRead(read);
            product.setLink("https://www.niefengjun.cn"+link);
            goodList1.add(product);

        }
        return goodList1;

    }
}
