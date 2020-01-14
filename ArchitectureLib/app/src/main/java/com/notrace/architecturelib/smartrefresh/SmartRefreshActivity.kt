package com.notrace.architecturelib.smartrefresh

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.notrace.architecturelib.R
import com.notrace.architecturelib.databinding.ActivitySmartRefreshBinding
import com.notrace.network.API_CODE_EMPTY
import com.notrace.network.exception.ApiException
import com.notrace.network.mvvm.base.Status

class SmartRefreshActivity : AppCompatActivity() {


    lateinit var databinding: ActivitySmartRefreshBinding
    val viewModel by lazy {
        ViewModelProviders.of(this@SmartRefreshActivity).get(SmartRfreshViewModel::class.java).apply {

            networkState().observe(this@SmartRefreshActivity, Observer {
                if (it?.status == Status.FAILED && it.throwable is ApiException) {
                    val exception = it.throwable as ApiException
                    if (exception.serverCode == API_CODE_EMPTY) {
                        return@Observer
                    }
                    exception.message?.let {
                        Toast.makeText(this@SmartRefreshActivity, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databinding = DataBindingUtil.setContentView(this, R.layout.activity_smart_refresh)
        databinding.setLifecycleOwner(this)

        databinding.viewModel = viewModel
        viewModel.toastMessage.observe(this, Observer {
            Toast.makeText(this@SmartRefreshActivity, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }
}
