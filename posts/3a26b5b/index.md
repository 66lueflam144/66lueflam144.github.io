# Servlet &amp; Listener &amp; Filter from JAVA


对java的重要三组件再次看了一遍。

&lt;!--more--&gt;

ps： `hugo new posts//post_2/S_L_F.md`

# Java三剑客&amp;Tomcat

  &lt;br&gt;

# Servlet

  

&lt;aside&gt;

💡 JavaEE的规范接口之一，运行在服务器上的一个java程序，用来接收客户端发送过来的请求并进行处理，然后响应给客户端。

  

&lt;/aside&gt;

  

- 一点补充

    1.Servlet必须封装在WAR（web应用程序式保存文档）或者Web模组中，才能部署到应用程序式（web）服务器。

    2.由1.可得，Servlet程序由web服务器调用（对其进行生命周期的管理）。当应用加载一个Servlet的时候，从初始化到销毁，期间会发生一系列的事件（Servlet的生命周期事件或方法）。

    3.可以支持动态网页内容，同时对多个用户端提供服务。

  

容器指Tomcat

   &lt;br&gt;

## Servlet的生命周期

  

- Servlet构造器：Servlet的加载

- init初始化：Servlet的实例化，

    - 对于一个Servlet，init方法只能调用一次。无论有多少客户机访问Servlet，都不会重复执行init()。

    - init方法有一个类型为ServletConfig的参数，Servlet容器通过这个参数向Servlet传递配置信息，通过ServletConfig对象获取描述Servlet运行环境的ServletContext对象，使用该对象，Servlet可以和它的Servlet容器进行通信。

- service方法：Servlet的服务

    - Servlet初始化之后，将一直存在于容器中（内存马的基础）

    - 1.客户端发送GET请求，调用Servlet的doGet方法进行处理并响应

    - 2.客户端发送POST请求，调用Servlet的doPost方法处理并响应

    - 3.service方法处理用户请求。

        - 每当由一个对HttpServlet对象的请求，该对象的service方法就调用且获得请求对象ServletRequest和响应对象ServletResponse作为参数。

        - Servlet对象通过ServletRequest对象（得到客户端的相关信息和请求信息，处理之后，调用ServletResponse对象的方法设置响应信息。

- destroy销毁：在web工程停止的时候调用，Servlet的销毁

  

![Untitled](./S_L_Fpics/Untitled.png)

  

一个总结：


1. 在正常情况下，Servlet只会初始化一次，而处理服务会调用多次，销毁也只会调用一次；

2. 但是如果一个Servlet长时间不使用的话，也会被容器自动销毁，

3. 而如果需要再次使用时会**重新进行初始化**的操作，即在特殊情况下*初始化可能会进行多次，销毁也可能进行多次。*

  
 &lt;br&gt;
## Servlet的任务

  

- 接收请求

    - 将请求封装为ServletRequest对象，包含请求头、参数等各种信息

- 处理请求

    - 在service中接收参数，并进行处理

- 数据响应

    - 请求处理完成后，通过**转发（Forward）**或者**重定向（Redirect）**到某个页面

  

![Untitled](./S_L_Fpics/Untitled%201.png)

  
 &lt;br&gt;
## Servlet容器（Tomcat为例）

  

![Untitled](./S_L_Fpics/Untitled%202.png)

  
 &lt;br&gt;
### Tomcat容器4个等级（由高到低）（结合图看）：

  

- Container

- Engine

- Host

- Servlet：包含Context容器（1 or more），一个Context对应一个Web工程，同时Context直接管理Servlet在容器中的包装类Wrapper，so ***Context直接影响Servlet的工作方式。***

  

![Untitled](./S_L_Fpics/Untitled%203.png)

  

（其实目前4张图片，三张都在说同一件事）

   &lt;br&gt;

### 容器的生命周期

  

（和Servlet的生命周期大同小异）

  

