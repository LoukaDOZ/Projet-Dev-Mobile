package com.example.projet_dm

import android.os.Bundle
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity
import com.example.projet_dm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Remove default action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}