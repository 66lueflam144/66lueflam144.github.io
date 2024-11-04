---
title: SSRF Me
subtitle: De1CTF 2019
date: 2024-01-22T10:19:02+08:00
slug: 77d67ac
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
  - SSRF
categories:
  - CTF
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

之前失败过的。

  

<!--more-->

  
  

# Start Python？

  

打开URL就是这么一个东西，不过可以看出来是个python flask 代码。

  

![start](../post_pre/pics/ssrfme/ssrf1.png)

  

找专业人士快速排版一下：

  

```python

#! /usr/bin/env python

# encoding=utf-8

from flask import Flask, request

import socket

import hashlib

import urllib

import sys

import os

import json

  

reload(sys)

sys.setdefaultencoding('latin1')

  

app = Flask(__name__)

secret_key = os.urandom(16)

  
  

class Task:

    def __init__(self, action, param, sign, ip):

        self.action = action

        self.param = param

        self.sign = sign

        self.sandbox = md5(ip)

        if not os.path.exists(self.sandbox):  # SandBox For Remote_Addr

            os.mkdir(self.sandbox)

  

    def Exec(self):

        result = {}

        result['code'] = 500

  

        if self.checkSign():

            if "scan" in self.action:

                tmpfile = open("./%s/result.txt" % self.sandbox, 'w')

                resp = scan(self.param)

                if resp == "Connection Timeout":

                    result['data'] = resp

                else:

                    print resp

                    tmpfile.write(resp)

                tmpfile.close()

                result['code'] = 200

  

            if "read" in self.action:

                f = open("./%s/result.txt" % self.sandbox, 'r')

                result['code'] = 200

                result['data'] = f.read()

  

        if result['code'] == 500:

            result['data'] = "Action Error"

        else:

            result['code'] = 500

            result['msg'] = "Sign Error"

  

        return result

  

    def checkSign(self):

        if getSign(self.action, self.param) == self.sign:

            return True

        else:

            return False

  
  

# generate Sign For Action Scan.

@app.route("/geneSign", methods=['GET', 'POST'])

def geneSign():

    param = urllib.unquote(request.args.get("param", ""))

    action = "scan"

    return getSign(action, param)

  
  

@app.route('/De1ta', methods=['GET', 'POST'])

def challenge():

    action = urllib.unquote(request.cookies.get("action"))

    param = urllib.unquote(request.args.get("param", ""))

    sign = urllib.unquote(request.cookies.get("sign"))

    ip = request.remote_addr

  

    if waf(param):

        return "No Hacker!!!!"

  

    task = Task(action, param, sign, ip)

    return json.dumps(task.Exec())

  
  

@app.route('/')

def index():

    return open("code.txt", "r").read()

  
  

def scan(param):

    socket.setdefaulttimeout(1)

    try:

        return urllib.urlopen(param).read()[:50]

    except:

        return "Connection Timeout"

  
  

def getSign(action, param):

    return hashlib.md5(secret_key + param + action).hexdigest()

  
  

def md5(content):

    return hashlib.md5(content).hexdigest()

  
  

def waf(param):

    check = param.strip().lower()

    if check.startswith("gopher") or check.startswith("file"):

        return True

    else:

        return False

  
  

if __name__ == '__main__':

    app.debug = False

    app.run(host='0.0.0.0', port=80)

```

  

这个代码主要分成三个部分，根据python的函数调用顺序导致的代码位置，我们**从下到上**看

  
  

## 第一部分

  
  

```python

def scan(param):

    # 设置套接字超时时间为1s

    socket.setdefaulttimeout(1)

    try:

      # 尝试打开param参数指定的URL，并读取内容，返回前50个字符

        return urllib.urlopen(param).read()[:50]

    except:

      # 打开异常则捕获异常返回报错信息

        return "Connection Timeout"

  
  

def getSign(action, param):

  # 用hashlib.md5对secret_key、param、action进行哈希计算并以十六进制字符串形式返回

    return hashlib.md5(secret_key + param + action).hexdigest()

  
  

def md5(content):

    return hashlib.md5(content).hexdigest()

  
  

def waf(param):

  # 用strip()移除字符串两段的空白符，用lower()将字符串全转为小写

    check = param.strip().lower()

    # 检查处理过的字符串是否以gopher 或者 file开头，进行安全性判断

    if check.startswith("gopher") or check.startswith("file"):

        return True

    else:

        return False

  
  

if __name__ == '__main__':

    app.debug = False

    app.run(host='0.0.0.0', port=80)

```

  

