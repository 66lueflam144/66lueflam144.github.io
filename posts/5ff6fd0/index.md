# Halo 0.4.3 审计ing


&lt;!--more--&gt;


# HALO

&gt;一个java博客系统，但funny problems everywhere

看着怪像wordpress的。&lt;br&gt;

打开pom.xml，到处都是警告
![警告](./vulnerable.png)


我一定是和DOCKER有仇，所以只能打开linux虚拟机通过linux的docker进行搭建环境。

## pom.xml

```xml
&lt;dependency&gt;
			&lt;groupId&gt;com.h2database&lt;/groupId&gt;
			&lt;artifactId&gt;h2&lt;/artifactId&gt;
			&lt;scope&gt;runtime&lt;/scope&gt;
		&lt;/dependency&gt;
```

这个并没有见过，但看名字和数据库有关，于是搜索，结果为&lt;br&gt;
[h2未授权访问漏洞](https://blog.csdn.net/weixin_45366453/article/details/125525496)

&lt;br&gt;看不懂思密达&lt;br&gt;
所以转向看controller

# 任意文件删除


## 漏洞代码

**删除备份**

```java
//BackupController.java

    @GetMapping(value = &#34;delBackup&#34;)
    @ResponseBody
    //fileName文件名，type备份类型
    public JsonResult delBackup(@RequestParam(&#34;fileName&#34;) String fileName,
                                @RequestParam(&#34;type&#34;) String type) {
        final String srcPath = System.getProperties().getProperty(&#34;user.home&#34;) &#43; &#34;/halo/backup/&#34; &#43; type &#43; &#34;/&#34; &#43; fileName;
        try {
            FileUtil.del(srcPath);
            return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage(&#34;code.admin.common.delete-success&#34;));
        } catch (Exception e) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage(&#34;code.admin.common.delete-failed&#34;));
        }
    }
```

理解逻辑就是：
1. 请求中有fileName和type，然后调用`System.getProperties().getProperty(&#34;user.home&#34;)`获取当前用户的主目录路径。（`System.getProperties()` 是一个方法，它返回一个 `Properties` 对象，该对象包含当前 Java 虚拟机的系统属性）
2. 拼接之后得到srcPath， 进行删除，成功就返回消息，失败也但会消息。

### vuln

在这里可能存在的问题就是，任意文件的删除。
- 文件名可以进行抓包修改
- 修改目录路径，即使写入了指定路径，但是未进行后续的验证，所以修改路径is true 。后面会发现很多地方都是这样的错误。

一个验证实验

```cmd
PS C:\Users\won\Downloads&gt; cd D:\sot\..\..\
PS D:\&gt;
```
进行跨目录是可以实现的。（nothing special but for me）


&lt;hr&gt;


# Page 

对wordpress的认知里面（或者说是对博客系统的认知），有类似在page或者是post模板处的漏洞，所以继续翻翻。

## 任意文件内容写入 漏洞代码

```java
//ThemeController.java
/**
     * 保存修改模板
     *
     * @param tplName    模板名称
     * @param tplContent 模板内容
     * @return JsonResult
     */
    @PostMapping(value = &#34;/editor/save&#34;)
    @ResponseBody
    //请求中参数有两个，一个tplName，一个tplContent
    public JsonResult saveTpl(@RequestParam(&#34;tplName&#34;) String tplName,
                              @RequestParam(&#34;tplContent&#34;) String tplContent) {
        if (StrUtil.isBlank(tplContent)) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage(&#34;code.admin.theme.edit.no-content&#34;));
        }
        try {
            //获取项目根路径
            final File basePath = new File(ResourceUtils.getURL(&#34;classpath:&#34;).getPath());
            //获取主题路径
            final StrBuilder themePath = new StrBuilder(&#34;templates/themes/&#34;);
            themePath.append(BaseController.THEME);
            themePath.append(&#34;/&#34;);
            themePath.append(tplName);
            final File tplPath = new File(basePath.getAbsolutePath(), themePath.toString());

            //用FileWriter写入tplContent
            //使用 FileWriter 对象写入内容，如果指定的文件路径不存在的话， FileWriter 会自动创建一个新的文件。
            final FileWriter fileWriter = new FileWriter(tplPath);
            fileWriter.write(tplContent);
        } catch (Exception e) {
            log.error(&#34;Template save failed: {}&#34;, e.getMessage());
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage(&#34;code.admin.common.save-failed&#34;));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage(&#34;code.admin.common.save-success&#34;));
    }
```

### vuln

- 对写入的内容即tplContent没有进行过滤，完全会写入危险的代码，比如反向shell代码。
而FileWriter的无中生有更是大大方便这种攻击。

- 依旧是路径的问题，完全换一个地方写入都可以

&lt;hr&gt;


## 文件内容读取？

```java
 @GetMapping(value = &#34;/getTpl&#34;, produces = &#34;text/text;charset=UTF-8&#34;)
    @ResponseBody
    public String getTplContent(@RequestParam(&#34;tplName&#34;) String tplName) {
        String tplContent = &#34;&#34;;
        try {
            //获取项目根路径
            final File basePath = new File(ResourceUtils.getURL(&#34;classpath:&#34;).getPath());
            //获取主题路径
            final StrBuilder themePath = new StrBuilder(&#34;templates/themes/&#34;);
            themePath.append(BaseController.THEME);
            themePath.append(&#34;/&#34;);
            themePath.append(tplName);
            final File themesPath = new File(basePath.getAbsolutePath(), themePath.toString());
            final FileReader fileReader = new FileReader(themesPath);
            tplContent = fileReader.readString();
        } catch (Exception e) {
            log.error(&#34;Get template file error: {}&#34;, e.getMessage());
        }
        return tplContent;
    }
```

### vuln

这个的问题根源就是对目录的不设限制。

&lt;hr&gt;

question
可以看到确实是根据指定目录进行实例化，为什么可以做到读取别的内容进行返回？

答案，因为可以跨目录，指定目录只是说从哪里开始，但没有限制去哪里，所以可以。

可以写一个demo进行测试理解。

---

> Author:   
> URL: https://66lueflam144.github.io/posts/5ff6fd0/  

