package com.example.inventoryapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishnusivadas.advanced_httpurlconnection.PutData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getTableNamesList(table: String): Array<String> {
    var tableList = emptyArray<String>()

    withContext(Dispatchers.IO) {
        val field = arrayOfNulls<String>(1)
        field[0] = "table"
        val data = arrayOfNulls<String>(1)
        data[0] = table
        val putDataWarehouse = PutData(
            "http://$IpGlobal/Android/getTableList.php",
            "POST",
            field,
            data
        )

        if (putDataWarehouse.startPut()) {
            if (putDataWarehouse.onComplete()) {
                val resultWarehouse = putDataWarehouse.result
                if (resultWarehouse == "Table is empty") {
                    Log.i("PutData", "getTableList $resultWarehouse")
                } else {
                    val gson = Gson()
                    val listWarehouse = gson.fromJson<List<Map<String, String>>>(
                        resultWarehouse, object : TypeToken<List<Map<String, String>>>() {}.type
                    )
                    tableList = listWarehouse.mapNotNull { it["name"] }.toTypedArray()
                }
            }
        }
    }

    return tableList
}

fun getInventoryId(): Boolean {
    var funResult = false
    val field = arrayOfNulls<String>(1)
    field[0] = "InventoryName"

    val data = arrayOfNulls<String>(1)
    data[0] = ActiveInventory.name
    val putData = PutData(
        "http://$IpGlobal/Android/getInventoryTargetData.php",
        "POST",
        field,
        data
    )
    if (putData.startPut()) {
        if (putData.onComplete()) {
            val result = putData.result
            if (result != "Inventory targed doesn't exist" || result !="Error: Database connection") {

                Log.i("PutData", "getInventoryTargetData $result")
                val gson = Gson()
                val resultTable = gson.fromJson<List<Map<String, String>>>(
                    result, object : TypeToken<List<Map<String, String>>>() {}.type
                )
                ActiveInventory.id = resultTable[0]["id"]!!.toInt()
                funResult = true
            }
            Log.i("PutData", "getInventoryTargetData $result")
        }
    }
    return funResult
}



fun getProductsFromInventoryTarget(): Boolean {
    var funResult = false
    val field = arrayOfNulls<String>(1)
    field[0] = "InventoryId"

    val data = arrayOfNulls<String>(1)
    data[0] = ActiveInventory.id.toString()
    val putData = PutData(
        "http://$IpGlobal/Android/getProductsFromInventoryTarget.php",
        "POST",
        field,
        data
    )
    if (putData.startPut()) {
        if (putData.onComplete()) {
            val result = putData.result
            if (result != "Inventory target is empty") {

                Log.i("PutData", "getProductAmountInInventoryTarget $result")
                val gson = Gson()
                val products = gson.fromJson<List<Map<String, String>>>(
                    result, object : TypeToken<List<Map<String, String>>>() {}.type
                )
                ActiveInventory.productsAmountTotal = products.size
                val productIdList = emptyList<Int>().toMutableList()
                for(product in products){
                    productIdList += product["product_id"]!!.toInt()
                }
                ActiveInventory.productsIDs = productIdList
                Log.i("PutData","productIDs: ${ActiveInventory.productsIDs}")
                funResult = true
            }
            Log.i("PutData", "getProductAmountInInventoryTarget $result")
        }
    }
    return funResult

}

fun jsonToProduct(product_json: Map<String, String>): Product {
    return Product(
        id = product_json["id"]!!.toInt(),
        name = product_json["name"].toString(),
        barcode = product_json["barcode"].toString(),
        amount = product_json["amount"]!!.toInt(),
        totalAmount = product_json["total_amount"]!!.toInt(),
        price = product_json["price"]!!.toDouble()
    )
}

fun correctProductAmount(context: Context, realAmount: String, id: Int): Boolean {
    var funResult = false
    if (realAmount.toInt() >= 0) {
        val field = arrayOfNulls<String>(3)
        field[0] = "InventoryID"
        field[1] = "product_id"
        field[2] = "amount"

        val data = arrayOfNulls<String>(3)
        data[0] = ActiveInventory.id.toString()
        data[1] = id.toString()
        data[2] = realAmount

        val putData = PutData(
            "http://$IpGlobal/Android/correctProductAmount.php", "POST", field, data
        )

        if (putData.startPut()) {
            if (putData.onComplete()) {
                val result = putData.result
                if (result == "Correction succeeded") {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT)
                        .show()
                    funResult = true
                    Log.i("PutData", result)
                }
                else {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT)
                        .show()
                    Log.i("PutData", result)
                }
            }
        }
    }
    return funResult
}
