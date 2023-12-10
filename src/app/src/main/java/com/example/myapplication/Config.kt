package com.example.myapplication

var IpGlobal = ""

object ActiveInventory {
    var id : Int = -1
    var name : String = ""
    var type : String = "" // productions or warehouses
    var allOrRandomProducts : String = "" // AllProducts or RandomProducts
    var productsNumberToInventory : Int = -1
    var productsNumberTotal : Int = -1
    var productsIDs : List<Int> = emptyList()
    var productsIDsInList : List<Int> = emptyList()

}

data class Product(
    var id: Int,
    var name: String,
    var barcode: String,
    var number: Int,
    var totalNumber: Int,
    var price: Double
)
object ActiveProduct {
    var id: Int = -1
    var name: String = ""
    var barcode: String = ""
    var number: Int = -1
    var totalNumber: Int = -1
    var price: Double = -1.0
}