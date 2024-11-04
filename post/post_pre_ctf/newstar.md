---
title: 2023 Newstar CTF Begin of HTTP
subtitle: 
date: 2023-10-17T12:55:32+08:00
slug: 6168dd2
tags:
  - HTTP
categories:
  - CTF
---

OMG...居然是败给了一个POST&GET

  

# 1

感觉和之前做过的一个题目很像，可能出自同一个出题人之手。

<br>

按照要求进行GET传参后，需要进行POST传参。

于是我就在这里出现了问题。

我直接使用burp suite的repeater里面的change request method，请求头也从

  

<!-- more -->

  

```

GET /?ctf=1 HTTP/1.1

Host: node4.buuoj.cn:27986

Cache-Control: max-age=0

Upgrade-Insecure-Requests: 1

User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.5481.78 Safari/537.36

Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7

Accept-Encoding: gzip, deflate

Accept-Language: ja,zh-CN;q=0.9,zh;q=0.8

Cookie: power=hacker

Connection: close

```

  

变成

  

```

POST / HTTP/1.1

Host: node4.buuoj.cn:27986

Cache-Control: max-age=0

Upgrade-Insecure-Requests: 1

User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.5481.78 Safari/537.36

Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7

Accept-Encoding: gzip, deflate

Accept-Language: ja,zh-CN;q=0.9,zh;q=0.8

Cookie: power=hacker

Connection: close

Content-Type: application/x-www-form-urlencoded

Content-Length: 5

  

ctf=1

```

然后我把最后的`ctf=1`换成在Response界面找到的`secret=n3wst4rCTF2023g00000d`（解码后）<br>be like

```

POST / HTTP/1.1

...

  

secret=n3wst4rCTF2023g00000d

```

结果就是毫无反应。。。

修改为

```

POST /?ctf=1 HTTP/1.1

...

  

secret=n3wst4rCTF2023g00000d

```

跳转成功。。。

  

>POST请求是HTTP协议中的一种方法，用于将数据发送到服务器以创建或修改资源。

  

这两个请求的主要区别在于URL路径部分。

- 1. POST /?ctf=1 HTTP/1.1：这个请求将数据通过POST方法发送到服务器，并且附带了查询参数`ctf=1`。查询参数是通过URL的`?`符号后跟随的键值对形式的参数，用于向服务器传递额外的信息。

例如：`http://example.com/?ctf=1`

- 2. POST / HTTP/1.1：这个请求将数据通过POST方法发送到服务器，但没有附带任何查询参数。请求的URL路径只是根路径`/`，没有其他路径或参数信息。

   例如：`http://example.com/`

  

<font size=1>然后我一直在那里`POST /`....好心寒 </font>

  

# 2

  

## cookie

  

之后就是修改cookie。

如果你和我一样穷凶极恶（bushi）的扒拉请求头看的话就会非常清楚的记得一个name=power，value=hacker的cookie。

<br>尝试用cookie editor但是修改失败（不知道什么原因）所以继续用burp suite，将`power=hacker`修改为`power=ctfer`即可。

接着是指定浏览器。

  
  

## 指定浏览器

  

>User-Agent 首部包含了一个特征字符串，用来让网络协议的对端来识别发起请求的用户代理软件的应用类型、操作系统、软件开发商以及版本号。

  

浏览器通常使用的格式为：

```

User-Agent: Mozilla/<version> (<system-information>) <platform> (<platform-details>) <extensions>

```

原请求头中

```

POST /?ctf=1 HTTP/1.1

...

User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.5481.78 Safari/537.36

...

```

  

- 1.客户端类型：这个 User-Agent 字符串包含字符串 "Mozilla"，因为早期的 Netscape Navigator 浏览器是使用 Mozilla 代码库开发的。大多数现代浏览器仍然包含这个字符串，用于向服务器表明其兼容性以及其实际的应用程序类型。

- 2.操作系统(system-information)：这个 User-Agent 字符串指定了客户端正在运行的操作系统，它的值为 "Windows NT 10.0"，表示该客户端正运行在 Windows 10 操作系统上。架构(platform)：这个 User-Agent 字符串包含 "Win64"，说明客户端操作系统是 Windows 64 位系统。

- 3.软件版本(extensions)：这个 User-Agent 字符串还包含 Chrome 浏览器和 Safari 浏览器的版本信息。"Chrome/110.0.5481.78" 表示请求使用的 Chrome 浏览器版本为 110.0.5481.78，而 "Safari/537.36" 则表示 Safari 浏览器的版本号为 537.36。

  

我们按照要求直接把`User-Agent`字段全改为`NewStarCTF2023`，界面跳转，从`newstarctf.com`查找。

  

## Referer

  

>该请求头包含了当前请求页面的来源页面的地址，即表示当前页面是通过此来源页面里的链接进入的。服务端一般使用 Referer 请求头识别访问来源，可能会以此进行统计分析、日志记录以及缓存优化等。

  

接着按要求增加`Referer:newstarctf.com`

  

## 本地用户

>X-Real-IP:记录服务器的地址并且直接替换。X-Real-IP 通常被 HTTP 代理用来表示与它产生 TCP 连接的设备 IP，这个设备可能是其他代理，也可能是真正的请求端。需要注意的是，X-Real-IP 目前并不属于任何标准，代理和 Web 应用之间可以约定用任何自定义头来传递这个信息。

  

<a href=https://ixyzero.com/blog/archives/4088.html>参考（X-Forwarded-For & X-Real—IP）</a>

  

<br>本地用户->通用的IP->127.0.0.1

好的，又添加

```

X-Real-IP:127.0.0.1

```

  

## 结果

  

最后请求be like：

  

```

POST /?ctf=1 HTTP/1.1

Host: node4.buuoj.cn:27986

Cache-Control: max-age=0

Upgrade-Insecure-Requests: 1

User-Agent: NewStarCTF2023

Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7

Accept-Encoding: gzip, deflate

Accept-Language: ja,zh-CN;q=0.9,zh;q=0.8

Cookie: power=ctfer

Referer:newstarctf.com

X-Real-IP:127.0.0.1

Connection: close

Content-Type: application/x-www-form-urlencoded

Content-Length: 28

  

secret=n3wst4rCTF2023g00000d

```

  

响应就会把flag告诉你。

  

# 3

  

觉得应该再读*网络是怎样连接的*这本书。

<a href=https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Referer>一个学习的网站</a>

<br>其实后面的都不难，把我绊倒的是`POST /`和`POST /?ctf=1`。

  

<font size=1>好心寒，不小心把`<br>`打成`<be>`导致后面一片mk现原形。删删改改以为又抽风...</font>
