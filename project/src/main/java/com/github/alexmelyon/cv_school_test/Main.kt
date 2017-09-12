package com.github.alexmelyon.cv_school_test

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by JCD on 07.09.2017.
 */

/**
 * Created by JCD on 30.08.2017.
 *
 * 1. +Crop
 * 2. +Gray
 * 3. +Flip
 * 4. Normalize
 * 5. Gaussian noise
 */
fun main(args: Array<String>) {
    println("JAVA LIBRARY PATH: ${System.getProperty("java.library.path")}")
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    listOf(
            "../fragments",
            "../fragments_greyscale",
            "../fragments_flip",
            "../fragments_normalization",
            "../fragments_noise")
            .forEach { directory ->
                if (!File(directory).exists()) {
                    Files.createDirectory(Paths.get(directory))
                }
            }
    File("../annotations").list().forEach { annotation ->
        val annotationFile = File("../annotations/$annotation")
        val pngFilename = annotationFile.nameWithoutExtension + ".png"
        annotationFile.readLines(Charsets.UTF_8).forEachIndexed { index, line ->
            CoordsXY.fromLine(line)?.let { coords ->
                processFile("../images/$pngFilename", index, coords)
            }
        }
    }
    println("OK")
}

fun processFile(filename: String, n: Int, coords: CoordsXY) {
    if (!File(filename).exists()) {
        throw FileNotFoundException(filename)
    }
    val src = Imgcodecs.imread(filename)
    val name = File(filename).nameWithoutExtension

    // 1. Crop
    val cropped = src.submat(coords.toRect())
    imwriteAssert("../fragments/${name}_${n}.png", cropped)

    // 2. Gray
    val grayed = Mat()
    Imgproc.cvtColor(cropped, grayed, Imgproc.COLOR_RGB2GRAY)
    imwriteAssert("../fragments_greyscale/${name}_${n}_grey.png", grayed)

    // 3. Flip
    val flipped = Mat()
    Core.flip(cropped, flipped, FlipCode.HORIZONTAL.ordinal)
    imwriteAssert("../fragments_flip/${name}_${n}_flip.png", flipped)

    // 4. Normalize
    val normalized = Mat()
    Core.normalize(grayed, normalized, 0.0, 255.0, Core.NORM_MINMAX)
    imwriteAssert("../fragments_normalization/${name}_${n}_normalize.png", normalized)

    // 5. Gaussian noise
    val gauss = Mat(cropped.size(), cropped.type())
    Core.randn(gauss, 0.0, Math.sqrt(255.0))
    val gaussian = cropped + gauss
    imwriteAssert("../fragments_noise/${name}_${n}_noise.png", gaussian)
}

operator fun Mat.plus(other: Mat): Mat {
    val res = Mat()
    Core.add(this, other, res)
    return res
}

enum class FlipCode {
    VERTICAL,
    HORIZONTAL
}

fun imwriteAssert(filename: String, img: Mat) {
    val res = Imgcodecs.imwrite(filename, img)
    if (!res) {
        throw IOException("Cannot write file '$filename'")
    }
}

data class CoordsXY(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
    companion object {
        private val REGEXP = """(\d+),\s*(\d+),\s*(\d+),\s*(\d+)""".toPattern()
        fun fromLine(line: String): CoordsXY? {
            val matcher = REGEXP.matcher(line)
            if (matcher.find()) {
                return CoordsXY(matcher.group(1).toInt(), matcher.group(2).toInt(), matcher.group(3).toInt(), matcher.group(4).toInt())
            } else {
                return null
            }
        }
    }

    fun toRect(): Rect = Rect(x1, y1, x2 - x1, y2 - y1)
}