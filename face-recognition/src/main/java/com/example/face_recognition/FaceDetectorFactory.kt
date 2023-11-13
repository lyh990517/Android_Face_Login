package com.example.face_recognition

import android.content.Context

class FaceDetectorFactory {
    companion object{
        fun create(context: Context): FaceDetector = FaceDetectorImpl(context)
    }
}