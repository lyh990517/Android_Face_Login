package com.example.face_recognition

import android.content.Context

class FaceRecognizerFactory {
    companion object{
        fun create(context: Context): FaceRecognizer = FaceRecognizerImpl(context)
    }
}