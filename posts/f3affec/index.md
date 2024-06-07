# JSP for ME


[参考](https://www.w3cschool.cn/jsp/)

  

# JSP Introdu

  

&gt;Java Server Pages

  

- 一种动态网页开发技术

- 一种Java Servlet，主要用于实现Java Web Application的用户界面部分

- 使用JSP标签`&lt;% %&gt;`在HTML网页中插入Java代码

- JSP通过网页表单获取用户输入数据、访问数据库以及其他数据源，然后动态的创建网页

  
  

## Why JSP

  

与类似的CGI程序相比，

- 性能优越更人性化：直接在HTML插入元素无需单独引用，服务器直接调用编译好的JSP文件，无需事先载入解释器和目标脚本

  

- 背景喜人：JSP基于Java Servlets API，坐拥各大企业级Java API

  

- 合作共赢：JSP页面可以和处理业务逻辑的servlets一起使用

  
  

## JSP Structure

  

- **JSP容器（引擎）**：处理JSP页面，JSP容器与Web服务器协同合作，为JSP的正常运行提供必要的运行环境和其他服务，并且能够正确识别专属于JSP网页的特殊元素。

  

**JSP容器和JSP文件在Web Application中所处的位置：**

  
  

&lt;img src=&#34;https://7n.w3cschool.cn/statics/images/course/jsp-arch.jpg&#34; alt=&#34;Typical Web Server supporting JSP&#34; width=&#34;600&#34; height=&#34;300&#34; &gt;

  

### Handle JSP

  
  
  

&lt;img src=&#34;https://atts.w3cschool.cn/attachments/day_160825/201608251727372798.jpg&#34; alt=&#34;Handle JSP&#34; width=&#34;600&#34; height=&#34;300&#34; &gt;

  

以下步骤表明了Web服务器是如何使用JSP来创建网页的：

  

- 您的浏览器**发送一个HTTP请求**给服务器。

- Web服务器**识别**出这是一个对JSP网页的请求，并且将该请求传递给JSP引擎。通过使用URL或者.jsp文件来完成。

- JSP引擎从磁盘中**载入JSP文件**，然后将它们**转化为servlet**。这种转化只是简单地将所有模板文本改用println()语句，并且将所有的JSP元素转化成Java代码。

- JSP引擎将servlet**编译成可执行类**，并且将原始请求传递给servlet引擎。

- Web服务器的某组件将会调用servlet引擎，然后载入并执行servlet类。在执行过程中，servlet**产生HTML格式的输出**并将其内嵌于HTTP response中上交给Web服务器。

- Web服务器**以静态HTML网页的形式**将HTTP response返回到您的浏览器中。

- 最终，Web浏览器处理HTTP response中动态产生的HTML网页，就好像在处理静态网页一样。

  
  

## JSP Scope

  

- **编译**阶段

    &lt;br&gt;解析JSP文件、servlet容器编译servlet源文件，生成servlet类

- **初始化**阶段

    &lt;br&gt;加载JSP对应的servlet类，创建其实例，并调用其初始化方法

- **执行**阶段

    &lt;br&gt;调用与JSP对应的servlet实例的服务方法

- **销毁**阶段

    &lt;br&gt;调用与JSP对应的servlet实例的销毁方法，然后销毁servlet实例

  
  

# JSP rules

  

## 脚本程序

  

```jsp

&lt;%

out.println(&#34;Your IP address is &#34; &#43; request.getRemoteAddr());

%&gt;

```

  

## JSP声明

  

```jsp

&lt;%! int i = 0; %&gt;

```

  

## JSP表达式

  

```jsp

&lt;p&gt;

  Today&#39;s Hit : &lt;%= (new java.util.Date()).toLocaleString() %&gt;

&lt;/p&gt;

```

  

## JSP注释

  

```jsp

&lt;%-- --%&gt;

```

  

## JSP指令

  

```jsp

&lt;%@page .... %&gt;s

```

  

有三种指令：

  

|指令|描述|

|---|---|

|`&lt;%@page ... %&gt;`|  定义页面的依赖属性，比如脚本语言、error页面、缓存需求等等|

|`&lt;%@include ... %&gt;`|包含其他文件|

|`&lt;%@taglib ... %&gt;`|引入标签库的定义，可以是自定义标签|

  
  
  

&lt;hr&gt;

  

if you want more just click that link...

  
  

# JSP in Execution

  
  

## 请求信息

  

```jsp

&lt;%@ page import=&#34;java.util.Enumeration&#34; %&gt;

&lt;%@ page import=&#34;javax.servlet.http.Cookie&#34; %&gt;

&lt;%@ page import=&#34;javax.servlet.http.HttpSession&#34; %&gt;

  

&lt;%@ page contentType=&#34;text/html;charset=UTF-8&#34; language=&#34;java&#34; %&gt;

&lt;!DOCTYPE html&gt;

&lt;html&gt;

&lt;head&gt;

    &lt;meta charset=&#34;utf-8&#34;&gt;

    &lt;title&gt;So Many Things&lt;/title&gt;

&lt;/head&gt;

&lt;body&gt;

&lt;h2&gt;Do you want some HTTP?&lt;/h2&gt;

  

&lt;!-- HTTP Headers --&gt;

&lt;table border=&#34;1&#34;&gt;

    &lt;tr&gt;

        &lt;th&gt;Header Name&lt;/th&gt;

        &lt;th&gt;Header Value(s)&lt;/th&gt;

    &lt;/tr&gt;

    &lt;%

        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String paramName = (String) headerNames.nextElement();

            out.print(&#34;&lt;tr&gt;&lt;td&gt;&#34; &#43; paramName &#43; &#34;&lt;/td&gt;\n&#34;);

            String paramValue = request.getHeader(paramName);

            out.println(&#34;&lt;td&gt;&#34; &#43; paramValue &#43; &#34;&lt;/td&gt;&lt;/tr&gt;\n&#34;);

        }

    %&gt;

&lt;/table&gt;

  

&lt;!-- Cookies --&gt;

&lt;h3&gt;Cookies:&lt;/h3&gt;

&lt;%

    Cookie[] cookies = request.getCookies();

    if (cookies != null) {

        out.println(&#34;&lt;ul&gt;&#34;);

        for (Cookie cookie : cookies) {

            out.println(&#34;&lt;li&gt;&#34; &#43; cookie.getName() &#43; &#34;: &#34; &#43; cookie.getValue() &#43; &#34;&lt;/li&gt;&#34;);

        }

        out.println(&#34;&lt;/ul&gt;&#34;);

    } else {

        out.println(&#34;&lt;p&gt;No cookies found.&lt;/p&gt;&#34;);

    }

%&gt;

  

&lt;!-- Session Information --&gt;

&lt;h3&gt;Session Information:&lt;/h3&gt;

&lt;%

    HttpSession sessione = request.getSession(false); // Pass false to prevent creating a new session if one doesn&#39;t exist

    if (sessione != null) {

        out.println(&#34;&lt;p&gt;Session ID: &#34; &#43; sessione.getId() &#43; &#34;&lt;/p&gt;&#34;);

    } else {

        out.println(&#34;&lt;p&gt;No active session.&lt;/p&gt;&#34;);

    }

%&gt;

  

&lt;!-- User&#39;s IP Address --&gt;

&lt;h3&gt;User&#39;s IP Address:&lt;/h3&gt;

&lt;%

    String userIP = request.getRemoteAddr();

    out.println(&#34;&lt;p&gt;&#34; &#43; userIP &#43; &#34;&lt;/p&gt;&#34;);

%&gt;

  

&lt;/body&gt;

&lt;/html&gt;

  

```

  

## fun clock

  

```jsp

&lt;%@page import=&#34;java.io.*, java.util.*&#34; %&gt;

&lt;html&gt;

&lt;head&gt;

    &lt;meta charset=&#34;utf-8&#34;&gt;

    &lt;title&gt;So Many Things&lt;/title&gt;

&lt;/head&gt;

&lt;body&gt;

&lt;h2&gt;Auto Refresh Header&lt;/h2&gt;

&lt;%

    response.setIntHeader(&#34;Refresh&#34;, 5);

    Calendar calendar = new GregorianCalendar();

    String am_pm;

    int hour = calendar.get(Calendar.HOUR);

    int minute = calendar.get(Calendar.MINUTE);

    int second = calendar.get(Calendar.SECOND);

    if (calendar.get(Calendar.AM_PM) == 0){

        am_pm = &#34;AM&#34;;

    }

    else{

        am_pm = &#34;PM&#34;;

    }

    String CT = hour &#43; &#34;:&#34; &#43; minute &#43; &#34;:&#34; &#43; second &#43; &#34; &#34; &#43; am_pm;

    out.println(&#34;Current Time is: &#34; &#43; CT &#43; &#34;\n&#34;);

%&gt;

&lt;/body&gt;

&lt;/html&gt;

```

  
  

## cookie

  

学得好想ew...

  
  

这个是自定义cookie：

不过会把浏览器上的cookie都召唤出来。

```jsp

&lt;%

    Cookie fn = new Cookie(&#34;fn&#34;, request.getParameter(&#34;fn&#34;));

    Cookie ln = new Cookie(&#34;ln&#34;, request.getParameter(&#34;ln&#34;));

    fn.setMaxAge(60*60*24);

    ln.setMaxAge(60*60*24);

    response.addCookie(fn);

    response.addCookie(ln);

%&gt;

  

&lt;html&gt;

&lt;head&gt;

    &lt;title&gt;This&lt;/title&gt;

&lt;/head&gt;

&lt;body&gt;

&lt;center&gt;

    &lt;h1&gt;Cookie&lt;/h1&gt;

&lt;/center&gt;

&lt;ul&gt;

    &lt;li&gt;

        &lt;p&gt;&lt;b&gt;FN:&lt;/b&gt;

        &lt;%= request.getParameter(&#34;fn&#34;)%&gt;&lt;/p&gt;

    &lt;/li&gt;

    &lt;li&gt;

        &lt;p&gt;&lt;b&gt;LN:&lt;/b&gt;

        &lt;%= request.getParameter(&#34;ln&#34;)%&gt;&lt;/p&gt;

    &lt;/li&gt;

&lt;/ul&gt;

  

&lt;%

    Cookie cookie = null;

    Cookie[] cookies = null;

    cookies = request.getCookies();

    if (cookies != null){

        out.println(&#34;&lt;h2&gt; Found Cookie Name and Value, Do you want to taste it?&lt;/h2&gt;&#34;);

        for (int i = 0; i &lt; cookies.length; i&#43;&#43;){

            cookie = cookies[i];

            out.print(&#34;Name:&#34; &#43; cookie.getName() &#43; &#34;, &#34;);

            out.print(&#34;Value:&#34; &#43; cookie.getValue() &#43; &#34;&lt;br /&gt;&#34;);

        }

    }

    else {

        out.println(&#34;&lt;h2&gt;There is no COOKIE!&lt;/h2&gt;&#34;);

    }

%&gt;

&lt;/body&gt;

&lt;/html&gt;

```

  
  

## session

  
  

自定义一个session查询。

  

```jsp

&lt;%@page import=&#34;java.io.*, java.util.*&#34; %&gt;

&lt;%

    Date createTime = new Date(session.getCreationTime());

    Date lastAccessTime = new Date(session.getLastAccessedTime());

    String titile = &#34;Welcome Back!&#34;;

    Integer visitCount = new Integer(0);

    String visitCountKey = new String(&#34;visitCount&#34;);

    String userIDKey = new String(&#34;userID&#34;);

    String userID = new String(&#34;ABCD&#34;);

  

    if (session.isNew()){

        titile = &#34;welcome!&#34;;

        session.setAttribute(userIDKey, userID);

        session.setAttribute(visitCountKey,visitCount);

    }

  

    visitCount = (Integer) session.getAttribute(visitCountKey);

    visitCount = visitCount &#43; 1;

    userID = (String) session.getAttribute(userIDKey);

    session.setAttribute(visitCountKey, visitCount);

%&gt;

&lt;html&gt;

&lt;head&gt;

    &lt;title&gt;Hey&lt;/title&gt;

&lt;/head&gt;

&lt;body&gt;

&lt;table border=&#34;1&#34; align=&#34;center&#34;&gt;

    &lt;tr bgcolor=&#34;#faebd7&#34;&gt;

        &lt;th&gt;Session Info&lt;/th&gt;

        &lt;th&gt;Value&lt;/th&gt;

    &lt;/tr&gt;

    &lt;tr&gt;

        &lt;td&gt;Create Time&lt;/td&gt;

        &lt;td&gt;&lt;% out.print(createTime); %&gt;&lt;/td&gt;

    &lt;/tr&gt;

    &lt;tr&gt;

        &lt;td&gt;Time of Last Access&lt;/td&gt;

        &lt;td&gt;&lt;% out.print(lastAccessTime); %&gt;&lt;/td&gt;

    &lt;/tr&gt;

    &lt;tr&gt;

        &lt;td&gt;User ID&lt;/td&gt;

        &lt;td&gt;&lt;% out.print(userID); %&gt;&lt;/td&gt;

    &lt;/tr&gt;

    &lt;tr&gt;

        &lt;td&gt;Number of Visits&lt;/td&gt;

        &lt;td&gt;&lt;% out.print(visitCount); %&gt;&lt;/td&gt;

    &lt;/tr&gt;

&lt;/table&gt;

&lt;/body&gt;

&lt;/html&gt;

```

  

## 页面重定向

  
  
  

```jsp

&lt;%@page import=&#34;java.io.*, java.util.*&#34; %&gt;

&lt;html&gt;

&lt;head&gt;

    &lt;title&gt;Redirection&lt;/title&gt;

&lt;/head&gt;

&lt;body&gt;

&lt;center&gt;

    &lt;h1&gt;Redirection&lt;/h1&gt;

&lt;/center&gt;

&lt;%

    String site = new String(&#34;https://66lueflam144.github.io/&#34;);

    response.setStatus(response.SC_MOVED_TEMPORARILY);

    response.setHeader(&#34;Location&#34;, site);

%&gt;

&lt;/body&gt;

&lt;/html&gt;

```

  

# JSP 木马

  

[参考1](https://www.cnblogs.com/Eleven-Liu/p/17301613.html)

&lt;br&gt;

  

[参考2](https://github.com/theralfbrown/webshell-2/tree/master/jsp)

  

&lt;br&gt;

  

[参考3](https://virusday.github.io/2020/12/08/jsp%E4%B8%80%E5%8F%A5%E8%AF%9D%E6%9C%A8%E9%A9%AC/jsp%E4%B8%80%E5%8F%A5%E8%AF%9D%E6%9C%A8%E9%A9%AC/)

  
  

暂时也没什么可说的。


---

> Author:   
> URL: https://66lueflam144.github.io/posts/f3affec/  

