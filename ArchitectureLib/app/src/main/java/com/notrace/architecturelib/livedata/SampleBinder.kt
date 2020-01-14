package com.notrace.architecturelib.livedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.drakeet.multitype.ItemViewBinder
import com.notrace.architecturelib.R
import com.notrace.architecturelib.data.model.Repo
import com.notrace.architecturelib.databinding.ItemRepoBinding
import com.notrace.support.multitype.BindingViewHolder

class SampleBinder(): ItemViewBinder<Repo, BindingViewHolder>() {
    override fun onBindViewHolder(holder: BindingViewHolder, item: Repo) {
        var itemRecommendVideoBinding = (holder.binding as ItemRepoBinding)
        itemRecommendVideoBinding.item = item
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder {

        var binding = DataBindingUtil.inflate<ItemRepoBinding>(
            inflater,
            R.layout.item_repo,
            parent, false
        )

        return BindingViewHolder(binding)
    }
}