#include <stdio.h>
#include "jnilib_TestJNI.h"

JNIEXPORT void JNICALL Java_jnilib_TestJNI_sayHello(JNIEnv *env, jobject object){
    printf("Hello World!\n");
}
