---
title: 简单的RSA笔记
subtitle: 
date: 2024-03-26T10:54:48+08:00
slug: 993b230
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
  - LEARN
categories:
  - PHP
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

  

# MD5

  

> Message-DigestAlgorithm 5

  

![MD5](https://s3.bmp.ovh/imgs/2022/02/784b757cee5f7bf0.png)

  

一种被广泛使用的**密码散列函数**，可以产生出一个<u>128位</u>（16个字符(BYTES)）的散列值（hash value），用于确保信息传输完整一致。

同时也是一种单向加密算法。

  

- 将数据（如一段文字）运算变为另一固定长度值，是散列算法的基础原理。

  

1996年后被证实存在弱点，可以被加以破解，对于需要高度安全性的资料，专家一般建议改用其他算法，如SHA-2。

2004年，证实MD5算法无法防止碰撞攻击，因此不适用于安全性认证，如SSL公开密钥认证或是数字签名等用途。

  

## 特点

  

- 压缩性

- 容易计算--线性时间复杂度

- 抗修改性

- 抗碰撞性

  

==MD5加密中文需要使用UTF-8编码，但Windows下默认是GBK编码，两种编码得到的结果是不一样的==

  

## 算法

  

- []输入:不定长度信息

- []输出:固定长度128-bit（128个0和1的二进制串）

  

存在换算为十六进制的输出，每4bit表示一个十六进制数，此时为32位

  

### 加密

  

1. 填充数据

  

计算 数据长度（bit）对512取模，不够448位，进行填充使之成立。

填充规则：第一位填充1，其余填充0

  
  

2. 记录数据长度

  

填充之后的数据位数为 N*512+448， 向其后追加64位用来存储填充前数据长度

  

3. 以标准的幻数作为输入

  

每512个字节进行一次处理，前一次处理的输出 作为 后一次处理的输入

  

在该“循环”处理开始前，需要拿4个标准数作为输入：

  

- unsigned int A=0x67452301,

- unsigned int B=0xefcdab89,

- unsigned int C=0x98badcfe,

- unsigned int D=0x10325476;

  
  

4. 进行N轮循环处理，最后输出结果

  

每一轮处理也要循环64次，这64次循环被分为4各组（每16次循环为一组），每组循环使用**不同的逻辑处理函数**，处理完成后，将**输出作为输入进入下一轮循环**。

  

## 实现

  

```php

<?php

$str = 'apple';

  

if (md5($str) === '1f3870be274f6c49b3e31a0c6728957f') {

    echo "Would you like a green or red apple?";

}

?>

```

  

## 作用

  

1. 一致性检验

2. 数字签名

3. 安全访问认证

  

## 碰撞

  

```python

import hashlib

for i in range(0,9999999999999999):

md5=hashlib.md5(str(i).encode("utf-8")).hexdigest()

if md5[0:5]=="66666": #md5后前五位是66666

print(i)

print("no result")

```

  

## 参考

  

[一文读懂MD5算法](https://segmentfault.com/a/1190000021691476)

[MD5加密原理解析及OC版原理实现](https://developer.aliyun.com/article/790494)

[MD5碰撞原理简单介绍及其实现](https://www.cnblogs.com/wysngblogs/p/15905398.html)

[彩虹表](https://zh.wikipedia.org/wiki/%E5%BD%A9%E8%99%B9%E8%A1%A8)

[md5](https://www.php.net/manual/zh/function.md5.php)

  
  

<hr>

  

# DNS污染？
