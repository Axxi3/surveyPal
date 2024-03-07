package com.example.surveypal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            var bottombar:SmoothBottomBar=findViewById(R.id.smoothBottomBar)

        bottombar.setOnItemSelectedListener {
            when(it){
                0->{
                    findNavController(R.id.fragmentContainerView2).navigate(R.id.action_accountFragment_to_homePage)
                }
                1->{
                    findNavController(R.id.fragmentContainerView2).navigate(R.id.action_homePage_to_accountFragment)
                }
            }
        }
    }
}