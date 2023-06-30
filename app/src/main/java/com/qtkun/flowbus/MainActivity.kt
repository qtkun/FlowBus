package com.qtkun.flowbus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qtk.flowbus.observe.observeEvent
import com.qtk.flowbus.post.postEvent
import com.qtkun.flowbus.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeEvent<String>("Jump") {
            binding.tvText.text = it
        }

        binding.btnJump.setOnClickListener {
            postEvent("Back", generateRandomString(20))
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}