1. 加载与实例化：容器启动时，读取web.xml中内容，容器中无实例化Servlet对象，则实例化一个（创建web.xml指定的ServletConfig对象，并将其作为参数来进行下一步的Servlet对象的init方法调用）。

    - ServletConfig对象和Servlet实例的关系：

        - 每个Servlet实例对应一个ServletConfig对象，由容器创建，并进行相关联

        - ServletConfig储存Servlet初始化参数

        - ServletConfig对象可以获取***ServletContext***对象（整个web应用程序的上下文）

            - *ServletContext*是容器给每一个web应用程序创建的一个全局对象（资源）（该程序的所有Servlet都可以访问），提供一系列方法，Servlet用来进行与容器的交互，belike 获取初始化参数、读取资源文件之类的.

2. 初始化：调用Servlet对象的init进行初始化，详情参考上面的Servlet的生命周

3. 处理请求：same

4. 销毁：容器移除一个Servlet就调用其destroy方法（完全残忍）

  
 &lt;br&gt;
## Servlet的线程安全

  

point：

  

- **Problem**：Servlet是单实例多线程的，也就是，一个类一个实例多个线程（访问）

    - 如果存在可以修改的成员变量，就存在多个线程同时访问和修改这些变量，导致数据竞争（数据不一致）、死锁等问题。

- **Answer**：使用Servlet最好无状态

    - 无可修改的成员变量

    - 每次请求都是独立的，不会受到之前的请求的影响

    - 好处是：易于维护

  
 &lt;br&gt;
## 实现

  

客户端通过URL地址访问web服务器中的资源，Servlet程序映射到一个URL地址上

  

在web.xml中的映射实现，例子：

  

```java

  

  &lt;servlet&gt;// 注册Servlet

    &lt;servlet-name&gt;FirstServlet&lt;/servlet-name&gt; //设置Servlet类一个逻辑名称，用于在容器内进行标识

    &lt;servlet-class&gt;FirstServlet&lt;/servlet-class&gt; //Servlet的类名，就是写了实现代码的类的名字

  &lt;/servlet&gt;

  

  &lt;servlet-mapping&gt; //用于映射上面的Servlet的对外访问路径

    &lt;servlet-name&gt;FirstServlet&lt;/servlet-name&gt; //设置引用的Servlet名称，也就是上面那个的名称

    &lt;url-pattern&gt;/FirstServlet&lt;/url-pattern&gt; //servlet对外的访问路径，当访问/FirstServlet的时候，就会将请求转发到指定的Servlet类

  &lt;/servlet-mapping&gt;

  

```

  

ps：转发：客户端只发送一次请求，在服务端进行转发，可以共享数据，浏览器URL不变。
 &lt;br&gt;
  

一个简单的例子：

  

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

  
 &lt;br&gt;
&lt;b&gt;注意一下&lt;/b&gt;，IDEA需要映射URL到这个项目的deploy名

   &lt;br&gt;

belike this is `http://localhost:8080/ServletDemo2_war/hello`

  

然后

  

---

  

# Filter

   &lt;br&gt;

&lt;aside&gt;

💡 请求一个资源或者从一个资源返回信息的时候执行***过滤***操作的中间件。

  

&lt;/aside&gt;

  

详细点is，请求到达Servlet之前进行过滤、响应从Servlet发出后进行过滤。

   &lt;br&gt;

Filter本身并不申请请求对象和响应对象，只提供过滤功能。

  

![filter所在地](./S_L_Fpics/Untitled%204.png)

  
 &lt;br&gt;
## Filter的执行顺序

  

如果定义了多个Filter，且同一个请求会被多个Filter进行处理，那么顺序就来自于web.xml中的顺序，从上到下执行。

  

- Filter接口中有一个doFilter方法，当Filter配置好对哪个资源进行拦截的时候，服务器就会在每次调用该资源之前先调用doFilter方法（执行一段代码）

- 调用doFilter方法的时候，会传递一个filterChain对象（提供一个doFilter方法）

- 如果调用filterChain对象的doFilter方法，那么服务器就会调用web资源的service方法——该资源被访问。（在调用目标资源之前，执行一段代码，与上面的同理）

  
 &lt;br&gt;
## Filter生命周期

  

