---
title: PHP 命令和代码执行
subtitle: 
date: 2023-11-10T18:17:45+08:00
slug: f357208
tags:
  - PHP
  - CMD
categories:
  - LEARN
---

一点笔记

<!--more-->

  
  

# 命令执行(RCE)

  

## php命令执行原理

  

>通过参数可控的命令执行函数进行命令执行

  

## 相关函数

  
  

### **exec**

  

写了一个简单的demo进行演示test

```php

//demo.php

<?php

$sys = $_REQUEST['value'];

$cmd = exec($sys);//Linux用bash，Windows用cmd

echo $cmd;//因为exec()函数没有回显，需要echo进行输出，并且只返回执行后的最后一行结果

?>

```

  

```html

index.html

<!DOCTYPE html>

<html lang="zh-CN">

    <head>

    <meta charset="utf-8" />

    <title>Karina</title>

    </head>

  

    <body>

        <h1>Cause I'm too spicy for your heart</h1>

        <p>welcome and here for your test.</p>

    </body>

</html>

```

其实html可写可不写。

  

当我构造payload为`?value=whoami`的时候，就会将电脑名+当前用户名返回。

如果是`>value=calc.exe`，就会成功召唤计算器。

  

----

  

### **shell_exec**

  

shell_exec()没有回显，用`echo`或者`var_dump()`进行返回，但这个函数返回所有内容——也是和exec()的区别所在。

  

``其实就是调用shell_exec()函数，使用示例如下：

```php

<?php

&sys = $_REQUEST['value'];

$cmd = `$sys`;

echo $cmd;

?>

```

  

----

  

### **system & passthru**

  

均将输入的参数当作命令执行，且回显所有内容

  

```php

<?php

&sys = $_REQUEST['value'];

$cmd = system($sys);

//$cmd = passthru($sys);

?>

```

  

----

  

### **popen & proc_open**

  

`popen()`通常用于打开进程文件指针，但如果传入的参数可控，也能进行无回显命令执行

```php

<?php

&sys = $_REQUEST['value'];

$cmd = popen($sys,'r');

echo $cmd;

var_dump($cmd);

?>

```

返回结果是**文件指针**，即：

```txt

Resource id #2resource(2) of type (stream)

```

第一个resource是echo的回显，第二个resource是var_dump的回显。

  

proc_open()，执行一个命令并且打开用来输入或输出的文件指针，与popen()类似但是参数更多，处理数据努力更强。

  

### **pcntl_exec**

  

pcntl_exec()在当前进程空间执行指定程序，PHP version >=4.2.0

  

由于其执行命令是没有回显的，所以其常与python结合来反弹shell，或是绕过disable_functions

  

<a href=https://www.freebuf.com/articles/network/263540.html>一个参考</a>

<a href=https://xz.aliyun.com/t/10057>两个参考</a>

  

---

  
  

## 管道符

  

- `|`：直接执行后面语句，A|B，执行B

- `||`：如果前面语句执行错误，才执行后面语句

- `&`：均执行

- `&&`：如果前面语句执行错误则后面也不执行，只有前面执行成功才执行后面

- `;`：only for Linux的一个，前面执行完，继续执行后面。

  

-----

-----

  
  

# 代码执行

  

需要分清楚命令执行与代码执行的区别与联系。

  

>代码执行--通过代码调用函数直接调用PHP中任意代码进行执行。

  

funny thing is ：代码执行可以通过调用命令执行的函数来执行系统命令，来达到控制后台甚至服务器——达成RCE。

  

## 相关函数

  

### eval() & assert()

  

### preg_replace()

  

### create_function()

  

### array_map()

  

### call_user_func() & call_user_func_array()

  

### array_filter() & array_walk()

  

### ob_start()

  
  

### usort()

  
  
  
  
  

# 番外

  

用dirmap和dirsearch又扫描了一下这个为测试用而搭建的本地网站，结果dirsearch还在努力着，dirmap已经把所有文件都告诉我了。。。下一步思考怎么设置文件禁止访问。
