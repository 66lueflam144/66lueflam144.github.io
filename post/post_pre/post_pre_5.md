---
title: JSP for ME
subtitle: 只是一些需要记住的
date: 2024-02-01T10:14:35+08:00
slug: f3affec
draft: false
author:
  name: 
  link: 
  email: 
  avatar: 
description: 
keywords: 
license: 
comment: false
weight: 0
tags:
  - JSP
categories:
  - LEARN
  - JAVA
hiddenFromHomePage: false
hiddenFromSearch: false
hiddenFromRss: false
hiddenFromRelated: false
summary: 
resources:
  - name: featured-image
    src: featured-image.jpg
  - name: featured-image-preview
    src: featured-image-preview.jpg
toc: true
math: false
lightgallery: false
password: 
message: 
repost:
  enable: true
  url:
---

[参考](https://www.w3cschool.cn/jsp/)

  

# JSP Introdu

  

>Java Server Pages

  

- 一种动态网页开发技术

- 一种Java Servlet，主要用于实现Java Web Application的用户界面部分

- 使用JSP标签`<% %>`在HTML网页中插入Java代码

- JSP通过网页表单获取用户输入数据、访问数据库以及其他数据源，然后动态的创建网页

  
  

## Why JSP

  

与类似的CGI程序相比，

- 性能优越更人性化：直接在HTML插入元素无需单独引用，服务器直接调用编译好的JSP文件，无需事先载入解释器和目标脚本

  

- 背景喜人：JSP基于Java Servlets API，坐拥各大企业级Java API

  

- 合作共赢：JSP页面可以和处理业务逻辑的servlets一起使用

  
  

## JSP Structure

  

- **JSP容器（引擎）**：处理JSP页面，JSP容器与Web服务器协同合作，为JSP的正常运行提供必要的运行环境和其他服务，并且能够正确识别专属于JSP网页的特殊元素。

  

**JSP容器和JSP文件在Web Application中所处的位置：**

  
  

<img src="https://7n.w3cschool.cn/statics/images/course/jsp-arch.jpg" alt="Typical Web Server supporting JSP" width="600" height="300" >

  

### Handle JSP

  
  
  

<img src="https://atts.w3cschool.cn/attachments/day_160825/201608251727372798.jpg" alt="Handle JSP" width="600" height="300" >

  

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

    <br>解析JSP文件、servlet容器编译servlet源文件，生成servlet类

- **初始化**阶段

    <br>加载JSP对应的servlet类，创建其实例，并调用其初始化方法

- **执行**阶段

    <br>调用与JSP对应的servlet实例的服务方法

- **销毁**阶段

    <br>调用与JSP对应的servlet实例的销毁方法，然后销毁servlet实例

  
  

# JSP rules

  

## 脚本程序

  

```jsp

<%

out.println("Your IP address is " + request.getRemoteAddr());

%>

```

  

## JSP声明

  

```jsp

<%! int i = 0; %>

```

  

## JSP表达式

  

```jsp

<p>

  Today's Hit : <%= (new java.util.Date()).toLocaleString() %>

</p>

```

  

## JSP注释

  

```jsp

<%-- --%>

```

  

## JSP指令

  

```jsp

<%@page .... %>s

```

  

有三种指令：

  

|指令|描述|

|---|---|

|`<%@page ... %>`|  定义页面的依赖属性，比如脚本语言、error页面、缓存需求等等|

|`<%@include ... %>`|包含其他文件|

|`<%@taglib ... %>`|引入标签库的定义，可以是自定义标签|

  
  
  

<hr>

  

if you want more just click that link...

  
  

# JSP in Execution

  
  

## 请求信息

  

```jsp

<%@ page import="java.util.Enumeration" %>

<%@ page import="javax.servlet.http.Cookie" %>

<%@ page import="javax.servlet.http.HttpSession" %>

  

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<html>

<head>

    <meta charset="utf-8">

    <title>So Many Things</title>

</head>

<body>

<h2>Do you want some HTTP?</h2>

  

<!-- HTTP Headers -->

<table border="1">

    <tr>

        <th>Header Name</th>

        <th>Header Value(s)</th>

    </tr>

    <%

        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String paramName = (String) headerNames.nextElement();

            out.print("<tr><td>" + paramName + "</td>\n");

            String paramValue = request.getHeader(paramName);

            out.println("<td>" + paramValue + "</td></tr>\n");

        }

    %>

</table>

  

<!-- Cookies -->

<h3>Cookies:</h3>

<%

    Cookie[] cookies = request.getCookies();

    if (cookies != null) {

        out.println("<ul>");

        for (Cookie cookie : cookies) {

            out.println("<li>" + cookie.getName() + ": " + cookie.getValue() + "</li>");

        }

        out.println("</ul>");

    } else {

        out.println("<p>No cookies found.</p>");

    }

%>

  

<!-- Session Information -->

<h3>Session Information:</h3>

<%

    HttpSession sessione = request.getSession(false); // Pass false to prevent creating a new session if one doesn't exist

    if (sessione != null) {

        out.println("<p>Session ID: " + sessione.getId() + "</p>");

    } else {

        out.println("<p>No active session.</p>");

    }

%>

  

<!-- User's IP Address -->

<h3>User's IP Address:</h3>

<%

    String userIP = request.getRemoteAddr();

    out.println("<p>" + userIP + "</p>");

%>

  

</body>

</html>

  

```

  

## fun clock

  

```jsp

<%@page import="java.io.*, java.util.*" %>

<html>

<head>

    <meta charset="utf-8">

    <title>So Many Things</title>

</head>

<body>

<h2>Auto Refresh Header</h2>

<%

    response.setIntHeader("Refresh", 5);

    Calendar calendar = new GregorianCalendar();

    String am_pm;

    int hour = calendar.get(Calendar.HOUR);

    int minute = calendar.get(Calendar.MINUTE);

    int second = calendar.get(Calendar.SECOND);

    if (calendar.get(Calendar.AM_PM) == 0){

        am_pm = "AM";

    }

    else{

        am_pm = "PM";

    }

    String CT = hour + ":" + minute + ":" + second + " " + am_pm;

    out.println("Current Time is: " + CT + "\n");

%>

</body>

</html>

```

  
  

## cookie

  

学得好想ew...

  
  

这个是自定义cookie：

不过会把浏览器上的cookie都召唤出来。

```jsp

<%

    Cookie fn = new Cookie("fn", request.getParameter("fn"));

    Cookie ln = new Cookie("ln", request.getParameter("ln"));

    fn.setMaxAge(60*60*24);

    ln.setMaxAge(60*60*24);

    response.addCookie(fn);

    response.addCookie(ln);

%>

  

<html>

<head>

    <title>This</title>

</head>

<body>

<center>

    <h1>Cookie</h1>

</center>

<ul>

    <li>

        <p><b>FN:</b>

        <%= request.getParameter("fn")%></p>

    </li>

    <li>

        <p><b>LN:</b>

        <%= request.getParameter("ln")%></p>

    </li>

</ul>

  

<%

    Cookie cookie = null;

    Cookie[] cookies = null;

    cookies = request.getCookies();

    if (cookies != null){

        out.println("<h2> Found Cookie Name and Value, Do you want to taste it?</h2>");

        for (int i = 0; i < cookies.length; i++){

            cookie = cookies[i];

            out.print("Name:" + cookie.getName() + ", ");

            out.print("Value:" + cookie.getValue() + "<br />");

        }

    }

    else {

        out.println("<h2>There is no COOKIE!</h2>");

    }

%>

</body>

</html>

```

  
  

## session

  
  

自定义一个session查询。

  

```jsp

<%@page import="java.io.*, java.util.*" %>

<%

    Date createTime = new Date(session.getCreationTime());

    Date lastAccessTime = new Date(session.getLastAccessedTime());

    String titile = "Welcome Back!";

    Integer visitCount = new Integer(0);

    String visitCountKey = new String("visitCount");

    String userIDKey = new String("userID");

    String userID = new String("ABCD");

  

    if (session.isNew()){

        titile = "welcome!";

        session.setAttribute(userIDKey, userID);

        session.setAttribute(visitCountKey,visitCount);

    }

  

    visitCount = (Integer) session.getAttribute(visitCountKey);

    visitCount = visitCount + 1;

    userID = (String) session.getAttribute(userIDKey);

    session.setAttribute(visitCountKey, visitCount);

%>

<html>

<head>

    <title>Hey</title>

</head>

<body>

<table border="1" align="center">

    <tr bgcolor="#faebd7">

        <th>Session Info</th>

        <th>Value</th>

    </tr>

    <tr>

        <td>Create Time</td>

        <td><% out.print(createTime); %></td>

    </tr>

    <tr>

        <td>Time of Last Access</td>

        <td><% out.print(lastAccessTime); %></td>

    </tr>

    <tr>

        <td>User ID</td>

        <td><% out.print(userID); %></td>

    </tr>

    <tr>

        <td>Number of Visits</td>

        <td><% out.print(visitCount); %></td>

    </tr>

</table>

</body>

</html>

```

  

## 页面重定向

  
  
  

```jsp

<%@page import="java.io.*, java.util.*" %>

<html>

<head>

    <title>Redirection</title>

</head>

<body>

<center>

    <h1>Redirection</h1>

</center>

<%

    String site = new String("https://66lueflam144.github.io/");

    response.setStatus(response.SC_MOVED_TEMPORARILY);

    response.setHeader("Location", site);

%>

</body>

</html>

```

  

# JSP 木马

  

[参考1](https://www.cnblogs.com/Eleven-Liu/p/17301613.html)

<br>

  

[参考2](https://github.com/theralfbrown/webshell-2/tree/master/jsp)

  

<br>

  

[参考3](https://virusday.github.io/2020/12/08/jsp%E4%B8%80%E5%8F%A5%E8%AF%9D%E6%9C%A8%E9%A9%AC/jsp%E4%B8%80%E5%8F%A5%E8%AF%9D%E6%9C%A8%E9%A9%AC/)

  
  

暂时也没什么可说的。
