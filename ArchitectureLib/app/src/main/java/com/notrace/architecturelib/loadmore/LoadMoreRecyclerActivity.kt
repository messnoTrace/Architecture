package com.notrace.architecturelib.loadmore

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.notrace.architecturelib.R
import com.notrace.architecturelib.databinding.ActivityLoadMoreRecyclerBinding
import com.notrace.network.API_CODE_EMPTY
import com.notrace.network.exception.ApiException
import com.notrace.network.mvvm.Status

class LoadMoreRecyclerActivity : AppCompatActivity() {

    lateinit var databinding: ActivityLoadMoreRecyclerBinding
    val viewModel by lazy {
        ViewModelProviders.of(this@LoadMoreRecyclerActivity).get(LoadMoreViewModel::class.java).apply {

            networkState().observe(this@LoadMoreRecyclerActivity, Observer {
                if (it?.status == Status.FAILED && it.throwable is ApiException) {
                    val exception = it.throwable as ApiException
                    if (exception.serverCode == API_CODE_EMPTY) {
                        return@Observer
                    }
                    exception.message?.let {
                        Toast.makeText(this@LoadMoreRecyclerActivity, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_load_more_recycler)
        databinding.setLifecycleOwner(this)

        databinding.viewModel = viewModel
        viewModel.toastMessage.observe(this, Observer {
            Toast.makeText(this@LoadMoreRecyclerActivity, it.toString(), Toast.LENGTH_SHORT).show()
        })

    }
}
