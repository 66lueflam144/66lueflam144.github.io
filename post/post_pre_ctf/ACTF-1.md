---
title: ACTF 2020 Back File
date: 2023-10-23T12:52:06+08:00
slug: 4c47f54
tags:
  - 目录遍历
  - EASY
categories:
  - CTF
---

>其实看这个名字的时候就在想是不是和目录遍历有关了。

  

<!--more-->

  

## Scan

  

打开靶场页面检查发现确实没有什么，所以用dirsearch扫描一下。

在经历漫长扫描后，在report中找到扫描文件报告打开，查找响应状态码为`200`的文件，得到：

```txt

200     0B   http://89ecddd8-8816-4676-a066-6ed0467a4fdc.node4.buuoj.cn:81/flag.php

200   347B   http://89ecddd8-8816-4676-a066-6ed0467a4fdc.node4.buuoj.cn:81/index.php.bak

```

看大小就知道第二个有一些东西，输入网址得到一个bak文件的下载。

```php

//index.php.bak

  

<?php

include_once "flag.php";

  

if(isset($_GET['key'])) {

    $key = $_GET['key'];

    if(!is_numeric($key)) {

        exit("Just num!");

    }

    $key = intval($key);

    $str = "123ffwsfwefwf24r2f32ir23jrw923rskfjwtsw54w3";

    if($key == $str) {

        echo $flag;

    }

}

else {

    echo "Try to find out source file!";

}

```

进行一些翻译：

- 1. `include_once "flag.php";` 表示在代码中包含了一个名为 `flag.php` 的文件。通过这行代码，`flag.php `文件中的内容会被复制到当前代码文件的相应位置——这样做的目的是为了引入该文件中定义的变量、函数或类，以便在当前代码文件中使用它们。

- 2. 如果 HTTP GET 请求中带有 `key` 参数，代码将进入相应的条件判断分支。

- 3. `key` 参数的值被赋给变量 `$key`。

- 4. 如果 `$key` 不是一个数字（通过 `is_numeric` 函数判断），将输出 "Just num!" 并终止程序执行。

- 5. 如果 `$key` 是一个数字，则将其转换为整数（通过 `intval` 函数实现）。

- 6. 字符串变量 `$str` 初始化为 "123ffwsfwefwf24r2f32ir23jrw923rskfjwtsw54w3"。

- 7. 如果 `$key` 的值等于 `$str` 的值，将输出 `flag` 的值。

- 8. 如果没有提供 `key` 参数，将输出 "Try to find out source file!"。

  

----

----

  

## translate

  

### ?key=

所以去看看`flag.php`有什么，但是打开是一片空白。<br>（在这里犯了一个错误，就是认为`key`这个请求参数是在`flag.php`下生效。然后搞半天没有任何东西。）

其实从`index.php.bak`这个名字就可以知道是和`index.php`有关。不过网站的默认首页就是它，所以不用写`http://xx/index.php?key=`，直接写`http://xx/?key=`就行。

  

----

  

### Just Num

前面提到一个`is_numeric()`函数判断是否是数字，所以`$key=$str`的值其实只是`123`.

(把`$str`的值ctrl+c上去，得到一个`just num`，嗯，想起来高中时候的一些题目。)

  

----

  

## ファンワイ

因为嫌弃dirsearch太慢所以去找了个新的`dirmap`，但是没有任何out file生成……可能还需要调一下配置。
