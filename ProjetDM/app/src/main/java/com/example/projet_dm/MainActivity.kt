package com.example.projet_dm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projet_dm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}