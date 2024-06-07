# R！C！E！


## Scan

看到这种白茫茫一片的就直接上工具。

dirsearch、dirmap一起用

- dirmap以迅雷不及掩耳之速扫出：

```http

[200][text/plain; charset=utf-8][21.00b] http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/HEAD

[200][text/plain; charset=utf-8][265.00b] http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/COMMIT_EDITMSG

[200][application/octet-stream][289.00b] http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/index

[200][text/plain; charset=utf-8][459.00b] http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/logs/HEAD

[200][text/plain; charset=utf-8][112.00b] http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/packed-refs

[200][text/plain; charset=utf-8][177.00b] http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/logs/refs/remotes/origin/HEAD

```

- dirsearch慢慢来到起跑线：

```http

200   265B   http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/COMMIT_EDITMSG

200    73B   http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/description

502     0B   http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.gdrive/token_v2.json

403   316B   http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/hooks/

200   295B   http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/config

200    21B   http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/HEAD

...//还有很多就省略了

```

  

----

  

## githack &amp; hack

再用githack

```py

python .\GitHack.py http://30e2220f-465d-4920-82f8-01d52c8f6522.node4.buuoj.cn:81/.git/

```

下载下来一个文件夹，其中比较重要的：

```php

//bo0g1pop.php

&lt;?phphighlight_file(__FILE__);

if (&#39;;&#39; === preg_replace(&#39;/[^\W]&#43;\((?R)?\)/&#39;, &#39;&#39;, $_GET[&#39;star&#39;])) {    

    if(!preg_match(&#39;/high|get_defined_vars|scandir|var_dump|read|file|php|curent|end/i&#39;,$_GET[&#39;star&#39;])){        

        eval($_GET[&#39;star&#39;]);    

        }

}

```

本文盲看不懂但知道是GET传参，参数是star，还有正则表达式。（官方————无参数命令执行）

  

----

----

  

## 测评&amp;ファンワイ

  

### 测评

这次出动了dirsearch、dirmap、githack。

- 然后在速度方面dirmap完虐dirsearch。

- githack这个呢，最开始在CTFHub下载的，结果是一个老版本，只适用于python2……我……

- 在zn网页找不到升级版反而找到一堆怎么pip2的，就去了GitHub上找……结果人家去年就搞了升级版的……沉默是今晚的康桥……这个延迟度比我的Google验证码还延迟……

  

### ファンワイ

在用githack下载/.git/的时候，火绒蹦出来说有危险立马消杀，留下我目瞪口呆。开虚拟机好麻烦的啊……火绒你……
&lt;hr&gt;
2024：现在已经离不开虚拟机了

---

> Author:   
> URL: https://66lueflam144.github.io/posts/1573821/  

