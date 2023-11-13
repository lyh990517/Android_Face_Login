package com.example.facemoduletest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.facemoduletest.databinding.ActivityMainBinding
import com.example.face_recognition.FaceRecognizerFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val faceRecognizer by lazy { FaceRecognizerFactory.create(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        faceRecognizer.initialize(binding.bboxOverlay, binding.previewView)
        faceRecognizer.startCamera()
        binding.faceSelector.setOnClickListener {
            faceRecognizer.setFaceImage()
        }
        binding.start.setOnClickListener {
            faceRecognizer.startRecognition()
        }

        binding.changeFacing.setOnClickListener {
            faceRecognizer.changeCameraFacing()
        }
        faceRecognizer.getIdentifiedUser {
            if (it != "Unknown") {
                binding.userName.text = "Login Success!"
            } else {
                binding.userName.text = "Login Fail...!"
            }
        }
    }


}