- `scan(param)`用于从指定的URL中读取内容返回前50个字符，如果有异常就报错。

- `getSign(action, param)`用于生成给定的参数的相关哈希值并以十六进制字符串形式返回。即创造签名(Sign)

- `md5(content)`和上一个一样，对content进行生成哈希值并以十六进制字符串形式返回。

- `waf(param)`进行对param参数的安全性检查

- `if __name__ == '__main__'`作用是在脚本被直接执行时，将 Flask 应用的调试模式关闭，并在 0.0.0.0:80 上启动应用。

  

<hr>

  

## 第二部分

  

```python

# generate Sign For Action Scan.

# 指定该函数处理的路由，即/geneSign的GET和POST请求

@app.route("/geneSign", methods=['GET', 'POST'])

def geneSign():

  # 从请求中获取param参数，并进行解码

    param = urllib.unquote(request.args.get("param", ""))

    action = "scan"

    # 用getSign函数对参数进行处理并返回，由上面可知会返回一个十六进制字符串

    return getSign(action, param)

  
  

@app.route('/De1ta', methods=['GET', 'POST'])

def challenge():

  # 从请求中获取action、param、sign三个参数

  # action和sign在cookie中，param是参数

    action = urllib.unquote(request.cookies.get("action"))

    param = urllib.unquote(request.args.get("param", ""))

    sign = urllib.unquote(request.cookies.get("sign"))

    # 获取请求的远程地址

    ip = request.remote_addr

    # 用waf()函数对param进行安全性检查，确保没有ghoper或者file

    if waf(param):

        return "No Hacker!!!!"

    # 创建Task类实例

    task = Task(action, param, sign, ip)

    # 调用 Task 实例的 Exec 方法，将结果使用 json.dumps 转换为 JSON 格式并返回。

    return json.dumps(task.Exec())

```

  

- `geneSign()`对/geneSign的参数param进行处理，返回一个十六进制字符串

- `challenge()`接收来自客户端的请求，解析请求中的参数和 cookie，进行一些条件检查（使用 waf 函数），然后创建 Task 实例并调用其 Exec 方法，最后将结果以 JSON 格式返回。

  
  

<hr>

  

## 第三部分

  
  

```python

class Task:

    def __init__(self, action, param, sign, ip):

        self.action = action

        self.param = param

        self.sign = sign

        self.sandbox = md5(ip)

        if not os.path.exists(self.sandbox):  # SandBox For Remote_Addr

            os.mkdir(self.sandbox)

  

    def Exec(self):

        result = {}

        result['code'] = 500

  

        if self.checkSign():

            if "scan" in self.action:

                tmpfile = open("./%s/result.txt" % self.sandbox, 'w')

                resp = scan(self.param)

                if resp == "Connection Timeout":

                    result['data'] = resp

                else:

                    print resp

                    tmpfile.write(resp)

                tmpfile.close()

                result['code'] = 200

  

            if "read" in self.action:

                f = open("./%s/result.txt" % self.sandbox, 'r')

                result['code'] = 200

                result['data'] = f.read()

  

        if result['code'] == 500:

            result['data'] = "Action Error"

        else:

            result['code'] = 500

            result['msg'] = "Sign Error"

  

        return result

  

    def checkSign(self):

        if getSign(self.action, self.param) == self.sign:

            return True

        else:

            return False

```

  

### apart

  
  

```python

def checkSign(self):

        if getSign(self.action, self.param) == self.sign:

            return True

        else:

            return False

```

  

