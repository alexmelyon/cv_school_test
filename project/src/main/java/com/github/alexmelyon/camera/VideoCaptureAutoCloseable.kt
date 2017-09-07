package com.github.alexmelyon.camera

import org.opencv.videoio.VideoCapture

/**
 * Created by JCD on 31.08.2017.
 */

class VideoCaptureAutoCloseable(cameraNum: Int) : VideoCapture(cameraNum), AutoCloseable {
    override fun close() {
        release()
    }
}