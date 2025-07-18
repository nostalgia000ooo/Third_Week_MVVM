### Week Third

#### 学习MVVM架构并使用Room数据库

- 梳理MVVM模型数据处理逻辑
- 引入databinding livedata viewmodel包
- 实现MVVM的静态交互功能
- 引入Room组件利用注解与数据库交互

###### 待办事项管理demo
1. 分为Model、Repository、ViewModel、View（activity）进行数据交互
2. 在Model中使用@Entity定义实体类
3. 引入数据库后新建DataBase和Dao文件，设计增删改查相关数据库操作 
<br>**demo较为简单，没有添加修改功能**</br>
4. 在过程中发现了LiveData与suspend fun 不兼容的问题，已完成修改
5. 之后发现数据无法存入数据库，debug发现是android规定的主线程不允许与数据库交互防止ui被阻塞
6. 解决方案：使用子线程交互/database添加.allowMainThreadQueries()标签
    
###### ** 演示视频 **
- ![image](/res/database.png)
- 最新页面交互视频
- <video id="video" controls="" preload="none" poster="页面交互">
      <source id="mp4" src="res/new_demo.mp4" type="video/mp4">
</video>