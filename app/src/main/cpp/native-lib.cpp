#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <android/bitmap.h>

using namespace cv;

void applyGaussianSobelFilter(cv::Mat& src, cv::Mat& dst, int gaussianKernelSize) {
    // Aplicar filtro gaussiano
    cv::Mat blurred;
    cv::GaussianBlur(src, blurred, cv::Size(gaussianKernelSize, gaussianKernelSize), 0);

    // Aplicar el operador Sobel
    cv::Mat sobelX, sobelY, sobelCombined;
    cv::Sobel(blurred, sobelX, CV_16S, 1, 0);
    cv::Sobel(blurred, sobelY, CV_16S, 0, 1);
    cv::convertScaleAbs(sobelX, sobelX);
    cv::convertScaleAbs(sobelY, sobelY);
    cv::addWeighted(sobelX, 0.5, sobelY, 0.5, 0, sobelCombined);

    // Asignar la matriz resultante a la matriz de destino
    dst = sobelCombined.clone();
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

extern "C"
JNIEXPORT void JNICALL
Java_ec_edu_ups_proyecto_1vision_ProcessingActivity_filters(
        JNIEnv* env,
        jobject /* this */,
        jobject bitmapIn,
        jobject bitmapOut,
        jint hMin,
        jint sMin,
        jint vMin,
        jint hMax,
        jint sMax,
        jint vMax) {


    cv::Mat src;
    bitmapToMat(env, bitmapIn, src, false);
    //cv::flip(src, src, 0);
    cv::Mat tmp;
    cv::cvtColor(src, tmp, cv::COLOR_BGR2HSV);
    cv::inRange(tmp, cv::Scalar(hMin, sMin, vMin), cv::Scalar(hMax, sMax, vMax), tmp);

    matToBitmap(env, tmp, bitmapOut, false);
}

extern "C"
JNIEXPORT void JNICALL
Java_ec_edu_ups_proyecto_1vision_ProcessingActivity_applyGaussianSobelFilter(
        JNIEnv* env,
        jobject /* this */,
        jobject bitmapIn,
        jobject bitmapOut,
        jint gaussianKernelSize) {

    cv::Mat src;
    bitmapToMat(env, bitmapIn, src, false);

    // Aplicar el filtro gaussiano seguido del operador Sobel
    cv::Mat result;
    applyGaussianSobelFilter(src, result, gaussianKernelSize);

    // Convertir la matriz resultante de nuevo a bitmap
    matToBitmap(env, result, bitmapOut, false);
}

extern "C" {

JNIEXPORT void JNICALL
Java_ec_edu_ups_proyecto_1vision_ProcessingActivity_applyScarletWitchEffect(
        JNIEnv *env,
        jobject /* this */,
        jobject bitmapIn,
        jobject bitmapOut) {

    // Convertir los bitmaps de entrada y salida a matrices de OpenCV
    Mat src, dst;
    bitmapToMat(env, bitmapIn, src, false);

    // Convertir la imagen a escala de grises
    cvtColor(src, dst, COLOR_BGR2GRAY);

    // Aplicar un umbral para resaltar los píxeles rojos
    Mat redMask;
    inRange(src, Scalar(0, 0, 100), Scalar(100, 100, 255), redMask);

    // Aplicar el efecto Scarlet Witch combinando la imagen en escala de grises con la máscara roja
    bitwise_and(dst, redMask, dst);

    // Convertir la matriz de salida a bitmap
    matToBitmap(env, dst, bitmapOut, false);
}
}
