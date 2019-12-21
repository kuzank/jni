>  原文地址 https://www.jianshu.com/p/fe42aa3150a0

<h2> 1. 什么是 JNI？</h2>  
JNI(Java Native Interface) Java 本地接口，又叫 Java 原生接口。它允许 Java 调用 C/C++ 的代码, 同时也允许在 C/C++ 中调用 Java 的代码。可以把 JNI 理解为一个桥梁，连接 Java 和底层。其实根据字面意思，JNI 就是一个介于 Java 层和 Native 层的接口，而 Native 层就是 C/C++ 层面。

<h2> 2. 为什么使用 JNI？</h2>  
一般情况下都是从 Java 的角度来使用 JNI，也就是说在 Java 中调用 C/C++ 语言来实现一些操作。所以从 Java 角度来说使用 JNI 具有以下的优点：

> 1.  能够重复使用一些现成的、具有相同功能的的 C/C++ 代码
> 2.  因为 C/C++ 是偏向底层的语言，所以使用 C/C++ 能够更加的高效，而且也使得 Java 能够访问操作系统中一些底层的特性。

<h2> 3. 怎么使用 JNI？</h2>  
_这里所说的使用 JNI 是指从 Java 层调用 C/C++ 代码，一般的使用步骤都是使用 Java 定义一个类，然后在该类中声明一个 native 的方法，接着使用 C/C++ 来实现这个方法的方法体。_

### 3.1 使用 Java 声明 native 方法

**方法一：**TestJNI.java

```java
public class TestJNI{
    public native void sayHello();
}
```

在声明 native 方法的时候还可以规定具体的包，例如：

**方法二：**TestJNI.java

```java
package jnilib;
public class TestJNI{
    public native void sayHello();
}
```

这两种方式都可以，但是使用这两种方式声明 native 方法，最后生成的动态库时，在 IntelliJ IDEA 中的使用方法却是不一样（这一点在最后会进行说明），这里我们采用方法二。

### 3.2 编译声明的 Java 文件

先使用`javac`编译生成`.class`文件

因为在源码中使用了 package 的命令，所以编译的时候需要用 "-d ." 参数，其中 "." 表示在当前目录生成 **jnilib 文件夹**来存放编译生成`.class`文件

再使用`javah`编译生成`.h`文件

需要在类文件名前面加上包名，编译完成之后，会在当前目录生成`jnilib_TestJNI.h`的文件，接下来我们用 C 语言来实现刚刚声明的函数时，需要`include`这个头文件。

jnilib_TestJNI.h：

```c
#include <jni.h>


#ifndef _Included_jnilib_TestJNI
#define _Included_jnilib_TestJNI
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_jnilib_TestJNI_sayHello
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
```

其中 `JNIEXPORT void JNICALL Java_jnilib_TestJNI_sayHello(JNIEnv *, jobject);` 就是我们用 Java 声明的 native 函数经过转换之后的形式，当我们用 C 语言来实现的时候需要使用这个函数的声明。

### 3.3 用 C 语言来实现函数

创建一个`TestJNI.c`文件：

TestJNI.c

```c
#include <stdio.h>
#include "jnilib_TestJNI.h"

JNIEXPORT void JNICALL Java_jnilib_TestJNI_sayHello(JNIEnv *env, jobject object){
    printf("Hello World!\n");
}
```

### 3.4 生成动态库文件

这需要注意的是在不同的操作系统，能够生成的动态库文件也是不一样的，在 Windows 中，要生成`.dll`文件，在 Linux 中要生成`.so`文件，在 Mac OS X 中要生成`.jnilib`文件。同时定义生成的库文件名的时候也要遵循：lib + 文件名＋扩展名 的原则。本例中我们在 Mac OS X 中所以我们定义生成的库文件为：`libTestJNI.jnilib`。

makefile：

```c
CC=gcc
CFLAGS=I.

libTestJNI.jnilib : TestJNI.c
    $(CC) -fPIC -I/Library/java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/include -I/Library/java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/include/darwin -shared -o libTestJNI.jnilib TestJNI.c
```

执行 make 之后获得 libTestJNI.jnilib 其中`/Library/java/JavaVirtualMachines/jdk1.8.0_91.jdk`为 Java 的安装目录。

### 3.5 使用生成的动态库文件

使用 Java 调用生成的动态库

Demo.java

```java
import jnilib.TestJNI;
public class Demo{
    static{
            try{
                System.loadLibrary("TestJNI");
            }catch(UnsatisfiedLinkError e){
                System.err.println("Native code library failed to load.\n" + e);
                System.exit(1);
            }
        }

    public static void main(String[] args) {
        TestJNI test = new TestJNI();
        test.sayHello();
    }
}
```

编译、执行后得到结果：

<h2> 4. 在 IntelliJ IDEA 里使用 JNI？</h2>  
利用 IntelliJ IDEA 创建项目，这里因为我们之前声明 native 函数的时候使用了 package，所以我们需要在 src/main/java 的目录下创建一个文件夹为 jnilib，把我们之前生成的`TestJNI.java libTest.jnilib` 文件放到该目录下。接着我们创建 Demo 文件来调用生成的动态库，但是如果我们此时运行我们的 Demo 的话会产生下面的异常：

```
java.lang.UnsatisfiedLinkError: no GetDownloadID in java.library.path
```

这时我们需要点击`EditConfigurations`在 `VM Options` 一栏填上 `-Djava.library.path="/Users/xiangang/JavaWebLearning/DownloadID/src/main/java/jnilib"`双引号里面的路径就是你刚刚创建的 `jnilib`文件夹的路径。

如果我们在声明 native 函数的时候没有使用 package 命令，则我们**必须把以上的两个文件放在 src/main/java 目录下，而且调用这个库文件的文件也不能使用 package。**


