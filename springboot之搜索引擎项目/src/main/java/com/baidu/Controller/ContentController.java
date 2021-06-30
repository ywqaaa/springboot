package com.baidu.Controller;

import com.baidu.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    //用于爬取数据存进数据库
    @GetMapping("/parse/{keyword}")
    @ResponseBody
    public boolean parse(@PathVariable("keyword") String keywords) throws Exception {
        return contentService.parseContent(keywords);
    }

    //获取这些数据实现搜索功能，创建用于前端搜索的API数据接口，格式为json格式
    @GetMapping({"/search/{keyword}/{pageNo}/{pageSize}"})
    @CrossOrigin
    @ResponseBody
    public List<Map<String,Object>> search(@PathVariable("keyword")String keyword,
                                           @PathVariable("pageNo")int pageNo,
                                           @PathVariable("pageSize")int pageSiz) throws IOException {
        return contentService.searchPage(keyword,pageNo,pageSiz);


    }

}
