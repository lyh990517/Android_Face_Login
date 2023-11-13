package com.example.facemoduletest.module

import androidx.camera.view.PreviewView
import com.ml.quaterion.facenetdetection.BoundingBoxOverlay

interface FaceDetector {

    fun initialize(boundingBoxOverlay: BoundingBoxOverlay, previewView: PreviewView)
    fun startCamera()
    fun startRecognition()
    fun setFaceImage()
}