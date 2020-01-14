package com.notrace.support.multitype

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * 给multytype添加databinding
 *
 */
class BindingViewHolder : RecyclerView.ViewHolder {
    lateinit var binding: ViewDataBinding

    constructor(binding: ViewDataBinding) : super(binding.root) {
        this.binding = binding
    }



}