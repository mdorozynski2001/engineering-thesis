package com.example.inventoryapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.databinding.ActivityFastInventoryBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.io.IOException




class FastInventory : AppCompatActivity() {
    private lateinit var binding: ActivityFastInventoryBinding
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var collectedBarcode = ""
    private var previousBbarcode = ""
    private var activeBarcode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFastInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanButton.setOnClickListener {
            if (collectedBarcode == "" || collectedBarcode == previousBbarcode) {
                Toast.makeText(this@FastInventory, "No new barcode detected", Toast.LENGTH_SHORT)
                    .show()
                binding.barcode.text = getString(R.string.no_barcode)
                binding.nameText.text = "x"
                binding.numberInDatabase.text = "x"
                binding.numberReal.hint = "x"
                activeBarcode = ""



                binding.applyButton.isEnabled = false
            } else {
                Toast.makeText(this@FastInventory, "New barcode detected", Toast.LENGTH_SHORT)
                    .show()
                previousBbarcode = collectedBarcode
                activeBarcode = collectedBarcode
                binding.barcode.text = previousBbarcode

                val field = arrayOfNulls<String>(4)
                field[0] = "InventoryTargetType"
                field[1] = "InventoryID"
                field[2] = "productInfo"
                field[3] = "productInfoType"

                val data = arrayOfNulls<String>(4)
                data[0] = ActiveInventory.type
                data[1] = ActiveInventory.id.toString()
                data[2] = previousBbarcode
                data[3] = "barcode"

                val putData = PutData(
                    "http://$IpGlobal/Android/findProduct.php", "POST", field, data
                )

                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        val result = putData.result
                        if (result == "Barcode doesn't exist" || result == "Error: Database connection" || result == "All fields are required") {
                            Toast.makeText(this@FastInventory, result, Toast.LENGTH_SHORT)
                                .show()
                            Log.i("PutData", result)

                            binding.nameText.text = "x"
                            binding.numberInDatabase.text = "x"
                            binding.applyButton.isEnabled = false
                        } else {
                            Log.i("PutData", result)

                            val gson = Gson()
                            val product = gson.fromJson<List<Map<String, String>>>(
                                result, object : TypeToken<List<Map<String, String>>>() {}.type
                            )
                            ActiveProduct.id = product[0]["id"]!!.toInt()
                            ActiveProduct.name = product[0]["name"].toString()
                            ActiveProduct.barcode = product[0]["barcode"].toString()
                            ActiveProduct.number = product[0]["number"]!!.toInt()
                            ActiveProduct.totalNumber = product[0]["total_number"]!!.toInt()
                            ActiveProduct.price = product[0]["price"]!!.toDouble()


                            binding.nameText.text = ActiveProduct.name
                            binding.numberInDatabase.text = ActiveProduct.number.toString()
                            binding.applyButton.isEnabled = true
                        }
                    }
                }
            }
        }

        binding.applyButton.setOnClickListener {
            val realNumber = binding.numberReal.text.toString()
            if(correctProductNumber(this@FastInventory, realNumber, ActiveProduct.id)){
                binding.numberInDatabase.text = realNumber
            }
        }
    }


    private fun initBc() {
        barcodeDetector =
            BarcodeDetector.Builder(this@FastInventory).setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()
        cameraSource = CameraSource.Builder(this@FastInventory, barcodeDetector)
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