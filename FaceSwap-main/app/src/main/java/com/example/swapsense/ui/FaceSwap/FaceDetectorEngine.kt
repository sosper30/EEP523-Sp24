package com.example.swapsense.ui.FaceSwap

import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * A class to encapsulate the ML Kit Face Detection functionality.
 * Provides methods to initialize face detection with high accuracy options,
 * to process images, and to release resources when no longer needed.
 */
class FaceDetectorEngine() {

    private val detector: FaceDetector

    // Implement FaceDetectorOptions
    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()


    init {
        // Detect Face
        val options = highAccuracyOpts
        detector = FaceDetection.getClient(options)
    }

    fun stop() {
        // close the detector
        detector.close()
    }

    fun detectInImage(image: InputImage): Task<List<Face>> {
        // Return detected image
        return detector.process(image)
    }

}