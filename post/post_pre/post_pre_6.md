---
title: 序列化与反序列化
subtitle: 
date: 2024-01-26T10:16:36+08:00
slug: 2a957e7
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
  - serialize
  - LEARN
categories:
  - PHP
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

  
  

# Introduction For Serialize()

  

>序列化：将对象转换为字节序列的过程称为对象的序列化，举个例子，游戏的存档。

  

php中序列化函数`serialize()`用于序列化 `对象`或者`数组`，并返回一个`字符串`。

  

序列化后的结果可以方便的传递到其他地方if needed，且其**类型和结构**不会改变。

  

## example

  

```php

<?php

highlight_file(__FILE__);

  

$sites = array('Holy', 'Fool', 'Babe');

echo '<br\>';

var_dump(serialize($sites));

echo '<br\>';

  

class judas{

    public $name = "Judas";

    public $sex = "male";

    private $age = 26;

}

  

$M = new judas();

var_dump(serialize(M));

  

?>

```

  

效果如下：

  

![1](../post_pre/pics/serialize/ser1.png)

对**数组的序列化**：

  

- a 代表数组

- 3 代表数组中有3个元素

- i 代表数组的下标

- 0 下标值

- s 代表元素Holy的数据类型是 字符型

- 4 元素Holy的长度

  

对**对象的序列化**：

  

- O 代表对象

- 5 代表类名judas的长度

- 3 代表类中的字段数，即 `$name`、 `$sex`、 `$age`

- s 代表属性 name 的类型为 字符型

- 5 代表属性 name 的长度，即 Judas 的长度

  

<hr>

  

# Introduction For unserialize()

  

php中反序列化函数是`unserialize()`，用于还原 被序列化后的字符串 为原来的 数组或对象。

  
  

## example

  

在之前的测试代码上修修改改

  

```php

<?php

highlight_file(__FILE__);

  

$sites = array('Holy', 'Fool', 'Babe');

$ser1 = serialize($sites);

class judas{

    public $name = "Judas";

    public $sex = "male";

    private $age = 26;

}

  

$M = new judas();

$ser2 = serialize($M);

echo '序列化：';

echo '<br>';

var_dump($ser1);

echo '<br>';

var_dump($ser2);

  

echo '<br>';

  

echo '反序列化：';

echo '<br>';

var_dump(unserialize($ser1));

echo '<br>';

var_dump(unserialize($ser2));

  

?>

```

  

结果如图：

![2](../post_pre/pics/serialize/ser2.png)

  
  

<hr>

  

# function

  

序列化和反序列化在系统中的作用：

  

- 把对象的字节序列永久放在磁盘中，需要时调用，节省磁盘占用空间。

- 在传输过程中直接传输字节序列而不是对象，提高传输速率

<hr>

  

# 反序列化漏洞利用

  

## 条件

  

- unserialize()参数可控

- 存在可利用的类，且类中有**魔术方法**

  

## 魔术方法

  

### __construct() & __destruct()

  

>__construct():拥有该函数的类在每次创建新对象的时候，先调用此方法

  

>__destruct():拥有该函数的类，当某个对象的所有引用都被删除或者对象被销毁时执行。

  

用例子可以理解这两句话：

  

```php

<?php

highlight_file(__FILE__);

  
  

class judas{

    public $name = "Rihanna";

    public $sex = "female";

    private $age = 26;

    public function __construct(){

        echo 'construct<br>';

    }

    public function num($a, $b){

        echo $c = $a + $b;

        echo '<br>';

        return $c;

    }

    public function __destruct(){

        echo 'destruct<br>';

    }

    public function person($per){

        echo "we are $per <br>";

    }

}

  

$R = new judas();

$R->num(12, 13);

$R->person(female);

  

?>

```

  

结果be like：

  

```txt

construct

25

we are female

destruct

```

  

### __wakeup() & __sleep()

  

>__wakeup()如果存在，就在unserialize()前被调用，用于预先准备对象需要的资源

>__sleep()如果存在，就在serialize()前被调用，用于提交未提交的数据，或者类似的清理操作，如果没有返回属性的话，序列化时会将属性清空。

  

