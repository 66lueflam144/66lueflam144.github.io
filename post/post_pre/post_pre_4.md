---
title: Hugo Log
subtitle: 
date: 2024-01-18T10:12:31+08:00
slug: 6fb9740
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
categories:
  - HUGO
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

在备战小升初一个月之后发现hexo怎么也更新不了文章遂气急败坏。

  

<!--more-->

  

# Pre-Environmental

  

- GitHub

- git

- hugo_extended

  

默认前两者已经安装。

  

## Hugo_extended

  

因为一点网络问题所以最开始是直接到[GitHub](https://github.com/gohugoio/hugo/releases/tag/v0.121.2)上面下载的zip文件。注意要下载**extended**版本。

  

下载解压到你想要的地方之后，比如`D:/Hugo`，然后就把这个文件路径写入系统环境变量的`Path`里面。

  
  

## connect to remote

  

这里为了防止一些数据丢失的惨剧的发生，使用两个库进行搭建github page。

  

- xxx.github.io： 公开库，用来搭建page

- zzz(private repository)： 非公开库，用来备份数据

  

在github上创建好之后，无需clone到本地，想clone也行。

  
  

# Hugo-Start

  

## 目录结构

  

- `hugo new site <site-name>`：创建一个站点，也就是博客的初始化文件夹，举例:`hugo new site boom`，就会初始化一个`boom`文件夹，里面的文件目录结构：

  

```txt

boom

├── archetypes                      内容模版目录

│   └── default.md                  模版文件

├── config.toml                     配置文件

├── content                         内容目录

├── data                            数据目录

├── layouts                         网站模版目录

├── public                          构建怕个的建站文件，通过hugo命令行生成

├── static                          静态文件目录

├── themes                          主题目录

└── hugo.toml                       网站配置文件

```

  

`themes`用来存放你想使用的主题模板，内容因theme而异。`public`的内容也和选择的主题有关。

  

<hr>

  

## command

  
  

- `hugo new posts/xxx.md`：创建新post

- `hugo serve -D`或者`hugo serve -D --disableFastRender`：本地运行

- `hugo`：刷新public

  
  

<hr>

  

# remote connect

  

接到上面的两个库，假设一个是`xxx.github.io`，一个是`priva`。

  

## private repository connection

  

那么，在`boom\`下，我们连接的是`priva`：

  

- `git init`

- `git remote add origin https://github.com/xxx/priva.git`

- `git add .`

- `git commit -m "update"`

- `git push -u origin main`（这里可能会出现报错，需要先创建`main` branch）

  

## page repository connection

  

进入`public`里面，我们连接的是`xxx.github.io`，步骤一致：

  

- `git init`

- `git remote add origin https://github.com/xxx/xxx.github.io.git`

- `git add .`

- `git commit -m "update"`

- `git push -u origin main`（这里可能会出现报错，需要先创建`main` branch）

  
  

<hr>

  

# More

  

[参考1](https://haoyep.com/posts/windows-hugo-blog-github/)

<br>

  

[参考2](https://jellyzhang.github.io/%E4%BD%BF%E7%94%A8hugo%E6%90%AD%E5%BB%BA%E5%8D%9A%E5%AE%A2/)

<br>

  

[一些主题](https://immmmm.com/hugo-themes/)

<br>

  

[LoveIt主题文档](https://hugoloveit.com/zh-cn/theme-documentation-basics/)

<br>

  

[如果用LoveIt主题会遇到的taxonomyTerm问题的解决方法](https://github.com/gohugoio/hugo/issues/4528)

<br>

  

[一个插入图片的方法](https://lysandert.github.io/posts/blog/blog_insert_pic/)
