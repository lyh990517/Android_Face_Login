package com.example.face_recognition

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT_TREE
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Size
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LifecycleOwner
import com.example.face_recognition.core.FileReader
import com.example.face_recognition.core.FrameAnalyser
import com.google.common.util.concurrent.ListenableFuture
import com.ml.quaterion.facenetdetection.model.FaceNetModel
import com.ml.quaterion.facenetdetection.model.Models
import java.util.concurrent.Executors

class FaceDetectorImpl(private val context: Context) : FaceDetector {

    private val contentResolver = context.contentResolver
    private lateinit var previewView: PreviewView
    private lateinit var frameAnalyser: FrameAnalyser
    private lateinit var faceNetModel: FaceNetModel
    private lateinit var fileReader: FileReader
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT
    private var images = ArrayList<Pair<String, Bitmap>>()

    private val useGpu = true
    private val useXNNPack = true
    private val modelInfo = Models.FACENET

    private val fileReaderCallback = object : FileReader.ProcessCallback {
        override fun onProcessCompleted(
            data: ArrayList<Pair<String, FloatArray>>,
            numImagesWithNoFaces: Int
        ) {
            frameAnalyser.faceList = data
        }
    }

    override fun initialize(
        boundingBoxOverlay: com.example.face_recognition.core.BoundingBoxOverlay,
        previewView: PreviewView
    ) {
        boundingBoxOverlay.cameraFacing = cameraFacing
        boundingBoxOverlay.setWillNotDraw(false)
        boundingBoxOverlay.setZOrderOnTop(true)
        this.previewView = previewView
        faceNetModel = FaceNetModel(context, modelInfo, useGpu, useXNNPack)
        frameAnalyser = FrameAnalyser(
            context,
            boundingBoxOverlay,
            faceNetModel
        )
        fileReader = FileReader(faceNetModel)
    }

    override fun startCamera() {
        startCameraPreview()
    }

    override fun changeCameraFacing() {
        cameraFacing =
            if (cameraFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
        startCameraPreview()
    }

    override fun startRecognition() {
        fileReader.run(images, fileReaderCallback)
    }

    override fun setFaceImage() {
        directoryAccessLauncher.launch(Intent(ACTION_OPEN_DOCUMENT_TREE))
    }

    private fun startCameraPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraFacing)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        val imageFrameAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(480, 640))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
        imageFrameAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), frameAnalyser)
        cameraProvider.bindToLifecycle(
            context as LifecycleOwner,
            cameraSelector,
            preview,
            imageFrameAnalysis
        )
    }

    private val directoryAccessLauncher =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            setFaceImageForDetection(it.data?.data!!)
        }

    private fun setFaceImageForDetection(uri: Uri) {
        val childrenUri =
            DocumentsContract.buildChildDocumentsUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
            )
        val tree = DocumentFile.fromTreeUri(context, childrenUri)
        if (tree!!.listFiles().isNotEmpty()) {
            for (doc in tree.listFiles()) {
                if (doc.isDirectory) {
                    val name = doc.name!!
                    for (imageDocFile in doc.listFiles()) {
                        images.add(Pair(name, getFixedBitmap(imageDocFile.uri)))
                    }
                }
            }
        }
    }

    private fun getFixedBitmap(imageFileUri: Uri): Bitmap {
        var imageBitmap = com.example.face_recognition.core.BitmapUtils.getBitmapFromUri(
            contentResolver,
            imageFileUri
        )
        val exifInterface = ExifInterface(contentResolver.openInputStream(imageFileUri)!!)
        imageBitmap =
            when (exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> com.example.face_recognition.core.BitmapUtils.rotateBitmap(
                    imageBitmap,
                    90f
                )

                ExifInterface.ORIENTATION_ROTATE_180 -> com.example.face_recognition.core.BitmapUtils.rotateBitmap(
                    imageBitmap,
                    180f
                )

                ExifInterface.ORIENTATION_ROTATE_270 -> com.example.face_recognition.core.BitmapUtils.rotateBitmap(
                    imageBitmap,
                    270f
                )

                else -> imageBitmap
            }
        return imageBitmap
    }
}