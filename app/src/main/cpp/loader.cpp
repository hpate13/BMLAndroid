// BML native loader hook — PLACEHOLDER (not yet wired into the Gradle build).
//
// A real runtime loader must run inside the BTD6 process and hand off to
// LemonLoader before the game's il2cpp runtime initializes. LemonLoader already
// performs that injection (libmain.so + il2cpp metadata bootstrap); this file is
// reserved for the BTD6-specific native patches BML may need on top of it.
//
// To compile this module, enable `externalNativeBuild` in app/build.gradle.kts
// (requires the Android NDK), then call the JNI function below from Kotlin via
// `System.loadLibrary("bml_native")`.

#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_bml_android_core_LoaderCore_nativeStatus(JNIEnv *env, jobject /* this */) {
    // TODO: report LemonLoader bootstrap state from inside the game process.
    return env->NewStringUTF("native-loader-placeholder");
}
