package com.baidu;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //创建索引测试
    @Test
    void  contextLoads() throws IOException{
        CreateIndexRequest request=new CreateIndexRequest("text1");

        CreateIndexResponse index =restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(index);
    }

    //获取索引测试
    @Test
    void test2() throws IOException {
        //new一个获取索引请求
        GetIndexRequest getIndexRequest = new GetIndexRequest("text1");
        //客户端执行,看是否存在
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);//true,表示存在
    }
}
