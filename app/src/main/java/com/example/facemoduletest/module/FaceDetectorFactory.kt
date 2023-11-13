package com.example.facemoduletest.module

import android.content.Context

class FaceDetectorFactory {
    companion object{
        fun create(context: Context): FaceDetector = FaceDetectorImpl(context)
    }
}