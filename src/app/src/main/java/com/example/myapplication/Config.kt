package com.example.myapplication

object Config {
    var activeInventoryTargetName : String = ""
    var activeInventoryTargetType : String = ""

}
object ActiveProduct {
    var id: Int = -1
    var name: String = ""
    var barcode: String = ""
    var number: Int = -1
    var totalNumber: Int = -1
    var price: Double = -1.0
}

object UserInfo {
    var login: String = "login"
    var password: String = calculateMd5("password")
}