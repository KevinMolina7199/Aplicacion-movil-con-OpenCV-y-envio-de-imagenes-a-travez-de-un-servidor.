#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <android/bitmap.h>

using namespace cv;

extern "C" {

JNIEXPORT void JNICALL
Java_ec_edu_ups_proyecto_1vision_ProcessingActivity_convertToGrayscale(JNIEnv *env, jobject thiz,
                                                                       jobject bitmap_in,
                                                                       jobject bitmap_out) {
    AndroidBitmapInfo infoIn;
    void *pixelsIn;
    AndroidBitmapInfo infoOut;
    void *pixelsOut;

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap_in, &infoIn) >= 0);
        CV_Assert(infoIn.format == ANDROID_BITMAP_FORMAT_RGBA_8888);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap_in, &pixelsIn) >= 0);
        CV_Assert(pixelsIn);

        CV_Assert(AndroidBitmap_getInfo(env, bitmap_out, &infoOut) >= 0);
        CV_Assert(infoOut.format == ANDROID_BITMAP_FORMAT_RGBA_8888);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap_out, &pixelsOut) >= 0);
        CV_Assert(pixelsOut);

        Mat src(infoIn.height, infoIn.width, CV_8UC4, pixelsIn);
        Mat gray(infoIn.height, infoIn.width, CV_8UC1);
        Mat dst(infoOut.height, infoOut.width, CV_8UC4, pixelsOut);

        cvtColor(src, gray, COLOR_RGBA2GRAY);
        cvtColor(gray, dst, COLOR_GRAY2RGBA);

        AndroidBitmap_unlockPixels(env, bitmap_in);
        AndroidBitmap_unlockPixels(env, bitmap_out);

    } catch (const cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap_in);
        AndroidBitmap_unlockPixels(env, bitmap_out);

        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap_in);
        AndroidBitmap_unlockPixels(env, bitmap_out);

        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
    }
}

} // extern "C"
