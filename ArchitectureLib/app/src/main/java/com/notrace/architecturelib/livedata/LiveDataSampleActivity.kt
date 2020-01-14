package com.notrace.architecturelib.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drakeet.multitype.MultiTypeAdapter
import com.notrace.architecturelib.R
import com.notrace.architecturelib.databinding.ActivityLiveDataSampleBinding
import com.notrace.network.mvvm.base.Status

class LiveDataSampleActivity : AppCompatActivity() {

    lateinit var dataBinding: ActivityLiveDataSampleBinding
    var adapter = MultiTypeAdapter()
    val viewModel by lazy {
        ViewModelProviders.of(this).get(LiveDataSampleViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_live_data_sample)
        dataBinding.viewModel = viewModel
        dataBinding.lifecycleOwner = this

        adapter.register(SampleBinder())
        adapter.items = viewModel.items
        dataBinding.rcvLivedata.adapter = adapter


        viewModel.repoSource.observe(this, Observer {
            when (it.status) {
                Status.FAILED -> {
                }
                Status.RUNNING -> {
                }
                Status.SUCCESS -> {
                    viewModel.items.addAll(it.data!!.items)
                    adapter.notifyDataSetChanged()
                }
            }
        })


        viewModel.requestData.postValue(true)

    }
}