- 加载和实例化

    - 容器启动时，根据web.xml中的filter声明进行实例化

    - 容器启动时执行，只会执行一次

- 初始化

    - 调用FilterConfig进行初始化filter，类似ServlertConfig

        - 利用FilterConfig可以得到ServletContext对象，以及在web.xml中的filter的初始化参数

    - 容器启动时执行，只会执行一次

- doFilter

    - 类似Servlet的service方法

    - 客户端请求资源，容器匹配到filter-mapping所指url-pa，则按照声明的顺序依次调用这些filter的doFilter方法

- 销毁

    - 容器调用其destroy方法进行销毁，被销毁的filter使用的资源也会被释放

  
 &lt;br&gt;
## 创建Filter

  

IDEA提供了三剑客的模板class，所以直接使用&#43;修改修改

  

```java

//Filter1.java

package com.test.Filters;

import javax.servlet.*;

import java.io.IOException;

import java.util.*;

public class Filter1 implements Filter{

    public void init(FilterConfig config) throws ServletException {

        String site = config.getInitParameter(&#34;site&#34;);

        System.out.println(&#34;name of website: &#34; &#43; site);

    }

  

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        System.out.println(&#34;I got a bad idea...&#34;);

  

        chain.doFilter(request, response);

  

    }

  

    public void destroy(){

        System.out.println(&#34;Destroy!!!&#34;);

    }

  

}

  

//web.xml

&lt;!DOCTYPE web-app PUBLIC

 &#34;-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN&#34;

 &#34;http://java.sun.com/dtd/web-app_2_3.dtd&#34; &gt;

  

&lt;web-app&gt;

  &lt;display-name&gt;Archetype Created Web Application&lt;/display-name&gt;

  &lt;servlet&gt;

    &lt;servlet-name&gt;DisplayHeader&lt;/servlet-name&gt;

    &lt;servlet-class&gt;com.test.Servlets.DisplayHeader&lt;/servlet-class&gt;

  &lt;/servlet&gt;

  &lt;servlet-mapping&gt;

    &lt;servlet-name&gt;DisplayHeader&lt;/servlet-name&gt;

    &lt;url-pattern&gt;/hello&lt;/url-pattern&gt;

  &lt;/servlet-mapping&gt;

  &lt;filter&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;filter-class&gt;com.test.Filters.Filter1&lt;/filter-class&gt;

  &lt;/filter&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;url-pattern&gt;/*&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

&lt;/web-app&gt;

```

  

```java

//DisplayHeader.java，直接搬的菜鸟教程的。

package com.test.Servlets;

  

import java.io.IOException;

import java.io.PrintWriter;

import java.util.Enumeration;

  

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

  

@WebServlet(&#34;/DisplayHeader&#34;)

  

//扩展 HttpServlet 类

public class DisplayHeader extends HttpServlet {

  

    // 处理 GET 方法请求的方法

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException

    {

        // 设置响应内容类型

        response.setContentType(&#34;text/html;charset=UTF-8&#34;);

  

        PrintWriter out = response.getWriter();

        String title = &#34;HTTP Header 请求实例&#34;;

        String docType =

                &#34;&lt;!DOCTYPE html&gt; \n&#34;;

        out.println(docType &#43;

                &#34;&lt;html&gt;\n&#34; &#43;

                &#34;&lt;head&gt;&lt;meta charset=\&#34;utf-8\&#34;&gt;&lt;title&gt;&#34; &#43; title &#43; &#34;&lt;/title&gt;&lt;/head&gt;\n&#34;&#43;

                &#34;&lt;body bgcolor=\&#34;#f0f0f0\&#34;&gt;\n&#34; &#43;

                &#34;&lt;h1 align=\&#34;center\&#34;&gt;&#34; &#43; title &#43; &#34;&lt;/h1&gt;\n&#34; &#43;

                &#34;&lt;table width=\&#34;100%\&#34; border=\&#34;1\&#34; align=\&#34;center\&#34;&gt;\n&#34; &#43;

                &#34;&lt;tr bgcolor=\&#34;#949494\&#34;&gt;\n&#34; &#43;

                &#34;&lt;th&gt;Header 名称&lt;/th&gt;&lt;th&gt;Header 值&lt;/th&gt;\n&#34;&#43;

                &#34;&lt;/tr&gt;\n&#34;);

  

        Enumeration headerNames = request.getHeaderNames();

  

        while(headerNames.hasMoreElements()) {

            String paramName = (String)headerNames.nextElement();

            out.print(&#34;&lt;tr&gt;&lt;td&gt;&#34; &#43; paramName &#43; &#34;&lt;/td&gt;\n&#34;);

            String paramValue = request.getHeader(paramName);

            out.println(&#34;&lt;td&gt; &#34; &#43; paramValue &#43; &#34;&lt;/td&gt;&lt;/tr&gt;\n&#34;);

        }

        out.println(&#34;&lt;/table&gt;\n&lt;/body&gt;&lt;/html&gt;&#34;);

    }

    // 处理 POST 方法请求的方法

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);

    }

}

  

```

  
 &lt;br&gt;
