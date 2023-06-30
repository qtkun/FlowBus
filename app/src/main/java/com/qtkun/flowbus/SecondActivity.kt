package com.qtkun.flowbus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qtk.flowbus.observe.observeEvent
import com.qtk.flowbus.post.postEvent
import com.qtkun.flowbus.databinding.ActivitySecondBinding

class SecondActivity: AppCompatActivity() {
    private val binding by lazy { ActivitySecondBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeEvent<String>("Back", true) {
            binding.tvText.text = it
        }

        binding.btnBack.setOnClickListener {
            postEvent("Jump", generateRandomString(20))
            finish()
        }
    }
}