如果getSign()函数通过`action`，`param`创造的Sign和实例的sign进行比较，匹配就True。

  
  

<hr>

  

```python

def Exec(self):

        # 字典result，将code键值设为500

        result = {}

        result['code'] = 500

        # 如果sign值匹配

        if self.checkSign():

            # 如果 action字符串中有scan

            if "scan" in self.action:

                # 以w模式打开result.txt，%s替换为self.sandbox的值

                tmpfile = open("./%s/result.txt" % self.sandbox, 'w')

                # 用scan函数获取param解析出的前50个字符

                resp = scan(self.param)

                # 如果内容是

                if resp == "Connection Timeout":

                    result['data'] = resp

                else:

                    print resp

                    # 将resp写入tempfile

                    tmpfile.write(resp)

                tmpfile.close()

                result['code'] = 200

            # 如果action字符串中有read

            if "read" in self.action:

                # 以r模式打开result.txt，%s替换为self.sandbox属性的值

                f = open("./%s/result.txt" % self.sandbox, 'r')

                result['code'] = 200

                # 将读取的内容存入result['data']

                result['data'] = f.read()

  

        if result['code'] == 500:

            result['data'] = "Action Error"

        else:

            result['code'] = 500

            result['msg'] = "Sign Error"

        # 返回最后的字典，包括着result['code']、result['data']

        return result

```

  

`if self.checkSign()`用于检查Sign是否有效，有效就往下执行：

  

- `action`中包含`scan`，则打开`result.txt`将scan(param)结果写入。

- `action`中包含`read`，则打开`result.txt`，将其内容读取存入`result[data]`并返回

  
  

<hr>

  

```python

def __init__(self, action, param, sign, ip):

        self.action = action

        self.param = param

        self.sign = sign

        self.sandbox = md5(ip)

        if not os.path.exists(self.sandbox):  # SandBox For Remote_Addr

            os.mkdir(self.sandbox)

```

  

在创建 Task类 进行实例化的时候，用`action`、`param`、`sign`、`ip`四个属性进行初始化。

  

`self.sandbox = mad5(ip)`sandbox接收`ip`的MD5哈希值。

  

最后一个if语句是判断是否存在`self.sandbox`，不存在就创建一个。

  
  

# 流程

  

大概思路就是在 `/De1ta` 中 get param ，cookie action sign 去读取 flag.txt，其中，`param=flag.txt`，`action` 中要含有 `read` 和 `scan`，且 `sign=md5(secert_key + param + action)`

  

<hr>

  

`param`指定需要访问的文件

`action`用来指定想要的`result['data']`：

  

- `scan`得到`result['code]=200`和写入`result.txt`的param指定文件前50个字符

- `read`得到`result['code]=200`和`reuslt['data']`

  

因为checkSign()函数，所以要先构造可以通过检查的sign。

  

sign由action和param经过getSign()得到。

  

调用了getSign()的是/geneSign路径

  

<hr>

  

我们想要得到有scan和read的sign，但只是`/geneSign?param=flag.txt`得到的只是有scan的sign，即`md5(secret_keyflag.txtscan)`。

  

so，`/geneSign?param=flag.txtread`，得到`md5(secret_keyflag.txtreadscan)`

  

得到我们需要的sign。

  

<hr>

  

然后通过`/De1ta`进行下一步

  

需要注意一点

  

```python

@app.route('/De1ta', methods=['GET', 'POST'])

def challenge():

  # 从请求中获取action、param、sign三个参数

  # action和sign在cookie中，param是参数

    action = urllib.unquote(request.cookies.get("action"))

    param = urllib.unquote(request.args.get("param", ""))

    sign = urllib.unquote(request.cookies.get("sign"))

```

  

即，请求中要加入有`action`和`sign`的cookie

  

action和sign都是是匹配着`scan`和`read`的。

  

所以修改修改修改：

  

![](../post_pre/pics/ssrfme/ssrf3.png)

  

![](../post_pre/pics/ssrfme/ssrf2.png)
