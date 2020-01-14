package com.notrace.architecturelib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.notrace.architecturelib.livedata.LiveDataSampleActivity
import com.notrace.architecturelib.loadmore.LoadMoreRecyclerActivity
import com.notrace.architecturelib.smartrefresh.SmartRefreshActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_loadmore.setOnClickListener {

            startActivity(Intent(this, LoadMoreRecyclerActivity::class.java))
        }

        btn_smartrefresh.setOnClickListener {
            startActivity(Intent(this, SmartRefreshActivity::class.java))
        }
        btn_livedata.setOnClickListener {
            startActivity(Intent(this, LiveDataSampleActivity::class.java))
        }
    }
}
