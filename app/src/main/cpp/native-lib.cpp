#include <jni.h>
#include <string>

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/dnn.hpp>
#include <opencv2/video.hpp>

#include "android/bitmap.h"


using namespace cv;

extern "C" JNIEXPORT jstring

JNICALL
Java_ec_edu_ups_proyecto_1vision_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

void bitmapToMat(JNIEnv * env, jobject bitmap, cv::Mat &dst, jboolean needUnPremultiplyAlpha){
    AndroidBitmapInfo  info;
    void*              pixels = 0;

    try {
        CV_Assert( AndroidBitmap_getInfo(env, bitmap, &info) >= 0 );
        CV_Assert( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                   info.format == ANDROID_BITMAP_FORMAT_RGB_565 );
        CV_Assert( AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0 );
        CV_Assert( pixels );
        dst.create(info.height, info.width, CV_8UC4);
        if( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 )
        {
            cv::Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(needUnPremultiplyAlpha) cvtColor(tmp, dst, cv::COLOR_mRGBA2RGBA);
            else tmp.copyTo(dst);
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            cv::Mat tmp(info.height, info.width, CV_8UC2, pixels);
            cvtColor(tmp, dst, cv::COLOR_BGR5652RGBA);
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch(const cv::Exception& e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        //jclass je = env->FindClass("org/opencv/core/CvException");
        jclass je = env->FindClass("java/lang/Exception");
        //if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
        return;
    }
}
void matToBitmap(JNIEnv * env, cv::Mat src, jobject bitmap, jboolean needPremultiplyAlpha) {
    AndroidBitmapInfo  info;
    void*              pixels = 0;
    try {
        CV_Assert( AndroidBitmap_getInfo(env, bitmap, &info) >= 0 );
        CV_Assert( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                   info.format == ANDROID_BITMAP_FORMAT_RGB_565 );
        CV_Assert( src.dims == 2 && info.height == (uint32_t)src.rows && info.width == (uint32_t)src.cols );
        CV_Assert( src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4 );
        CV_Assert( AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0 );
        CV_Assert( pixels );
        if( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 )
        {
            cv::Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type() == CV_8UC1)
            {
                cvtColor(src, tmp, cv::COLOR_GRAY2RGBA);
            } else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, cv::COLOR_RGB2RGBA);
            } else if(src.type() == CV_8UC4){
                if(needPremultiplyAlpha) cvtColor(src, tmp, cv::COLOR_RGBA2mRGBA);
                else src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            cv::Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if(src.type() == CV_8UC1)
            {
                cvtColor(src, tmp, cv::COLOR_GRAY2BGR565);
            } else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, cv::COLOR_RGB2BGR565);
            } else if(src.type() == CV_8UC4){
                cvtColor(src, tmp, cv::COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch(const cv::Exception& e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        //jclass je = env->FindClass("org/opencv/core/CvException");
        jclass je = env->FindClass("java/lang/Exception");
        //if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_ec_edu_ups_proyecto_1vision_ProcessingActivity_filters(
        JNIEnv *env,
        jobject thiz,
        jobject bitmap_in,
        jobject bitmap_out,
        jint h_min,
        jint s_min,
        jint v_min,
        jint h_max,
        jint s_max,
        jint v_max) {
    // TODO: implement filters()
    cv::Mat src;
    bitmapToMat(env,bitmap_in,src,false);
    cv::Mat tmp;
    cv::cvtColor(src,tmp,cv::COLOR_BGR2HSV);
    cv::inRange(tmp,cv::Scalar(h_min,s_min,v_min),cv::Scalar (h_max,s_max,v_max),tmp);

    matToBitmap(env,tmp,bitmap_out,false);
}


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
        Mat dst(infoOut.height, infoOut.width, CV_8UC4, pixelsOut);

        cvtColor(src, dst, COLOR_RGBA2GRAY);

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