## Filter如何filter something

  

- 映射过滤应用程序中所有资源

  

重点是url-pattern

  

```java

 &lt;filter&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;filter-class&gt;com.test.Filters.Filter1&lt;/filter-class&gt;

  &lt;/filter&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;url-pattern&gt;/*&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

```

  

- 过滤指定的类型文件资源

  

重点是url-pattern

  

```java

 &lt;filter&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;filter-class&gt;com.test.Filters.Filter1&lt;/filter-class&gt;

  &lt;/filter&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;url-pattern&gt;*.html&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

```

  

- 同时过滤多种类型资源

  

重点是filter-mapping

  

```java

 &lt;filter&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;filter-class&gt;com.test.Filters.Filter1&lt;/filter-class&gt;

  &lt;/filter&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;url-pattern&gt;*.html&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;url-pattern&gt;*.jsp&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

```

  

- 过滤指定的目录

  

重点是url-pattern

  

```java

 &lt;filter&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;filter-class&gt;com.test.Filters.Filter1&lt;/filter-class&gt;

  &lt;/filter&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;url-pattern&gt;/folder_name/*&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

```

  

- 过滤指定的Servlet

  

重点是filter-mapping中的servlet-name

  

```java

 &lt;filter&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;filter-class&gt;com.test.Filters.Filter1&lt;/filter-class&gt;

  &lt;/filter&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;servlet-name&gt;DisplayHeader&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

  &lt;servlet&gt;

    &lt;servlet-name&gt;DisplayHeader&lt;/servlet-name&gt;

    &lt;servlet-class&gt;com.test.Servlets.DisplayHeader&lt;/servlet-class&gt;

  &lt;/servlet&gt;

```

  

- 过滤指定文件

  

重点是url-pattern

  

```java

 &lt;filter&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;filter-class&gt;com.test.Filters.Filter1&lt;/filter-class&gt;

  &lt;/filter&gt;

  &lt;filter-mapping&gt;

    &lt;filter-name&gt;FilterTest&lt;/filter-name&gt;

    &lt;url-pattern&gt;/a.html&lt;/url-pattern&gt;

  &lt;/filter-mapping&gt;

```

  
 &lt;br&gt;
## Filter应用场景

  

1. 统一POST请求中文字符编码

2. 控制浏览器缓存页面中的静态资源

    1. 动态页面中引入一些图片orCSS（静态资源），为减轻服务器的压力，使用Filter控制浏览器缓存这些文件

3. 实现URL级别的权限认证

    1. 将执行敏感操作的Servlet映射到一些特殊目录中，同时用Filter将这些目录保护起来，限制只有某些权限的用户才能访问，从而在系统中实现一种URL级别的权限功能。

4. 实现用户自动登录

    1. 用户成功登录后发送名为user的COOKIE（值为用户名和md5后的密码）给客户端

    2. 用Filter检查用户是否带有该COOKIE

        1. 有，调用dao查询cookie的值和数据库是否匹配

        2. 匹配，则向session中存入user对象——登录标识，实现自动登录

  
 &lt;br&gt;
