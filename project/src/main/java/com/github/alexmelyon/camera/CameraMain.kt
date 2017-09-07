package com.github.alexmelyon.camera

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.videoio.Videoio
import java.awt.BorderLayout
import javax.swing.JFrame

fun main(args: Array<String>) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    var cameraNum = 0
    System.getProperty("camera")?.let { cameraNum = it.toInt() }

    val imagePanel = ImagePanel()
    val jFrame = JFrame().apply {
        setSize(1280, 720)
        contentPane.add(BorderLayout.CENTER, imagePanel)
        isVisible = true
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

    VideoCaptureAutoCloseable(cameraNum).use { camera ->
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280.0)
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 720.0)

        if (!camera.isOpened) {
            println("Camera $cameraNum is not opened")
        } else {
            val frame = Mat()
            while (true) {
                if (camera.read(frame)) {
                    imagePanel.image = frame.toBufferedImage()
                    imagePanel.repaint()
                }
            }
        }
    }
}
