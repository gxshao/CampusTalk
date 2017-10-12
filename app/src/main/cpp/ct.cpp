#include <jni.h>
#include <string>
#include <stdlib.h>
#include "base64.c"
#include "android/log.h"

#define Tag "shao"
#define LOG(...) __android_log_print(ANDROID_LOG_ERROR,Tag,__VA_ARGS__)
JavaVM* jvm;

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jstring JNICALL
Java_com_mrsgx_campustalk_utils_NativeUtils_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

jstring char2String( JNIEnv* env, const char* pat )
{
    env->GetJavaVM(&jvm);
//定义java String类 strClass
    jclass strClass = (env)->FindClass( "java/lang/String");
//获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
//建立byte数组
    jbyteArray bytes = (env)->NewByteArray((jsize)strlen(pat));
//将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, (jsize)strlen(pat), (jbyte*)pat);
//设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("UTF-8");
//将byte数组转换为java String,并输出
    return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);

}
JNIEXPORT jstring JNICALL
Java_com_mrsgx_campustalk_utils_NativeUtils_encode(JNIEnv *env, jobject, jbyteArray buffer) {
    jint len = env->GetArrayLength(buffer);
    const unsigned char *src = (const unsigned char *) env->GetByteArrayElements(buffer, 0);
    if (src == NULL) {
        LOG("转换失败");
        return NULL;
    }
    size_t t_len = (size_t) (len / 2 + len);
    unsigned char dest[t_len];
    size_t s_len = (unsigned int) len;
    int iRet = base64_encode(dest, &t_len, src, s_len);
    if (iRet == POLARSSL_ERR_BASE64_BUFFER_TOO_SMALL){
        base64_encode(dest, &t_len, src, s_len);
    }
    else if (iRet == POLARSSL_ERR_BASE64_INVALID_CHARACTER)
        LOG("无效数据!");

    return  char2String(env, (const char *) dest);
}
JNIEXPORT jbyteArray JNICALL
Java_com_mrsgx_campustalk_utils_NativeUtils_decodeBuffer(JNIEnv *env, jobject, jstring str) {
    const unsigned char *src = (const unsigned char *) env->GetStringUTFChars(str, 0);

    size_t t_len= (size_t) env->GetStringLength(str);
    unsigned char dest[t_len+t_len/2];
    size_t s_len=strlen((const char *) src);
    base64_decode(dest, &t_len, src,s_len);
    LOG("解析完成,长度为%d",t_len);

    jbyteArray array = env->NewByteArray((jsize) t_len);
    env->SetByteArrayRegion(array, 0, (jsize) t_len, (const jbyte *) dest);
    return array;
}
#ifdef __cplusplus
}
#endif