package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vishnusivadas.advanced_httpurlconnection.PutData
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

        val inputLogin = findViewById<EditText>(R.id.login)
        val inputPassword = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val loginError = findViewById<TextView>(R.id.loginError)

        loginButton.setOnClickListener {
            val login = inputLogin.text.toString()
            val password = calculateMd5(inputPassword.text.toString())

            if (login != "" && password != "") {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    val field = arrayOfNulls<String>(2)
                    field[0] = "username"
                    field[1] = "password"

                    val data = arrayOfNulls<String>(2)
                    data[0] = login
                    data[1] = password
                    val putData = PutData(
                        "http://192.168.0.38/Android/login.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            if (result == "Login Success") {
                                val intent = Intent(this@MainActivity, Menu::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            } else {
                                loginError.visibility = View.VISIBLE
                            }
                            Log.i("PutData", result)
                        }
                    }
                }
            }
        }

    }
}