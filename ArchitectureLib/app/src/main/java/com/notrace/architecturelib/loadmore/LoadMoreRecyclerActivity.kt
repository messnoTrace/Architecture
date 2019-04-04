package com.notrace.architecturelib.loadmore

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.notrace.architecturelib.R
import com.notrace.architecturelib.databinding.ActivityLoadMoreRecyclerBinding

class LoadMoreRecyclerActivity : AppCompatActivity() {

    lateinit var databinding:ActivityLoadMoreRecyclerBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_load_more_recycler)
        databinding.setLifecycleOwner(this)
        databinding.apply {
            viewModel = ViewModelProviders.of(this@LoadMoreRecyclerActivity).get(LoadMoreViewModel::class.java).apply {

            }
        }

    }
}
