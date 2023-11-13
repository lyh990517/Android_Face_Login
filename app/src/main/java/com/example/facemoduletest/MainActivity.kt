package com.example.facemoduletest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.facemoduletest.databinding.ActivityMainBinding
import com.example.face_recognition.FaceDetectorFactory

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

        binding.changeFacing.setOnClickListener {
            faceDetector.changeCameraFacing()
        }
    }


}