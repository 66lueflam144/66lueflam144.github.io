---
title: 了解一下资产测绘 
date: 2023-11-07T18:28:59+08:00
slug: 3977d43
tags:
  - 资产测绘
categories:
  - LEARN
---

# What is it

  

>**网络资产测绘**，用一些技术方法，探测全球互联网空间上的节点分布情况和网络关系索引，构建全球互联网图谱的一种方法。

  

<!--more-->

  

## 资产

  

>**网络资产**，指赛博空间中某机构所拥有的一切可能被潜在攻击者利用的设备、信息、应用等数字资产。具体包括但不限于硬件设备、云主机、操作系统、IP地址、端口、证书、域名、Web应用、业务应用、中间件、框架、机构公众号、小程序、App、API、源代码等。概括来说，只要是可操作的对象，不管是实体还是属性。都可以称之为“网络空间资产”。

  

网络资产在协议实现、网络应用等方面存在差异，如开放的端口/服务信息、banner信息、Web网页数据等，对这些差异进行特征提取可得到该资产的特征指纹，网络资产指纹库积累了大量网络资产指纹。资产指纹比对是将目标主机的特征指纹和指纹库进行匹配，从而实现资产属性识别。

  

### 指纹特征

  

>机构中某个资产具有的一系列特征信息的集合构成了这个资产的指纹特征——外网IP地址、内网IP地址、端口号、证书、域名、操作系统、Web应用、中间件/框架

  
  
  

-----

  

资产扫描和发现不等同于网络空间测绘，我们可以从字面上去做个简单拆解，可以把资产扫描和发现理解为“测”的一部分，那么我们还需要去“绘”,也就是说要去分析数据、解读数据,进而去找到网络空间与现实物理空间存在着多种映射关联，并描述出来。

  
  

-----

-----

  

# 测绘概念

  

- 行为测绘

- 动态测绘

- 交叉测绘

  

## 行为测绘

  

>对不同的banner特征进行识别

  

## 动态测绘

  

>周期性对某个资产进行扫描，动态掌握周期内新增资产的情况

  

网络空间资产是随时间变化而不断变化的，在这个角度上来看资产扫描与发现很多时候只是某些单一时间点的任务，而资产测绘的探测扫描则是持续的，通过网络资产的动态变化，反映现实空间的变化。

  
  

## 交叉测绘

  

>IPv4与IPv6的交叉

  

----

----

  
  

# 网络资产识别

  

- 设备组件识别

- 应用组件识别

- 业务类型推断

  

|name|description|more|

|---|---|---|

|设备组件识别|获取资产的网站响应头部数据、网站文件类型、网站异常响应、服务端口、banner等数据，提取设备指纹，通过与网络设备组件指纹库进行指纹比对，识别目标主机的设备类型、设备厂商、设备品牌、设备型号等设备属性。||

|应用组件识别|通过持续收集、解析目标网络的应用组件信息，如论坛程序、博客程序等，获取网站响应头部数据、HTML页面、特殊URL、开放的端口、banner等，生成应用指纹，通过比对网络应用组件指纹库来自动化识别目标主机的Web服务器软件、Web脚本语言、服务类型及相应版本型号等应用属性。||

|业务类型推断|基于资产探测数据，深入融合DNS信息、漏洞库、IP地理信息库等资源，建立资产多层级关联模型，推断网络资产的业务类型，实现网络资产多维度画像。业务类型推断旨在识别重要行业的重要资产，是网络空间深入态势感知的核心环节。||

  
  

----

-----

  
  
  

## 平台

  

- <a href=https://fofa.info/>FOFA</a>

- <a href=https://www.zoomeye.org/>ZoomEye</a>

  

----

  

# 参考

  

<a herf=http://www.gjbmj.gov.cn/n1/2022/0422/c411145-32406257.html>gov description</a><br>

  

<a href=https://paper.seebug.org/1810/#_2>seebug 网络空间测绘溯源技术剖析</a><br>

  

<a href=https://zhuanlan.zhihu.com/p/515210481>CAM</a><br>

  

<a href=https://sec.bsia.org.cn/4937/202303/58219.html>对话</a><br>

  

<a href=https://blog.nsfocus.net/cyberspace/>cyberspace</a><br>

  

<a href=https://www.anquanke.com/table/topic.html?id=1>一些talk</a>
