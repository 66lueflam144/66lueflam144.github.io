---
title: A Simple Web Server
subtitle: 翻译的一篇久远文章
date: 2024-01-18T10:21:17+08:00
slug: 76a9876
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
  - WEB
categories:
  - PYTHON
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

翻译了一下翻到的一篇关于用python写一个简单web server的[文章](https://aosabook.org/en/500L/a-simple-web-server.html)，看起来真的是闲出屁了。

  

<!--more-->

  
  

# 用500行（或少于）来实现一个简单的Web Server

  
  
  
  
  

## Introduction

  

网络在过去的二十年内以无数种方式改变着社会，但它的核心却只发生了很少的改变。许多系统依旧遵循着Tim Berners-Lee在四分之一个世纪之前设下的规则。尤其是web server，从过去到现在，它们依旧处理着相同的信息，并且是以同种方式。

  

该篇章将会探索它们如何处理信息。同时，也会探索开发者们如何创造 不必为了增加新功能而重写的 软件系统。

  

## Background

  

web上每一个程序都运行在Internet Protocol(IP)上——一系列通信标准。在这些标准中，我们最常接触的是Transmission Control Protocol(TCP/IP)，它让计算机之间的交流看起来像 reading(读取)和writing(写入)。

  

### sockets

  

使用IP的程序通过**sockets**(套接字)进行通信。每一个socket是一个点对点的通信通道的一端，就像一部手机是一个call的一端一样。

  

一个socket由一个定义了一台特定的计算机的**IP Address**和一个该计算机上的**port number**组成。

  

- IP Address由四个8-bit数字组成，比如 174.136.14.108。域名系统(Domain Name System)即DNS会将这些数字匹配到symbolic names，比如`example.org`。

  

- port number（端口数字）是定义主机上的套接字的数字（范围：0-65535）

如果说IP Address是一个公司的号码，那么Port number就是一个扩展。

特别的port 0-1023是操作系统使用的。

  

### HTTP

  

**The** **Hypertext** **Transfer** **Protocol**即**HTTP**描述了一种程序可以通过IP交换数据的途径,HTTP比较容易理解：客户端(client)发送一个请求(request)，指定client想要通过套接字的连接得到什么数据，然后服务端(server)就将这些数据作为响应发送回去。这些数据可能是从磁盘上复制的一个文件，也可能是程序动态生成的，或者两者的结合。

  

关于**HTTP请求**最重要的是，它只是文本(text)：任何程序都可以创造或者解析一个。

为了方便理解，这些文本必须包含以下部分：

  

![](../post_pre/pics/server/http-request.png)

  

- HTTP method 几乎要么是“GET”(获取信息)，要么是“POST”(提交表单数据或上传文件)

  

- URL 指定客户端想要的：它总是打开一个到磁盘上某个文件的路径，比如`/research/experiments.html`，但这完全取决于server想要怎么处理它（关键部分）。

  

- HTTP version 通常是 "HTTP/1.0" 或者 "HTTP/1.1"，两者的区别对我们来说意义不大。

  
  

而**HTTP响应**形式如下

  

![](../post_pre/pics/server/re.png)

  

version、headers以及body有着相同的形式和意义。

状态码(status code)是一种表明当请求在处理的时候发生了什么的数字：`200`意味着“一切都正常，按照预期进行了处理”，`404`意味着“没有找到”。不同的状态码有不同的意思，

  

对于这一章节的目的，我们只需要知道关于HTTP的另外两件事。

  

首先是 HTTP 是无状态的。每一个请求都是单独处理的，server并不会记忆任何一个请求和其下一个请求间的任何内容。如果一个应用(application)想要保持追踪某些东西比如一个用户的身份，那么它必须自己执行操作。

  

执行上述操作的比较常见的方式是使用 **cookie**，cookie是一种server发送给client、然后client会在稍后发送回server的短字符串。当一个用户执行某些需要请求在多个请求之间保存状态的功能时，server就会创造一个新的cookie，并将其储存在数据库(database)中，然后发送给它的浏览器。每一次浏览器将cookie发送回来的时候，server就会用这个cookie来查找用户正在做的事的相关信息。

  

其次是我们需要知道HTTP是，一个URL可以补充参数进而提供更多信息。比如，我们在使用搜索引擎，我们必须指定我们的搜索词是什么。我们将这些增加到URL的路径中，但我们应该做的是在URL中增加参数。

我们通过增加`?`到后跟被`&`成对分开的'key=value'的URL中来做到。比如`http://www.google.ca?q=Python`向Google请求搜索有关于Python1的网页，其中，key是`q`，value是`Python`。

更长的查询语句比如`http://www.google.ca/search?q=Python&amp;client=Firefox`则告诉Google请求者在使用Firefox等等。我们可以传递任何我们想要的参数，但哪一个值得重点关注、如何解析它们，都取决于这个web上运行的应用的决定。

  

当然，如果`?`和`&`是特别的字符，那么这里肯定有一种方式去逃脱它们，就像肯定有一种方式能将`"`放入由`"`分隔的字符串中一样。URL的编码标准使用`%`后跟2位代码来表示特殊字符，并用`+`来替换空白。因此，想要在Google上搜索"grade = A+"，就要使用`http://www.google.ca/search?q=grade+%3D+A%2B`。

  

开放的套接字，构造HTTP请求。解析响应很无聊，所以大多数人使用libraries来多大多数工作。Python有一个叫做`urllib2`的library（是`urllib`的替代品），但是它显露了很多大多数人完全不在乎的管道(plumbing)。Request是一个较于urllib2更易于使用的library。这里有一个使用了Request去下载AOSA book site一个网页的例子：

  

```Python

import requests

response = requests.get('http://aosabook.org/en/500L/web-server/testpage.html)

printf('status code:', response.status_code)

printf('content length:', response.headers['content-length'] )

printf(response.text)

```

  

得到结果是：

  

```txt

status code: 200

content length: 61

<html>

  <body>

    <p>Test page.</p>

  </body>

</html>

```

  

`request.get`发送一个HTTP GET请求到server然后返回一个包含着回应的物体object。这个obje的`status_code`成员是response的status，`content_length`成员是存在于响应中的字节数，`text`是实际上的数据（在这里是一个HTML page）。

  

# Hello Web

  

现在，我们准备好编写我们的第一个简单的web server了。基本思路很简单：

  

- 1.等待某人连接我们的server然后发送一个HTTP请求

  

- 2.解析请求

  

- 3.弄清楚它在请求什么

  

- 4.匹配查找数据（或者动态生成）

  

- 5.将数据格式化为HTML

  

- 6.发送回去

  
  

**1，2，6**都是相同的，从一个应用到另一个应用，所以Python标准库有一个叫做`BaseHTTPServer`(在Pyhton3中修改为http.server)的module可以为我们完成这项任务。我们只需要关注**3，4，5**

  
  
  

```python

import http.server

from http.server import BaseHTTPRequestHandler,HTTPServer

  

class RequestHandler(http.server.BaseHTTPRequestHandler):

    '''Handle HTTP request by returning a fixed 'page'.'''

    page = '''\

<html>

<body>

<p>Hello, web!</p>

</body>

</html>

'''

  
  

    def do_GET(self):

        self.send_response(200)

        self.send_header("Content-Type","text/html")

        self.send_header("Content-Length", str(len(self.page)))

        self.end_headers()

        self.wfile.write(self.page.encode('utf-8'))

  
  

if __name__ == '__main__':

    serverAddress = ('', 8080)

    server = http.server.HTTPServer(serverAddress, RequestHandler)

    server.serve_forever()

  

```

  

![](../post_pre/pics/server/web1.png)

  

第一行很简单：自从我们不再请求一个特定的文件后，我们的浏览器要求输入`/`（无论哪种server中都代表着root directory）。

第二行出现的原因是我们的浏览器自动的发送一个“第二个请求”，目的是请求一个图片文件"/favicon.ico"，如果它存在，就会作为一个icon在地址栏出现。

  
  

# Displaying Values

  

让我们修改以下我们的web server来展示一些HTTP请求中的值。（在debugging的时候我们会很频繁的执行这个操作，所以我们需要一些练习）

为了保持code clean，我们将创建页面和发送页面分开。

  

```python

import http.server

  

class RequestHandler(http.server.BaseHTTPRequestHandler):

    '''Handle HTTP request by returning a fixed 'page'.'''

    #在这个网页模板里，我们想要显示的是一个包含了格式化占位符的HTML table

    page = '''\

<html>

<body>

<table>

<tr> <td>Header</td> <td>Value</td></tr>

<tr> <td>Date and Time</td> <td>{date_time}</td></tr>

<tr> <td>client host</td> <td>{client_host}</td></tr>

<tr> <td>client port</td> <td>{client_port}</td></tr>

<tr> <td>Command</td> <td>{command}</td></tr>

<tr> <td>Path</td> <td>{path}</td></tr>

</table>

<p>Hello, web!</p>

</body>

</html>

'''

  

    def do_GET(self):

        page = self.create_page()

        self.send_page(page)

  

    def create_page(self):

        value = {

            'date_time': self.date_time_string(),

            'client_host': self.client_address[0],

            'client_port': self.client_address[1],

            'command': self.command,

            'path': self.path

        }

        page = self.page.format(**value)

        return page

  

    #和之前那个一样

    def send_page(self, page):

        self.send_response(200)

        self.send_header("Content-Type", "text/html")

        self.send_header("Content-Length", str(len(page)))

        self.end_headers()

        self.wfile.write(page.encode('utf-8'))

  

if __name__ == '__main__':

    serverAddress = ('', 8080)

    server = http.server.HTTPServer(serverAddress, RequestHandler)

    server.serve_forever()

  

```

  
  

这个程序的主体body并没有改变：之前，它创建了一个有address和request handler作为参数的`http.server`的类的实例，然后servers保持请求。如果我们运行它并且从浏览器发送一个请求到`http://localhost:8080/something.html`，那么我们会得到

  

![](../post_pre/pics/server/web2.png)

  

请注意，我们没有得到一个404错误，即使`something.html`不存在。这是因为一个wewb server只是一个程序，当得到一个请求的时候，它可以做任何它想要的。：发送回去在先前的请求中命名的文件，提供随机选择的Wikipedia 网页或者我们编写的内容。

  

<hr>

  

# Serving Static Pages

  

很明显，下一步是从磁盘提供页面而不是动态生成。

我们将重写`do_GET`

  

```python

 def do_GET(self):

        try:

  

            # Figure out what exactly is being requested.

            full_path = os.getcwd() + self.path

  

            # It doesn't exist...

            if not os.path.exists(full_path):

                raise ServerException("'{0}' not found".format(self.path))

  

            # ...it's a file...

            elif os.path.isfile(full_path):

                self.handle_file(full_path)

  

            # ...it's something we don't handle.

            else:

                raise ServerException("Unknown object '{0}'".format(self.path))

  

        # Handle errors.

        except Exception as msg:

            self.handle_error(msg)

```

这个方法假设它被允许提供它所运行在的web的目录或者目录下的任何文件(用`os.getcwd`实现)。它结合URL中提供的路径来获取用户想要的文件的路径（库自动放入`self.path`中，并且始终以`/`开头）。

  

如果文件不存在或者不是一个文件，这个方法会通过引发和捕获异常进行报错。另一方面，如果匹配到一个文件，就会调用一个名为`handle_file`的辅助方法来读取和返回内容(contents)。这个方法只是读取文件以及使用存在的`send_content`方法去发送回客户端：

  

```python

def handle_file(self, full_path):

        try:

            with open(full_path, 'rb') as reader:

                content = reader.read()

            self.send_content(content)

        except IOError as msg:

            msg = "'{0}' cannot be read: {1}".format(self.path, msg)

            self.handle_error(msg)

```

请注意，我们以二进制模式`rb`打开文件，这样Python就不会试图通过改变看起来像Windows行结尾的字节序列来“帮助”我们。同时，当提供服务时将整个文件内容读入内存是一个bad idea，因为文件可能很大。不过处理这种情况超出本章的范围。

  

为了完成整个类(class)，我们需要编写错误处理方法以及报告错误的页面模板。

  
  

```python

 Error_Page = """\

        <html>

        <body>

        <h1>Error accessing {path}</h1>

        <p>{msg}</p>

        </body>

        </html>

        """

  

    def handle_error(self, msg):

        content = self.Error_Page.format(path=self.path, msg=msg)

        self.send_content(content)

```

  

这个程序有效，如果我们不细看。这里的问题是：它总是返回200状态码，即使请求的页面不存在。在这种情况下网页也会发回一个包含错误的信息，但我们的浏览器并不懂人类语言，它不知道这个请求实际上失败了。为了让其更清楚，我们需要修改`handle_error`和`send_content`：

  

```python

# Handle unknown objects.

    def handle_error(self, msg):

        content = self.Error_Page.format(path=self.path, msg=msg)

        self.send_content(content, 404)

  

    # Send actual content.

    def send_content(self, content, status=200):

        self.send_response(status)

        self.send_header("Content-type", "text/html")

        self.send_header("Content-Length", str(len(content)))

        self.end_headers()

        self.wfile.write(content)

```

  

请注意当一个文件没有被找到的时候，我们并没有触发`ServerException`，而是生成了一个错误界面。`ServerException`是旨在表示web server代码内部存在的错误，即有些地方我们写错了。另一方面，当用户端出现错误的时候（即发送一个并不存在的文件的URL），错误界面由`handle_error`创建。

  

下面是Python3版本的完整代码：

  

```python

import http.server

import os.path

  

class ServerException(Exception):

    pass

  

class RequestHandler(http.server.BaseHTTPRequestHandler):

    '''Handle HTTP request by returning a fixed 'page'.'''

    page = '''\

<html>

<body>

<table>

<tr> <td>Header</td> <td>Value</td></tr>

<tr> <td>Date and Time</td> <td>{date_time}</td></tr>

<tr> <td>client host</td> <td>{client_host}</td></tr>

<tr> <td>client port</td> <td>{client_port}</td></tr>

<tr> <td>Command</td> <td>{command}</td></tr>

<tr> <td>Path</td> <td>{path}</td></tr>

</table>

<p>Hello, web!</p>

</body>

</html>

'''

  

    def do_GET(self):

        try:

            full_path = os.getcwd() + self.path

  

            if not os.path.exists(full_path):

                raise ServerException("'{0}' not found".format(self.path))

            elif os.path.isfile(full_path):

                self.handle_file(full_path)

            else:

                raise ServerException("Unknown object '{0}'".format(self.path))

        except Exception as msg:

            self.handle_error(msg)

  
  

    def handle_file(self, full_path):

        try:

            with open(full_path, 'rb') as reader:

                content = reader.read()

            self.send_content(content)

        except IOError as msg:

            msg = "'{0}' cannot be read: {1}".format(self.path, msg)

            self.handle_error(msg)

  

    Error_Page ="""\

    <html>

    <body>

    <h1>Error accessing {path}</h1>

    <p>{msg}</p>

    </body>

    </html>

    """

  

    def handle_error(self, msg):

        content = self.Error_Page.format(path=self.path, msg=msg)

        self.send_content(content, 404)

  

    def send_content(self, content, status=200):

        self.send_response(status)

        self.send_header("Content-Type", "text/html")

        self.send_header("Content-Length", str(len(content)))

        self.end_headers()

        self.wfile.write(content.encode('utf-8'))

  

    def create_page(self):

        value = {

            'date_time': self.date_time_string(),

            'client_host': self.client_address[0],

            'client_port': self.client_address[1],

            'command': self.command,

            'path': self.path

        }

        page = self.page.format(**value)

        return page

  

    def send_page(self, page):

        self.send_response(200)

        self.send_header("Content-Type", "text/html")

        self.send_header("Content-Length", str(len(page)))

        self.end_headers()

        self.wfile.write(page.encode('utf-8'))

  

if __name__ == '__main__':

    serverAddress = ('', 8080)

    server = http.server.HTTPServer(serverAddress, RequestHandler)

    server.serve_forever()

```

  

# Listing Directories

  

我们的下一步，在URL是一个目录的路径而不是一个文件的的时候，我们可以教我们的web server显示包含目录内容的列表。我们也可以更进一步，在目录中查找一个index.html文件然后显示它，并且如果文件不存在就只显示目录列表。

  

但是将这些规则写入`do_GET`是一个错误，因为resulting方法是一长串的控制特别行为的if语句。正确的解决方案是后退去解决生成问题，也就是如何处理URL。这里重写了`do_GET`：

  

```python

def do_GET(self):

        try:

  

            # Figure out what exactly is being requested.

            self.full_path = os.getcwd() + self.path

  

            # Figure out how to handle it.

            for case in self.Cases:

                handler = case()

                if handler.test(self):

                    handler.act(self)

                    break

  

        # Handle errors.

        except Exception as msg:

            self.handle_error(msg)

```

  

第一步一样：找到被请求的东西的完整的路径，在那之后，代码看起来完全不一样。比起一系列inline测试，这个版本将一组cases循环存储在列表中。每一个case是有两个方法的object，两个方法分别是`test`和`act`，前者告诉我们是否可以处理这个请求，后者进行一些操作。只要我们找到正确的case，我们就让它处理请求、跳出循环。

  

这3个case类重制了我们之前的web server的行为：

  

```python

class case_no_file(object):

    '''File or directory does not exist.'''

  

    def test(self, handler):

        return not os.path.exists(handler.full_path)

  

    def act(self, handler):

        raise ServerException("'{0}' not found".format(handler.path))

  
  

class case_existing_file(object):

    '''File exists.'''

  

    def test(self, handler):

        return os.path.isfile(handler.full_path)

  

    def act(self, handler):

        handler.handle_file(handler.full_path)

  
  

class case_always_fail(object):

    '''Base case if nothing else worked.'''

  

    def test(self, handler):

        return True

  

    def act(self, handler):

        raise ServerException("Unknown object '{0}'".format(handler.path))

and here's how we construct the list of case handlers at the top of the RequestHandler class:

  

class RequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):

    '''

    If the requested path maps to a file, that file is served.

    If anything goes wrong, an error page is constructed.

    '''

  

    Cases = [case_no_file(),

             case_existing_file(),

             case_always_fail()]

  

    ...everything else as before...

```

  

现在，在表面上我们已经让我们的web server更复杂了：文件从74行增加到99行，以及一个新的间接级别，同时没有任何新的功能。当我们回退到本章开头的教server为目录提供index.html页面（如果存在），并且一个目录列表不存在的时候，它的好处就体现出来了。

  

之前的handler是：

  

```python

class case_directory_index_file(object):

    '''Serve index.html page for a directory.'''

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        return os.path.isdir(handler.full_path) and \

               os.path.isfile(self.index_path(handler))

  

    def act(self, handler):

        handler.handle_file(self.index_path(handler))

```

  

现在，辅助方法`index_path`构建了到`index.html`的路径，将其放入case handler防止混乱出现在`main Requesthandler`中。`test`检查路径是否指向一个包含`index.html` page的目录，`act`调用main request handler为这个page提供服务。

  

`RequestHandler`所需要的唯一的改变是将一个`case_directory_index_file`实体（object）添加到我们的`Cases`列表中：

  
  

```python

 Cases = [case_no_file(),

             case_existing_file(),

             case_directory_index_file(),

             case_always_fail()]

```

  

如果是不包含`index.html`的目录呢？和上面那个一样：

  

```python

class case_directory_no_index_file(object):

    '''Serve listing for a directory without an index.html page.'''

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        return os.path.isdir(handler.full_path) and \

               not os.path.isfile(self.index_path(handler))

  

    def act(self, handler):

        ???

```

  

但`act`呢？

看起来我们把自己绕进了一个角落里。逻辑上，`act`方法应该创造并返回目录列表，但我们现存的代码并不允许这样的操作。`RequestHandler.do_GET`调用`act`，但不想要或者处理一个从它那里返回的值。

现在，让我们给`RequestHandler`增加一个方法以生成一个目录列表，并且从case handler的`act`中调用它。

  

```python

class case_directory_no_index_file(object):

    '''Serve listing for a directory without an index.html page.'''

  

    # ...index_path and test as above...

  

    def act(self, handler):

        handler.list_dir(handler.full_path)

  
  

class RequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):

  

    # ...all the other code...

  

    # How to display a directory listing.

    Listing_Page = '''\

        <html>

        <body>

        <ul>

        {0}

        </ul>

        </body>

        </html>

        '''

  

    def list_dir(self, full_path):

        try:

            entries = os.listdir(full_path)

            bullets = ['<li>{0}</li>'.format(e)

                for e in entries if not e.startswith('.')]

            page = self.Listing_Page.format('\n'.join(bullets))

            self.send_content(page)

        except OSError as msg:

            msg = "'{0}' cannot be listed: {1}".format(self.path, msg)

            self.handle_error(msg)

```

  

完整代码：

  

```python

import http.server

import os.path

import urllib.parse

  

class ServerException(Exception):

    pass

  

class case_no_file(object):

    '''File or directory does not exist.'''

  

    def test(self, handler):

        return not os.path.exists(handler.full_path)

  

    def act(self, handler):

        raise ServerException("'{0}' not found".format(handler.path))

  

class case_existing_file(object):

    '''File exists.'''

  

    def test(self, handler):

        return os.path.isfile(handler.full_path)

  

    def act(self, handler):

        handler.handle_file(handler.full_path)

  

class case_directory_index_file(object):

    '''Serve index.html page for a directory.'''

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        return os.path.isdir(handler.full_path) and \

               os.path.isfile(self.index_path(handler))

  

    def act(self, handler):

        handler.handle_file(self.index_path(handler))

  

class case_directory_no_index_file(object):

    '''Serve listing for a directory without an index.html page.'''

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        return os.path.isdir(handler.full_path) and \

               not os.path.isfile(self.index_path(handler))

  

    def act(self, handler):

        handler.list_dir(handler.full_path)

  

class case_always_fail(object):

    '''Base case if nothing else worked.'''

  

    def test(self, handler):

        return True

  

    def act(self, handler):

        raise ServerException("Unknown object '{0}'".format(handler.path))

  

class RequestHandler(http.server.BaseHTTPRequestHandler):

    '''

    If the requested path maps to a file, that file is served.

    If anything goes wrong, an error page is constructed.

    '''

    page = '''\

    <html>

    <body>

    <table>

    <tr> <td>Header</td> <td>Value</td></tr>

    <tr> <td>Date and Time</td> <td>{date_time}</td></tr>

    <tr> <td>client host</td> <td>{client_host}</td></tr>

    <tr> <td>client port</td> <td>{client_port}</td></tr>

    <tr> <td>Command</td> <td>{command}</td></tr>

    <tr> <td>Path</td> <td>{path}</td></tr>

    </table>

    <p>Hello, web!</p>

    </body>

    </html>

    '''

  

    Error_Page = """\

        <html>

        <body>

        <h1>Error accessing {path}</h1>

        <p>{msg}</p>

        </body>

        </html>

        """

  

    Cases = [case_no_file(),

             case_existing_file(),

             case_directory_index_file(),

             case_directory_no_index_file(),

             case_always_fail()]

  

    Listing_Page = '''\

        <html>

        <body>

        <ul>

        {0}

        </ul>

        </body>

        </html>

        '''

  

    def list_dir(self, full_path):

        try:

            entries = os.listdir(full_path)

            bullets = ['<li>{0}</li>'.format(e)

                for e in entries if not e.startswith('.')]

            page = self.Listing_Page.format('\n'.join(bullets))

            self.send_content(page)

        except OSError as msg:

            msg = "'{0}' cannot be listed: {1}".format(self.path, msg)

            self.handle_error(msg)

  

    def handle_error(self, msg):

        content = self.Error_Page.format(path=self.path, msg=msg)

        self.send_content(content, 404)

  

    def send_content(self, content, status=200):

        self.send_response(status)

        self.send_header("Content-Type", "text/html")

        self.send_header("Content-Length", str(len(content)))

        self.end_headers()

        self.wfile.write(content.encode('utf-8'))

  

    def create_page(self):

        value = {

            'date_time': self.date_time_string(),

            'client_host': self.client_address[0],

            'client_port': self.client_address[1],

            'command': self.command,

            'path': self.path

        }

        page = self.page.format(**value)

        return page

  

    def send_page(self, page):

        self.send_response(200)

        self.send_header("Content-Type", "text/html")

        self.send_header("Content-Length", str(len(page)))

        self.end_headers()

        self.wfile.write(page.encode('utf-8'))

  

    def do_GET(self):

        try:

            # Figure out what exactly is being requested.

            self.full_path = os.getcwd() + urllib.parse.unquote(self.path)

  

            # Figure out how to handle it.

            for CaseClass in self.Cases:

                handler = CaseClass

                if handler.test(self):

                    handler.act(self)

                    break

  

        # Handle errors.

        except Exception as msg:

            self.handle_error(msg)

  
  

if __name__ == '__main__':

    server_address = ('', 8080)

    server = http.server.HTTPServer(server_address, RequestHandler)

    server.serve_forever()

  

```

  

<hr>

  

# The CGI Protocol

  

当然，大多数人不想为了添加新功能而编辑 Web 服务器的源代码。为了避免这样做，服务器始终支持一种称为通用网关接口 (CGI) 的机制，它为 Web 服务器提供运行外部程序以满足请求的标准方法。

  

例如，假设我们希望服务器能够在 HTML 页面中显示当地时间。我们只需几行代码就可以在独立程序中完成此操作：

  

```python

from datetime import datetime

print '''\

<html>

<body>

<p>Generated {0}</p>

</body>

</html>'''.format(datetime.now())

```

  

为了让web server运行它，我们增加case handler：

  

```python

class case_cgi_file(object):

    '''Something runnable.'''

  

    def test(self, handler):

        return os.path.isfile(handler.full_path) and \

               handler.full_path.endswith('.py')

  

    def act(self, handler):

        handler.run_cgi(handler.full_path)

```

  

test很简单，如果文件后缀名是`.py`，`RequestHandler`就运行下面这个代码：

  

```python

def run_cgi(self, full_path):

        cmd = "python " + full_path

        child_stdin, child_stdout = os.popen2(cmd)

        child_stdin.close()

        data = child_stdout.read()

        child_stdout.close()

        self.send_content(data)

```

  

这是非常不安全的：如果有人知道我们服务器上的 Python 文件的路径，我们只是让他们运行它，而不用担心它可以访问哪些数据、它是否可能包含无限循环或其他任何内容

  

抛开这一点，核心思想很简单:

- 1.在子进程中运行程序

- 2.捕获子进程发送到标准输出的所有内容

- 3.将其发送回发出请求的客户端。

  

完整的 CGI 协议比这丰富得多，特别是它允许在 URL 中使用参数，服务器将这些参数传递给正在运行的程序，但这些细节不会影响系统的整体架构

  

这再次变得相当纠结。 RequestHandler 最初有一个方法，handle_file，用于处理内容。我们现在以 list_dir 和 run_cgi 的形式添加了两个特殊情况。这三种方法并不真正属于它们所在的位置，因为它们主要由其他人使用。

  

修复方法很简单：为所有case handlers程序创建一个父类，并将其他方法移动到该类（当且仅当它们由两个或多个处理程序共享时）。完成后，RequestHandler 类如下所示：

  

```python

class RequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):

  

    Cases = [case_no_file(),

             case_cgi_file(),

             case_existing_file(),

             case_directory_index_file(),

             case_directory_no_index_file(),

             case_always_fail()]

  

    # How to display an error.

    Error_Page = """\

        <html>

        <body>

        <h1>Error accessing {path}</h1>

        <p>{msg}</p>

        </body>

        </html>

        """

  

    # Classify and handle request.

    def do_GET(self):

        try:

  

            # Figure out what exactly is being requested.

            self.full_path = os.getcwd() + self.path

  

            # Figure out how to handle it.

            for case in self.Cases:

                if case.test(self):

                    case.act(self)

                    break

  

        # Handle errors.

        except Exception as msg:

            self.handle_error(msg)

  

    # Handle unknown objects.

    def handle_error(self, msg):

        content = self.Error_Page.format(path=self.path, msg=msg)

        self.send_content(content, 404)

  

    # Send actual content.

    def send_content(self, content, status=200):

        self.send_response(status)

        self.send_header("Content-type", "text/html")

        self.send_header("Content-Length", str(len(content)))

        self.end_headers()

        self.wfile.write(content)

while the parent class for our case handlers is:

  

class base_case(object):

    '''Parent for case handlers.'''

  

    def handle_file(self, handler, full_path):

        try:

            with open(full_path, 'rb') as reader:

                content = reader.read()

            handler.send_content(content)

        except IOError as msg:

            msg = "'{0}' cannot be read: {1}".format(full_path, msg)

            handler.handle_error(msg)

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        assert False, 'Not implemented.'

  

    def act(self, handler):

        assert False, 'Not implemented.'

```

  

现有文件的处理程序（只是随机选择一个示例）是：

  

```python

class case_existing_file(base_case):

    '''File exists.'''

  

    def test(self, handler):

        return os.path.isfile(handler.full_path)

  

    def act(self, handler):

        self.handle_file(handler, handler.full_path)

```

  
  

完整代码：

  

```python

import http.server

import os.path

import urllib.parse

from datetime import datetime

  

class ServerException(Exception):

    pass

  

class base_case(object):

    '''Parent for case handlers.'''

  

    def handle_file(self, handler, full_path):

        try:

            with open(full_path, 'rb') as reader:

                content = reader.read()

            handler.send_content(content)

        except IOError as msg:

            msg = "'{0}' cannot be read: {1}".format(full_path, msg)

            handler.handle_error(msg)

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        assert False, 'Not implemented.'

  

    def act(self, handler):

        assert False, 'Not implemented.'

  

class case_no_file(object):

    '''File or directory does not exist.'''

  

    def test(self, handler):

        return not os.path.exists(handler.full_path)

  

    def act(self, handler):

        raise ServerException("'{0}' not found".format(handler.path))

  

class case_existing_file(object):

    '''File exists.'''

  

    def test(self, handler):

        return os.path.isfile(handler.full_path)

  

    def act(self, handler):

        handler.handle_file(handler.full_path)

  

class case_directory_index_file(object):

    '''Serve index.html page for a directory.'''

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        return os.path.isdir(handler.full_path) and \

               os.path.isfile(self.index_path(handler))

  

    def act(self, handler):

        handler.handle_file(self.index_path(handler))

  

class case_directory_no_index_file(object):

    '''Serve listing for a directory without an index.html page.'''

  

    def index_path(self, handler):

        return os.path.join(handler.full_path, 'index.html')

  

    def test(self, handler):

        return os.path.isdir(handler.full_path) and \

               not os.path.isfile(self.index_path(handler))

  

    def act(self, handler):

        handler.list_dir(handler.full_path)

  

class case_always_fail(object):

    '''Base case if nothing else worked.'''

  

    def test(self, handler):

        return True

  

    def act(self, handler):

        raise ServerException("Unknown object '{0}'".format(handler.path))

  

print('''\

<html>

<body>

<p>Generated {0} </p>

</body>

</html>'''.format(datetime.now()))

  
  

class case_cgi_file(object):

    '''Something runnable.'''

  

    def test(self, handler):

        return os.path.isfile(handler.full_path) and \

            handler.full_path.endswith('.py')

  

    def act(self, handler):

        handler.run_cgi(handler.full_path)

  

class RequestHandler(http.server.BaseHTTPRequestHandler):

    '''

    If the requested path maps to a file, that file is served.

    If anything goes wrong, an error page is constructed.

    '''

    page = '''\

    <html>

    <body>

    <table>

    <tr> <td>Header</td> <td>Value</td></tr>

    <tr> <td>Date and Time</td> <td>{date_time}</td></tr>

    <tr> <td>client host</td> <td>{client_host}</td></tr>

    <tr> <td>client port</td> <td>{client_port}</td></tr>

    <tr> <td>Command</td> <td>{command}</td></tr>

    <tr> <td>Path</td> <td>{path}</td></tr>

    </table>

    <p>Hello, web!</p>

    </body>

    </html>

    '''

  

    Error_Page = """\

        <html>

        <body>

        <h1>Error accessing {path}</h1>

        <p>{msg}</p>

        </body>

        </html>

        """

  

    Cases = [case_no_file(),

             case_cgi_file(),

             case_existing_file(),

             case_directory_index_file(),

             case_directory_no_index_file(),

             case_always_fail()]

  

    Listing_Page = '''\

        <html>

        <body>

        <ul>

        {0}

        </ul>

        </body>

        </html>

        '''

  

    def list_dir(self, full_path):

        try:

            entries = os.listdir(full_path)

            bullets = ['<li>{0}</li>'.format(e)

                for e in entries if not e.startswith('.')]

            page = self.Listing_Page.format('\n'.join(bullets))

            self.send_content(page)

        except OSError as msg:

            msg = "'{0}' cannot be listed: {1}".format(self.path, msg)

            self.handle_error(msg)

  

    def handle_error(self, msg):

        content = self.Error_Page.format(path=self.path, msg=msg)

        self.send_content(content, 404)

  

    def send_content(self, content, status=200):

        self.send_response(status)

        self.send_header("Content-Type", "text/html")

        self.send_header("Content-Length", str(len(content)))

        self.end_headers()

        self.wfile.write(content.encode('utf-8'))

  

    def create_page(self):

        value = {

            'date_time': self.date_time_string(),

            'client_host': self.client_address[0],

            'client_port': self.client_address[1],

            'command': self.command,

            'path': self.path

        }

        page = self.page.format(**value)

        return page

  

    def send_page(self, page):

        self.send_response(200)

        self.send_header("Content-Type", "text/html")

        self.send_header("Content-Length", str(len(page)))

        self.end_headers()

        self.wfile.write(page.encode('utf-8'))

  

    def do_GET(self):

        try:

            # Figure out what exactly is being requested.

            self.full_path = os.getcwd() + urllib.parse.unquote(self.path)

  

            # Figure out how to handle it.

            for CaseClass in self.Cases:

                handler = CaseClass

                if handler.test(self):

                    handler.act(self)

                    break

  

        # Handle errors.

        except Exception as msg:

            self.handle_error(msg)

  

    def run_cgi(self, full_path):

        cmd = "python " + full_path

        child_stdin, child_stdout = os.popen(cmd)

        child_stdin.close()

        data = child_stdout.read()

        child_stdout.close()

        self.send_content(data)

  
  

if __name__ == '__main__':

    server_address = ('', 8080)

    server = http.server.HTTPServer(server_address, RequestHandler)

    server.serve_forever()

  

```

  

运行效果：

![](/images/web4.png)

  

<hr>

  

# Discussion

  

我们的原始代码和重构版本之间的差异反映了两个重要的想法。

  

**第一个是将类视为相关service的集合**，`RequestHandler`和`base_case`不做出决定或进行操作，它们提供其他类可以用来完成这些事情的工具。

  
  

**第二个是可扩展性**：人们可以通过编写外部 CGI 程序向我们的 Web 服务器添加新功能，或者通过添加case handler程序类。后者确实需要对 `RequestHandler` 进行一行更改（以将案例处理程序插入案例列表中）,但我们可以通过让 Web 服务器读取配置文件并从中加载处理程序类来摆脱这个问题。在这两种情况下，它们都可以忽略大多数较低级别的细节，就像 BaseHTTPRequestHandler 类的作者允许我们忽略处理套接字连接和解析 HTTP 请求的细节一样。

  

<hr>

  

**These ideas are generally useful; see if you can find ways to use them in your own projects.**
