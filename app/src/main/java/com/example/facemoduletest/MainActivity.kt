package com.example.facemoduletest

import android.content.Intent
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Size
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LifecycleOwner
import com.example.facemoduletest.databinding.ActivityMainBinding
import com.example.facemoduletest.module.FaceDetectorFactory
import com.google.common.util.concurrent.ListenableFuture
import com.ml.quaterion.facenetdetection.BitmapUtils
import com.ml.quaterion.facenetdetection.FileReader
import com.ml.quaterion.facenetdetection.FrameAnalyser
import com.ml.quaterion.facenetdetection.model.FaceNetModel
import com.ml.quaterion.facenetdetection.model.Models
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val faceDetector by lazy { FaceDetectorFactory.create(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        faceDetector.initialize(binding.bboxOverlay, binding.previewView)
        faceDetector.startCamera()
        binding.faceSelector.setOnClickListener {
            faceDetector.setFaceImage()
        }
        binding.start.setOnClickListener {
            faceDetector.startRecognition()
        }
    }


}