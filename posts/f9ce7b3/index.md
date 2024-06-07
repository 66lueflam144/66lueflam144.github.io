# SSTI


&lt;!--more--&gt;

## PYTHON

### Flask

示例代码

```python
from flask import Flask, request
from jinja2 import Template

app = Flask(__name__)
app.config[&#39;See&#39;] = &#34;passwd:12345&#34;

@app.route(&#34;/&#34;)

def index():
    name = request.args.get(&#39;name&#39;, &#39;guest&#39;)
    t = Template(&#39;&#39;&#39;
    &lt;html&gt;
        &lt;head&gt;
            &lt;title&gt;SSTI_TEST&lt;/title&gt;
        &lt;/head&gt;
        &lt;body&gt;
            &lt;h1&gt;hello, %s&lt;/h1&gt;
        &lt;/body&gt;
    &lt;/html&gt;
    &#39;&#39;&#39; % (name))
    return t.render()

if __name__ == &#34;__main__&#34;:
    app.run()
```

简单运行结果：



![ssti1](../post_7/pics/ssti1.png)



当`name={{5*6}}`的时候，就会显示`hello,30`。

![ssti2](../post_7/pics/ssti2.png)





说明name的参数被执行。

**SSTI的关键词就是，参数被执行。**

关于为什么会被执行，简单的说法就是，输入的内容，先加入渲染，然后一起输出。


一个不会出现SSTI的示例：
```python
from flask import Flask, request
from jinja2 import Template

app = Flask(__name__)
app.config[&#39;See&#39;] = &#34;passwd:12345&#34;

@app.route(&#34;/&#34;)

def index():
    return render_template(&#34;index.html&#34;, title=&#34;SSTI_TEST&#34;, name=request.args.get(&#34;name&#34;))


if __name__ == &#34;__main__&#34;:
    app.run()

```

```html
//index.html
&lt;html&gt;
  &lt;head&gt;
    &lt;title&gt;{{title}} - cl4y&lt;/title&gt;
  &lt;/head&gt;
 &lt;body&gt;
      &lt;h1&gt;Hello, {{name}} !&lt;/h1&gt;
  &lt;/body&gt;
&lt;/html&gt;
```

- 服务器先将index.html渲染，然后获取用户输入，就像往空缺的地方搭积木一样放进去，就只是放进去，不会参与解析渲染。

---

> Author:   
> URL: https://66lueflam144.github.io/posts/f9ce7b3/  

