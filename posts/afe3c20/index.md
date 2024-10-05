# Java_layers


&lt;!--more--&gt;


- [[#dao]]
- [[#entity]]
- [[#service]]
- [[#servlet]]
- [[#util]]

&lt;hr&gt;


# dao
&gt;Data Access Object

AKA mapper

1. 数据访问层，
	1. 封装对于数据库的增删查改基本操作
	2. 不涉及业务逻辑，可以理解是工具存放地
	3. 对工具的==具体实现==不在这里
2. 抽象API将application和业务层或持久层/机制分隔
	1. 隐藏底层机制
	2. 使得被分隔的两个层之间无需知道对方的具体实现

一个例子

- User：类
- Dao：接口
- UserDao：实现接口的类


### User

简单定义了一下，name和email以及constructor、getter和setter

```java
package JavaLayersTest;  
  
public class User {  
    private String name;  
    private String email;  
  
    //Constructors  
    public User(String name, String email) {  
        this.name = name;  
        this.email = email;  
    }  
    //getter and setter  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public String getEmail() {  
        return email;  
    }  
    public void setEmail(String email) {  
        this.email = email;  
    }}
```


### Dao

- 一个接口
- 定义了几个方法，没有具体实现

```java
package JavaLayersTest.dao;  
  
import java.util.List;  
import java.util.Optional;  
  
public interface Dao&lt;T&gt; {  
    Optional&lt;T&gt; get(long id);  
    List&lt;T&gt; getAll();  
  
    void save(T t);  
  
    void update(T t, String[] params);  
  
    void delete(T t);  
  
}

```


### UserDao

在Dao的基础上进行针对User类的Dao设计
- 数组实例User类
- 初始化构造函数，增加了两个对象
- 重写Dao的方法

```java
package JavaLayersTest.dao;  
  
import JavaLayersTest.User;  
  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Objects;  
import java.util.Optional;  
  
public class UserDao implements Dao&lt;User&gt;{  
    private List&lt;User&gt; users = new ArrayList&lt;&gt;();  
  
    public UserDao() {  
        users.add(new User(&#34;John&#34;, &#34;john@gmail.com&#34;));  
        users.add(new User(&#34;anna&#34;, &#34;anna@gmail.com&#34;));  
    }  
    @Override  
    public Optional&lt;User&gt; get(long id) {  
        return Optional.ofNullable(users.get((int) id));  
    }  
    @Override  
    public List&lt;User&gt; getAll() {  
        return users;  
    }  
    @Override  
    public void save(User user) {  
        users.add(user);  
    }  
    @Override  
    public void update(User user, String[] params) {  
        user.setName(Objects.requireNonNull(params[0], &#34;name cannot be null&#34;));  
        user.setEmail(Objects.requireNonNull(params[1], &#34;email cannot be null&#34;));  
        users.add(user);  
    }  
    @Override  
    public void delete(User user) {  
        users.remove(user);  
  
    }}
```


### UserApplication



```java
package JavaLayersTest;  
  
import JavaLayersTest.dao.Dao;  
import JavaLayersTest.dao.UserDao;  
  
import java.util.Optional;  
  
public class UApp {
	//Dao是一个接口或父类，UserDao 实现了 Dao 接口或者继承自 Dao 父类。
    //这样，userDao 可以指向任何实现 Dao 接口或继承自 Dao 的类的实例。
    private static Dao userDao;  
  
    public static void main(String[] args) {  
        userDao = new UserDao();  //就在这里实现了继承于Dao的UserDao
  
        User user1 = getUser(0);  
        System.out.println(user1);  
        userDao.update(user1, new String[]{&#34;Jake&#34;, &#34;jake@gamail.com&#34;});  
  
        User user2 = getUser(1);  
        userDao.delete(user2);  
        userDao.save(new User(&#34;Julie&#34;, &#34;julie@gmail.com&#34;));  
  
        userDao.getAll().forEach(user -&gt; System.out.println(user.getName()));  
  
    } 
    //通过id获得user实例 
    private static User getUser(long id) {  
        Optional&lt;User&gt; user = userDao.get(id);  
        return user.orElseGet(() -&gt; new User(&#34;non-existing user&#34;, &#34;no-email&#34;));  
    }}
```


通过上面的例子，
UApp没有和User类直接接触——like我们学的基础编写

而是

`UApp`  &lt;--- `UserDao` ---&gt; `User`
              |
              |
            `Dao`   

这样一个模式
UApp调用UserDao，
User被UserDao调用
UserDao基础设计来源于Dao


最后总结：

接口，使得应用层无需关心具体实现类，反之亦然。


&lt;hr&gt;

# service

- 负责业务逻辑
- 对Dao进行再次封装，封装成一个服务。




&lt;hr&gt;

# entity

- 实体层

有点抽象，例子match better

```java
public class User {  
    private String name;  
    private String email;  
  
    //Constructors  
    public User(String name, String email) {  
        this.name = name;  
        this.email = email;  
    }  
    //getter and setter  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public String getEmail() {  
        return email;  
    }  
    public void setEmail(String email) {  
        this.email = email;  
    }}

```

存放实体类，其中包含其setter和getter之类的。

&lt;hr&gt;



# servlet

感觉写过好多来着。
- 对请求进行处理，与service层进行交互

在notion上有个较为详细的笔记

```java
//web.xml
&lt;!DOCTYPE web-app PUBLIC
 &#34;-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN&#34;
 &#34;http://java.sun.com/dtd/web-app_2_3.dtd&#34; &gt;

&lt;web-app&gt;
  &lt;display-name&gt;Archetype Created Web Application&lt;/display-name&gt;
  &lt;servlet&gt;
    &lt;servlet-name&gt;Servlet1&lt;/servlet-name&gt;
    &lt;servlet-class&gt;Servlet1&lt;/servlet-class&gt;
  &lt;/servlet&gt;
  &lt;servlet-mapping&gt;
    &lt;servlet-name&gt;Servlet1&lt;/servlet-name&gt;
    &lt;url-pattern&gt;/hello&lt;/url-pattern&gt;
  &lt;/servlet-mapping&gt;

&lt;/web-app&gt;


//Servlet1.java
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// 扩展 HttpServlet 类
public class Servlet1 extends HttpServlet {

    private String message;

    public void init() throws ServletException
    {
        // 执行必需的初始化
        message = &#34;Hello World&#34;;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        // 设置响应内容类型
        response.setContentType(&#34;text/html&#34;);

        // 实际的逻辑是在这里
        PrintWriter out = response.getWriter();
        out.println(&#34;&lt;h1&gt;&#34; &#43; message &#43; &#34;&lt;/h1&gt;&#34;);
    }

    public void destroy()
    {
        // 什么也不做
    }
}

```

注意区分filter和servlet。
java式防火墙是filter。



# util

- 是一个多功能、基于工具的层
- 通用的、与业务无关的，可以独立出来，可供其他项目使用




# 一个项目例子

模仿的是[网上商城](https://github.com/ZongXR/SuperMarket.git)的`cart`部分代码编写的










---

> Author:   
> URL: https://66lueflam144.github.io/posts/afe3c20/  