```php

<?php

highlight_file(__FILE__);

  
  

class judas{

    public $name = "Rihanna";

    public $sex = "female";

    private $age = 26;

    public function __construct(){

        echo '<br>construct<br>';

    }

    public function __destruct(){

        echo '<br></br>destruct<br>';

    }

    public function __wakeup(){

        echo '<br>wake up， soonze！<br>';

    }

    public function __sleep(){

        echo '<br>am i sleeping<br>';

        return array("name", "sex", "age");

    }

}

$R = new judas();

echo $ser = serialize($R);

var_dump(unserialize($ser));

?>

```

  

结果是：

![3](../post_pre/pics/serialize/ser3.png)

  

ps:出现两次 destruct 是因为：

- construct 实例化对象

- 序列化

- wakeup

- 反序列化

- destruct 所有方法都执行完毕

- destruct 销毁对象

  

### __toString()

  

>用于定义一个类被当作字符串的时候该怎么处理

  

```php

<?php

highlight_file(__FILE__);

  
  

class judas{

    public $name = "Rihanna";

    public $sex = "female";

    private $age = 26;

    public function __construct(){

        echo '<br>construct<br>';

    }

    public function __destruct(){

        echo '<br></br>destruct<br>';

    }

    public function __wakeup(){

        echo '<br>wake up， soonze！<br>';

    }

    public function __toString(){

        return '<br>被当字符串中';

    }

}

$R = new judas();

echo $R;//类实例被当作字符用echo输出

?>

```

  

结果：

  

```txt

construct

  

被当字符串中

  

destruct

```

  

### __invoke()

  

>当尝试以调用函数的方式调用一个对象时，__invoke()会被自动调用，不过php version>=5.3.0

  

```php

<?php

highlight_file(__FILE__);

  
  

class judas{

    public $name = "Rihanna";

    public $sex = "female";

    private $age = 26;

    public function __construct(){

        echo '<br>construct<br>';

    }

    public function __destruct(){

        echo '<br></br>destruct<br>';

    }

    public function __wakeup(){

        echo '<br>wake up， soonze！<br>';

    }

    public function __sleep(){

        echo '<br>am i sleeping<br>';

        return array("name", "sex", "age");

    }

    public function __invoke(){

        echo '<br>被当作函数中<br>';

    }

}

$R = new judas();

$R();

?>

```

  

```txt

construct

  

被当作函数中

  
  

destruct

```

  

### __call()

  

>在对象中调用一个不存在或者不可访问方法，会调用

  

```php

<?php

highlight_file(__FILE__);

  
  

class judas{

    public $name = "Rihanna";

    public $sex = "female";

    private $age = 26;

    public function __construct(){

        echo '<br>construct<br>';

    }

    public function __destruct(){

        echo '<br></br>destruct<br>';

    }

    public function __wakeup(){

        echo '<br>wake up， soonze！<br>';

    }

    public function __sleep(){

        echo '<br>am i sleeping<br>';

        return array("name", "sex", "age");

    }

    public function __call($arg1, $arg2){

        echo '<br>寻找一个不存在或者不可访问的方法<br>';

    }

}

$R = new judas();

$R->next(1, 2);

?>

```

  

```txt

construct

  

寻找一个不存在或者不可访问的方法

  
  

destruct

```

  

### __set()

  
  

```php

<?php

highlight_file(__FILE__);

  
  

class judas{

    public $name = "Rihanna";

    public $sex = "female";

    private $age = 26;

    private $weight = 100;

    public function __construct(){

        echo '<br>construct<br>';

    }

    public function __destruct(){

        echo '<br></br>destruct<br>';

    }

    public function person($per){

        echo "<br>Fuck $per <br>";

    }

    public function __set($one, $two){

        echo "访问不存在或者不能访问的属性赋值中";

    }

    public function people(){

        echo $this->name;

        echo $this->weight;

    }

  

}

$R = new judas();

$R->weight = 120;//在为不可访问的属性赋值

echo '<br>';

$R->people();

?>

```

  

```txt

construct

访问不存在或者不能访问的属性赋值中

Rihanna100

  

destruct

```

  

相同的还有 （对不可访问属性调用）：

  

- `__isset()`

- `__unset`

- `__get()`

  
  

<hr>

  

<hr>
