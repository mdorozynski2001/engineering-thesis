package com.example.inventoryapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import com.example.inventoryapp.databinding.ActivityComplexInventoryBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.io.IOException
import java.lang.StrictMath

class ComplexInventory : AppCompatActivity() {
    private lateinit var binding: ActivityComplexInventoryBinding
    private var productsList = emptyList<Product>()
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private var collectedBarcode = ""
    private var activeProduct :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplexInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getProductsData()
        setTextViews()

        binding.buttonNextProduct.setOnClickListener {
            activeProduct = StrictMath.min(ActiveInventory.productsNumberToInventory-1,activeProduct+1)
            synchronizeProductWithDB(activeProduct)
            setTextViews()

        }

        binding.buttonPreviousProduct.setOnClickListener {
            activeProduct = StrictMath.max(0, activeProduct-1)
            synchronizeProductWithDB(activeProduct)
            setTextViews()
        }

        binding.buttonScanProduct.setOnClickListener {
            if(collectedBarcode == productsList[activeProduct].barcode){
                Toast.makeText(this@ComplexInventory,"Product correct", Toast.LENGTH_SHORT).show()
                binding.applyButton.isEnabled = true
                binding.numberReal.isEnabled = true
            }
            else{
                Toast.makeText(this@ComplexInventory,"Product incorrect", Toast.LENGTH_SHORT).show()
            }
        }

        binding.applyButton.setOnClickListener {
            val realNumber = binding.numberReal.text.toString()
            if(correctProductNumber(this@ComplexInventory, realNumber, productsList[activeProduct].id)) {
                synchronizeProductWithDB(activeProduct)
                binding.dbNumberInfo.text = realNumber
            }
        }

    }

    private fun synchronizeProductWithDB(id: Int){
        val product = getProduct(productsList[id].id)
        if (product.id != 0 && product.barcode != ""){
            productsList[id].number = product.number
            productsList[id].totalNumber = product.totalNumber
        }
    }

    private fun setTextViews(){
        binding.productNameView.text = productsList[activeProduct].name
        binding.productBarcodeView.text = productsList[activeProduct].barcode
        binding.dbNumberInfo.text = productsList[activeProduct].number.toString()
        binding.applyButton.isEnabled = false
        binding.numberReal.isEnabled = false
    }


    private fun getProductsData(){
        if(ActiveInventory.allOrRandomProducts == "AllProducts"){
            for(product in ActiveInventory.productsIDs){
                productsList = productsList + getProduct(product)
            }


        }
        else if(ActiveInventory.allOrRandomProducts == "RandomProducts"){
            for(id in ActiveInventory.productsIDsInList){
                productsList = productsList + getProduct(ActiveInventory.productsIDs[id])
            }
        }
        Log.i("PutData",productsList.toString())
    }



    private fun getProduct(productInfo : Int): Product {
        var product = Product(
            id = 0,
            name = "",
            barcode = "",
            number = 0,
            totalNumber = 0,
            price = 0.0

        )

        val field = arrayOfNulls<String>(4)
        field[0] = "InventoryTargetType"
        field[1] = "InventoryID"
        field[2] = "productInfo"
        field[3] = "productInfoType"

        val data = arrayOfNulls<String>(4)
        data[0] = ActiveInventory.type
        data[1] = ActiveInventory.id.toString()
        data[2] = productInfo.toString()
        data[3] = "id"

        val putData = PutData(
            "http://$IpGlobal/Android/findProduct.php", "POST", field, data
        )

        if (putData.startPut()) {
            if (putData.onComplete()) {
                val result = putData.result
                if (result == "Barcode doesn't exist" || result == "Error: Database connection" || result == "All fields are required") {
                    Toast.makeText(this@ComplexInventory, result, Toast.LENGTH_SHORT)
                        .show()
                    Log.i("PutData", result)
                } else {
                    Log.i("PutData", result)

                    val gson = Gson()
                    val jsonProduct = gson.fromJson<List<Map<String, String>>>(
                        result, object : TypeToken<List<Map<String, String>>>() {}.type
                    )
                    val returnProduct = jsonToProduct(jsonProduct[0])
                    product = returnProduct
                }
            }
        }

        return product
    }


    private fun initBc() {
        barcodeDetector =
            BarcodeDetector.Builder(this@ComplexInventory).setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()
        cameraSource = CameraSource.Builder(this@ComplexInventory, barcodeDetector)
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

