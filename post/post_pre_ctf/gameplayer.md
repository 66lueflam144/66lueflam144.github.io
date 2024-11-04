---
title: newstar CTF 2023 听说你是个游戏高手
date: 2023-10-23T13:17:04+08:00
slug: dd90fd2
tags:
  - tricks
categories:
  - CTF
---

>知道查看js文件，知道可以修改值但是修改错方向导致痛失……

  

<!--more-->

官方给的有两种方法

- 1.burp suite change the request method

- 2.修改gameScore值

  

## 研究页面

只截取了最重要的地方：

```js

...

/**********游戏当前状态************/

var curPhase = PHASE_DOWNLOAD;

var gameScore = 0;

...

  

//游戏结束

function gameover(){

    if(gameScore > 100000){

        var xhr = new XMLHttpRequest();

        xhr.open("POST", "/api.php", true);

        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onreadystatechange = function() {

        if (xhr.readyState === 4 && xhr.status === 200) {

            var response = JSON.parse(xhr.responseText);

            alert(response.message);

        }

        };

        var data = {

            score: gameScore,

        };

        xhr.send(JSON.stringify(data));

    }

    alert("成绩："+gameScore);

    gameScore=0;  

    curPhase =PHASE_READY;  

    ...    

}

```

- `xhr`调用`XMLHttpRequest()`来构造请求——`xh.open()`中又可以看到是以`POST`的方式，对`api.php`进行请求。

- 请求过程中会发送一个JSON文件，发送的`data`内容是`score:gameScore`。

- 而成绩是`gameScore`的值。

  
  
  

----

  

## burp

这个比较新（for me），刷新网站抓包得到`GET`请求头

```http

GET / HTTP/1.1

Host: 9ab09171-214d-4d99-aad4-4f4057a063da.node4.buuoj.cn:81

User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/118.0

Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8

Accept-Language: zh,zh-TW;q=0.8,zh-HK;q=0.6,en-US;q=0.4,en;q=0.2

Accept-Encoding: gzip, deflate

Connection: close

Upgrade-Insecure-Requests: 1

If-Modified-Since: Sun, 26 Mar 2023 07:18:44 GMT

If-None-Match: "2cb-5f7c86fc11d00-gzip"

```

然后就用burp的改变请求方式把它变成`POST`：

```http

POST /api.php HTTP/1.1

Host: 0d390052-3ebd-49f4-a519-064f1abd00bb.node4.buuoj.cn:81

User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/118.0

Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8

Accept-Language: zh,zh-TW;q=0.8,zh-HK;q=0.6,en-US;q=0.4,en;q=0.2

Accept-Encoding: gzip, deflate

Connection: close

Upgrade-Insecure-Requests: 1

Content-Type: application/x-www-form-urlencoded

Content-Length: 17

  

{"score":1000000}

```

发送之后就会得到flag。

  

----

  

## 修改页面代码

>控制台上执行代码，可以修改掉某个变量或者某个函数达到目）

  

- 在控制台输入`gameScore=100000`然后想办法让飞机最快速度坠毁就可以最快速度得到flag。<br>尝试直接修改js代码最开始的gameScore=100000但是没有效果。

  - 具体可以体现为，第一种后可以看到游戏界面的Score变成了100000，而第二种依旧是0；

  - 至于为什么无效，理解为js缓存的问题——游戏使用的仍然是之前的js缓存，如果刷新，得到的是从服务器发来的js缓存，人为修改的js没有上场机会。

- 参考：

<a href=https://learn.microsoft.com/zh-cn/microsoft-edge/devtools-guide-chromium/console/console-javascript>控制台运行JavaScript</a><br><a href=https://www.zhihu.com/question/30701118#>虽然是讨论chrome浏览器但完全可以套用在every</a>