## 缺省Servlet URL绕过Filter

  

有点子看不懂原文在说个什么……

  

在对资源使用Filter的时候，有两种方式来指定要应用的Filter：

  

- 指定URL模式

    - 这个模式必须和web.xml中的`&lt;servlet-mapping&gt;`元素定义的模式匹配

- 指定Servlet名称

    - 这个名称必须和web.xml中的`&lt;servlet&gt;`元素中定义的名称匹配

  

但大多数服务器会使用一个激活器Servlet为Servlet提供一个默认的URL访问路径：

  

`http://host/WebAppPrefix/servlet/ServletName`

  

用户可以通过该URL直接访问Servlet，也就是绕开Filter

  
 &lt;br&gt;

# Listener

  

&lt;aside&gt;

💡 实现特定接口的java程序again。Servlet的监听器，监听客户端的请求，服务端的操作等。

  

&lt;/aside&gt;

  

一般给按钮增加监听器——点击按钮，触发一项监听事件

  
 &lt;br&gt;

## Listener类型

  

- 对Session类

    - HttpSessionListener：监听Session的创建与销毁

- 对Request类

    - ServletReuqestListener：监听request的创建与销毁

- 对Context类

    - ServletContextListener：监听Context的创建与销毁

    这个只是一个笼统的例子（IDEA官方模板）

```java

    package com.test.Listeners;

    import javax.servlet.*;

    import javax.servlet.http.*;

    import javax.servlet.annotation.*;

    @WebListener

    public class ListenerforServletContext implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

        public ListenerforServletContext() {

        }

        @Override

        public void contextInitialized(ServletContextEvent sce) {

            /* This method is called when the servlet context is initialized(when the Web application is deployed). */

            System.out.println(&#34;ServletContext对象创建&#34;);

        }

        @Override

        public void contextDestroyed(ServletContextEvent sce) {

            /* This method is called when the servlet Context is undeployed or Application Server shuts down. */

            System.out.println(&#34;ServletContext对象销毁&#34;);

        }

        @Override

        public void sessionCreated(HttpSessionEvent se) {

            /* Session is created. */

            System.out.println(se.getSession() &#43; &#34;创建成功。&#34;);

        }

        @Override

        public void sessionDestroyed(HttpSessionEvent se) {

            /* Session is destroyed. */

            System.out.println(&#34;Session销毁。&#34;);

        }

        @Override

        public void attributeAdded(HttpSessionBindingEvent sbe) {

            /* This method is called when an attribute is added to a session. */

        }

        @Override

        public void attributeRemoved(HttpSessionBindingEvent sbe) {

            /* This method is called when an attribute is removed from a session. */

        }

        @Override

        public void attributeReplaced(HttpSessionBindingEvent sbe) {

            /* This method is called when an attribute is replaced in a session. */

        }

    }
```

 &lt;br&gt;然后其他的配置：
    
```java

    //web.xml

    &lt;!-- 对ServletContext和Request的listener的注册 --&gt;

      &lt;listener&gt;

        &lt;listener-class&gt;com.test.Listeners.ListenerforServletContext&lt;/listener-class&gt;

      &lt;/listener&gt;

      //web.xml文件中注册监听器

    &lt;!--注册针对HttpSession对象进行监听的监听器--&gt;

    &lt;listener&gt;

          &lt;description&gt;HttpSessionListener监听器&lt;/description&gt;

          &lt;listener-class&gt;me.gacl.web.listener.MyHttpSessionListener&lt;/listener-class&gt;

    &lt;/listener&gt;

    &lt;!-- 配置HttpSession对象的销毁时机 --&gt;

    &lt;session-config&gt;

          &lt;!--配置HttpSession对象的1分钟之后销毁 --&gt;

          &lt;session-timeout&gt;1&lt;/session-timeout&gt;

    &lt;/session-config&gt;

    ```
  

![Untitled](./S_L_Fpics/Untitled%205.png)

  

&lt;hr&gt;



---

> Author:   
> URL: https://66lueflam144.github.io/posts/3a26b5b/  

