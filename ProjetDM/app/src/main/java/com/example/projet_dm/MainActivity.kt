package com.example.projet_dm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projet_dm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}