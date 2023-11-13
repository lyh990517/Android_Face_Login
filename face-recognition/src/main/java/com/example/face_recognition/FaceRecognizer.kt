package com.example.face_recognition

import androidx.camera.view.PreviewView
import com.example.face_recognition.core.BoundingBoxOverlay

interface FaceRecognizer {

    fun initialize(boundingBoxOverlay: BoundingBoxOverlay, previewView: PreviewView)
    fun startCamera()
    fun changeCameraFacing()
    fun startRecognition()
    fun setFaceImage()
    fun getIdentifiedUser(get: (String) -> Unit)
}