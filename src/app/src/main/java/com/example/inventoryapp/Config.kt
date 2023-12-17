package com.example.inventoryapp

var IpGlobal = ""

object ActiveInventory {
    var id : Int = -1
    var name : String = ""
    var allOrRandomProducts : String = "" // AllProducts or RandomProducts
    var productsAmountToInventory : Int = -1
    var productsAmountTotal : Int = -1
    var productsIDs : List<Int> = emptyList()
    var productsIDsInList : List<Int> = emptyList()

}

data class Product(
    var id: Int,
    var name: String,
    var barcode: String,
    var amount: Int,
    var totalAmount: Int,
    var price: Double
)
object ActiveProduct {
    var id: Int = -1
    var name: String = ""
    var barcode: String = ""
    var amount: Int = -1
    var totalAmount: Int = -1
    var price: Double = -1.0
}