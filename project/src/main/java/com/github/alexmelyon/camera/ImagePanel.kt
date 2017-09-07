package com.github.alexmelyon.camera

import java.awt.Graphics
import java.awt.Image
import java.awt.image.ImageObserver
import javax.swing.JPanel

/**
 * Created by JCD on 31.08.2017.
 */

class ImagePanel : JPanel() {

    var image: Image? = null

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(image, 0, 0, null)
//        val imgWidth = image?.getWidth(null) ?: 0
//        val imgHeight = image?.getHeight(null) ?: 0
//        if (imgWidth > width || imgHeight > height) {
//            setSize(imgWidth, imgHeight)
//        }
    }
}