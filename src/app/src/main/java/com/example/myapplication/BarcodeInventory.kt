package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityBarcodeInventoryBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.io.IOException


data class Product(
    val id: String,
    val name: String,
    val barcode: String,
    val total_number: String,
    val price: String,
)

class BarcodeInventory : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeInventoryBinding
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var collectedBarcode = ""
    private var previousBbarcode = ""
    private var activeBarcode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanButton.setOnClickListener {
            if (collectedBarcode == "" || collectedBarcode == previousBbarcode) {
                Toast.makeText(this@BarcodeInventory, "No new barcode detected", Toast.LENGTH_SHORT)
                    .show()
                binding.barcode.text = getString(R.string.no_barcode)
                binding.nameText.text = "x"
                binding.numberInDatabase.text = "x"
                binding.numberReal.hint = "x"
                activeBarcode = ""
                


                binding.applyButton.isEnabled = false
            } else {
                Toast.makeText(this@BarcodeInventory, "New barcode detected", Toast.LENGTH_SHORT)
                    .show()
                previousBbarcode = collectedBarcode
                activeBarcode = collectedBarcode
                binding.barcode.text = previousBbarcode

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    val field = arrayOfNulls<String>(2)
                    field[0] = "table"
                    field[1] = "barcode"

                    val data = arrayOfNulls<String>(2)
                    data[0] = "products"
                    data[1] = previousBbarcode

                    val putData = PutData(
                        "http://192.168.0.201/Android/findProduct.php", "POST", field, data
                    )

                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            if (result == "Barcode doesn't exist") {
                                Toast.makeText(this@BarcodeInventory, result, Toast.LENGTH_SHORT)
                                    .show()

                                binding.nameText.text = "x"
                                binding.numberInDatabase.text = "x"
                                binding.applyButton.isEnabled = false
                            } else {

                                val gson = Gson()
                                val product = gson.fromJson<List<Map<String, String>>>(
                                    result, object : TypeToken<List<Map<String, String>>>() {}.type
                                )
                                binding.nameText.text = product[0]["name"]
                                binding.numberInDatabase.text = product[0]["total_number"]
                                binding.applyButton.isEnabled = true
                            }
                        }
                    }
                }
            }
        }

        binding.applyButton.setOnClickListener {
            val realNumber = binding.numberReal.text.toString()
            if (realNumber.toInt() >= 0) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    val field = arrayOfNulls<String>(3)
                    field[0] = "table"
                    field[1] = "barcode"
                    field[2] = "number"

                    val data = arrayOfNulls<String>(3)
                    data[0] = "products"
                    data[1] = collectedBarcode
                    data[2] = realNumber

                    val putData = PutData(
                        "http://192.168.0.201/Android/correctProductNumber.php", "POST", field, data
                    )

                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            if (result == "Correction succeeded") {
                                Toast.makeText(this@BarcodeInventory, result, Toast.LENGTH_SHORT)
                                    .show()
                                Log.i("PutData", result)
                                binding.numberInDatabase.text = realNumber
                            }
                        }
                    }
                }
            }
        }
    }


    private fun initBc() {
        barcodeDetector =
            BarcodeDetector.Builder(this@BarcodeInventory).setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()
        cameraSource = CameraSource.Builder(this@BarcodeInventory, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true)
            .setFacing(CameraSource.CAMERA_FACING_BACK).build()
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder) {
                try {
                    cameraSource.start(binding.surfaceView.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                cameraSource.stop()
            }

        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    applicationContext, "Barcode scanner has been stopped", Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    collectedBarcode = barcodes.valueAt(0).displayValue
                }
            }

        })
    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        initBc()
    }
}