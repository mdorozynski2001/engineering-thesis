package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.math.BigInteger
import java.security.MessageDigest

object UserInfo {
    var login: String = "login"
    var password: String = calculateMd5("password")
}

fun calculateMd5(password: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(password.toByteArray())).toString(16).padStart(32, '0')
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val login = findViewById<EditText>(R.id.login)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            if (login.text.toString() == UserInfo.login && calculateMd5(password.text.toString()) == UserInfo.password) {
                val intent = Intent(this@MainActivity, Menu::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            } else {
                val loginError = findViewById<TextView>(R.id.loginError)
                loginError.visibility = View.VISIBLE

            }
        }

    }
}