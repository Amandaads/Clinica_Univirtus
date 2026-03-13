package com.example.clinica_univirtus

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_test = findViewById<Button>(R.id.btn_cadastro)
        btn_test.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        val text = findViewById<TextView>(R.id.txt_ja_possui_conta)
//        val spannable = SpannableString(text.text)
//        val start = spannable.indexOf("Faça login")
//        val end = start + "Faça login".length
//        spannable.setSpan(ForegroundColorSpan(Color.BLUE), start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannable.setSpan(
//            StyleSpan(Typeface.BOLD), start, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        text.text = spannable



    }
}