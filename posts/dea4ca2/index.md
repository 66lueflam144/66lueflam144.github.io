# 2019极客大挑战 PHP


界面让我想起来之前做过的一个SQL Injection题目，好像也是猫猫在玩毛线。

# 1

  

尝试查看响应，直接`http://xxx/flag.php`或者`http://xxx/index.php`都得不到任何东西。然后再去看看说的话。

“备份”。

```txt

常用备份格式有

tar.gz，zip，rar，tar

  

常见的网站源码备份文件名：

web，website，backup，back，www，wwwroot，temp

```

&lt;!-- more --&gt;

  

不过如果是类似我这样的人，文件命名就会很随意。

  

随机排列组合上述“常用”，运气好，一会就有，运气不好可能没有。&lt;br&gt;所以用扫描工具。开了**dirsearch**在后台扫描。看见了很多有上述后缀名的文件。&lt;br&gt;不过扫描有点慢（可能因为开的全扫描），所以扫描的时候在这里打字。现在还没扫描好我打算先去吃晚饭了（bushi）。

  

# 2

扫描结束，很多文件，不过可以根据文件大小来判断，dirsearch还贴心标色区别，所以就是那个万紫丛中一点绿的`www.zip`。

在原网址输入后会下载一个zip文件，打开，里面重点有`flag.php`和`&#39;class.php`两个文件（之前就尝试输入文件名但return all blank），用vscode打开，代码审计。（...）

  

# 3

## dogdogdog

  

```php

//flag.php

  

&lt;?php

$flag = &#39;Syc{dog_dog_dog_dog}&#39;;

?&gt;

  

```

刘备砸碗GIF&lt;br&gt;

再看

```php

//class.php

&lt;?php

include &#39;flag.php&#39;;

  
  

error_reporting(0);

  
  

class Name{

    private $username = &#39;nonono&#39;;

    private $password = &#39;yesyes&#39;;

  

    public function __construct($username,$password){

        $this-&gt;username = $username;

        $this-&gt;password = $password;

    }

  

    function __wakeup(){

        $this-&gt;username = &#39;guest&#39;;

    }

  

    function __destruct(){

        if ($this-&gt;password != 100) {

            echo &#34;&lt;/br&gt;NO!!!hacker!!!&lt;/br&gt;&#34;;

            echo &#34;You name is: &#34;;

            echo $this-&gt;username;echo &#34;&lt;/br&gt;&#34;;

            echo &#34;You password is: &#34;;

            echo $this-&gt;password;echo &#34;&lt;/br&gt;&#34;;

            die();

        }

        if ($this-&gt;username === &#39;admin&#39;) {

            global $flag;

            echo $flag;

        }else{

            echo &#34;&lt;/br&gt;hello my friend~~&lt;/br&gt;sorry i can&#39;t give you the flag!&#34;;

            die();

  

        }

    }

}

?&gt;

```

大概是在讲有个private用户nonono和密码yesyes的设定，但外来输入会被__wakeup()重置为`guest`,但如果username是admin，就会返回flag。

于是我就构建了`?username=`这样的，发现，不知道要跟在哪一个php后面...

所以找专业人士（等以后学了php审计再来更新）分析一下：

  
  
  

- 1. `include &#39;flag.php&#39;;`：该语句用于包含一个名为 &#34;flag.php&#34; 的文件。这意味着代码中可能存在一个存储着敏感信息（如密钥、密码等）的变量。

  

- 2. `error_reporting(0);`：该语句禁止显示 PHP 错误报告。这可以防止攻击者通过错误信息来寻找潜在的安全漏洞。

  

- 3. `class Name`：定义了一个名为 `Name` 的类。

  

- 4. `private $username = &#39;nonono&#39;;` 和 `private $password = &#39;yesyes&#39;;`：类中的私有成员变量 `$username` 和 `$password` 被初始化为默认值。

  

- 5. `public function __construct($username, $password)`：该构造函数接受两个参数 `$username` 和 `$password`，并将它们赋值给对应的成员变量。通过构造函数，实例化对象时可以传入不同的用户名和密码。

  

- 6. `function __wakeup()`：该魔术方法在反序列化操作时会被调用。在这个例子中，`__wakeup()` 方法将用户的用户名设置为 &#34;guest&#34;。

  

- 7. `function __destruct()`：该魔术方法在对象销毁时会被调用。在这段代码中，析构函数判断密码是否为 100。如果不是，则输出一条警告信息，并显示用户的用户名和密码（可能是攻击者恶意构造的）。如果密码为 100 并且用户名为 &#34;admin&#34;，则输出存储在 `$flag` 变量中的敏感信息；否则输出友好的信息，并结束程序。

  

## catch me if you can

  

文件里面还有一个`index.php`，也看了看，

```php

//其中一段

&lt;?php

    include &#39;class.php&#39;;

    $select = $_GET[&#39;select&#39;];

    $res=unserialize(@$select);

    ?&gt;

```

  
  

- 1. `include &#39;class.php&#39;;`：该代码行将引入名为 &#34;class.php&#34; 的 PHP 文件。`include` 用于在 PHP 中包含指定的文件，以便在当前脚本中使用该文件中定义的类、函数和变量。

  

- 2. `$select = $_GET[&#39;select&#39;];`：该代码行将从 GET 请求中获取名为 &#34;select&#34; 的参数值，并将其赋给变量 `$select`。GET 请求的参数通常是通过 URL 的查询字符串（query string）传递的，例如 `http://example.com?select=value`。

  

- 3. `$res=unserialize(@$select);`：该代码行将尝试对 `$select` 变量进行反序列化操作，并将结果赋给变量 `$res`。`unserialize()` 是一个 PHP 函数，用于将字符串转换回原始的 PHP 值（对象、数组等）。`@` 符号用于抑制可能出现的错误或警告信息。

  

在这个过程中，它包含了一个潜在的安全风险。由于它使用了 `unserialize()` 函数来反序列化用户输入的数据，而用户可以通过构造恶意数据来执行任意的 PHP 代码。

  

## 反序列化

  

&lt;a href=https://blog.csdn.net/solitudi/article/details/113588692&gt;参考&lt;/a&gt;

具体的，我们下次再说，因为实在还没学。


---

> Author:   
> URL: https://66lueflam144.github.io/posts/dea4ca2/  

