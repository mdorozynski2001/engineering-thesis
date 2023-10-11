package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

object UserInfo{
    var login: String ="login"
    var password: String ="password"
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val login = findViewById<EditText>(R.id.login)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener{
            if(login.text.toString()==UserInfo.login && password.text.toString()==UserInfo.password) {
                val intent = Intent(this@MainActivity, Menu::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
            else{
                val loginError = findViewById<TextView>(R.id.loginError)
                loginError.visibility = View.VISIBLE

            }
        }

    }
}