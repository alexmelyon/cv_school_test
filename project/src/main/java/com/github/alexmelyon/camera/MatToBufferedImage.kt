package com.github.alexmelyon.camera

import org.opencv.core.Mat
import java.awt.image.BufferedImage

/**
 * Created by JCD on 31.08.2017.
 */

fun Mat.toBufferedImage(): BufferedImage {
    val mat = this
    val data = ByteArray(mat.width() * mat.height() * mat.elemSize().toInt())
    val type: Int
    mat.get(0, 0, data)

    when (mat.channels()) {
        1 -> type = BufferedImage.TYPE_BYTE_GRAY
        3 -> {
            type = BufferedImage.TYPE_3BYTE_BGR
            // bgr to rgb
            var b: Byte
            var i = 0
            while (i < data.size) {
                b = data[i]
                data[i] = data[i + 2]
                data[i + 2] = b
                i = i + 3
            }
        }
        else -> throw IllegalStateException("Unsupported number of channels")
    }

    val out = BufferedImage(mat.width(), mat.height(), type)

    out.raster.setDataElements(0, 0, mat.width(), mat.height(), data)

    return out
}