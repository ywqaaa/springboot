package com.baidu.utils;

import com.baidu.pojo.Content;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;

@Component
public class HtmlParseUtil {

    public static void main(String[] args) throws Exception {
        new HtmlParseUtil().parseJD("php").forEach(System.out::println);
    }
    public ArrayList<Content> parseJD(String keywords) throws Exception{
        //获取请求
        String url = "https://search.jd.com/Search?keyword="+keywords;
        HttpGet httpGet = new HttpGet(url);
        // 设置请求信息
        //伪装浏览器
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");


        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);

        //所有在js可以使用的方法，这里都能用
        Element element=document.getElementById("J_goodsList");
        System.out.println(element.html());

        //获取所有的li元素
        Elements elements=element.getElementsByTag("li");
        System.out.println(elements.html());

        ArrayList<Content> goodLists=new ArrayList<>();
        for (Element el:elements){
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price= el.getElementsByClass("p-price").eq(0).text();
            String title=el.getElementsByClass("p-name").eq(0).text();

            Content content=new Content();
            content.setTitle(title);
            content.setPrice(price);
            content.setImg("https:"+img);
            goodLists.add(content);
        }
        return goodLists;
    }
}
