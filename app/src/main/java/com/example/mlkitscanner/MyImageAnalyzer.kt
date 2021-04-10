package com.example.mlkitscanner

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.FragmentManager
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class MyImageAnalyzer(
    private val fragmentManager: FragmentManager,
    private val context: Context
): ImageAnalysis.Analyzer {
    private var bottomSheet = BarcodeResultBottomSheet()

    override fun analyze(image: ImageProxy) {
        scanBarcode(image)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun scanBarcode(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(inputImage)
                .addOnCompleteListener {
                    imageProxy.close()
                    if (it.isSuccessful) {
                        readBarcodeData(it.result as List<Barcode>)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }

    private fun readBarcodeData(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            Log.d("ImageAnalyzer", "readBarcodeData: type -> ${barcode.valueType}")
            when (barcode.valueType) {
                Barcode.TYPE_URL -> {
                    val url = barcode.url?.url

                    if (!bottomSheet.isAdded)
                        bottomSheet.show(fragmentManager, "")

                    bottomSheet.updateUrl(url.toString())
                }
                Barcode.TYPE_TEXT -> {
                    val data = barcode.displayValue

                    Toast.makeText(context, data.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}