CC=gcc
CFLAGS=I.

libTestJNI.jnilib : TestJNI.c
	$(CC) -fPIC -I/Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home/include/ -I/Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home/include/darwin/ -shared -o libTestJNI.jnilib TestJNI.c
