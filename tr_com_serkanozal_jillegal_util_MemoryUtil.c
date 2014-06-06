#include <jni.h>
#include "tr_com_serkanozal_jillegal_util_MemoryUtil.h"

jclass globalRefClass = NULL;

JNIEXPORT jclass JNICALL Java_tr_com_serkanozal_jillegal_util_MemoryUtil_pinClass(JNIEnv *env, jclass ownerClass, jstring className) {
	if (globalRefClass == NULL) {
		jclass localRefCls = (*env)->FindClass(env, (*env)->GetStringUTFChars(env, className, NULL));
		
		if (localRefCls == NULL) {
			return NULL; /* exception thrown */
		}
		
		globalRefClass = (*env)->NewGlobalRef(env, localRefCls);
	}

	return globalRefClass;	
}

JNIEXPORT jobject JNICALL Java_tr_com_serkanozal_jillegal_util_MemoryUtil_pinObject(JNIEnv *env, jclass ownerClass, jobject object) {
	return (*env)->NewGlobalRef(env, object);
}

