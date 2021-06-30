# springboot
项目
本项目是由springboot+elasticsearch搭建的整体搜索引擎服务项目，目前已经支持中文分词，检索关键词高亮操作，搜索效率高。Elasticsearch是一个开源的、RESTful风格的搜索和数据引擎。
本项目结构采用目前流行的前后端分离架构，后端采用java的springboot框架，前端采用vue框架，后端提供api接口，前端用axios（ajax）技术实现获取后端api接口数据并渲染到前端界面
获取数据：启动后端爬虫api接口 浏览器输入http://127.0.0.1:9090/parse/{keyword}
启动项目：前提，启动elasticsearch，然后启动DemoApplication.java文件,最后浏览器打开http://127.0.0.1:9090/就完成了
