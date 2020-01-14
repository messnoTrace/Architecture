package com.notrace.support.multitype

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import com.drakeet.multitype.ItemViewBinder

class CommonItemViewBinder<T> : ItemViewBinder<T, BindingViewHolder> {


    /**
     * 不需要绑定的情况下使用此值作为variableId
     */
    val VAR_NONE = 0
    @NonNull
    var variableId: Int
    @LayoutRes
    var layoutId: Int

    private var extraBindings: SparseArray<Any>? = SparseArray()
    private var indexVariableId: Int = 0

    constructor(variableId: Int, layoutId: Int) {
        this.variableId = variableId
        this.layoutId = layoutId
    }

    override fun onBindViewHolder(holder: BindingViewHolder, item: T) {
        onBind(holder.binding, item, getPosition(holder))
        holder.binding.executePendingBindings()

    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingViewHolder {
        return BindingViewHolder(DataBindingUtil.inflate(inflater, layoutId, parent, false))
    }

    fun onBind(binding: ViewDataBinding, item: T, position: Int): Boolean {
        if (variableId == VAR_NONE) {
            return false
        }
        val result = binding.setVariable(variableId, item)
        if (!result) {
            throw MissingVariableException(binding, variableId, layoutId)
        }

        if (indexVariableId != VAR_NONE) {
            binding.setVariable(indexVariableId, position)
        }

        if (extraBindings != null) {
            val size = extraBindings!!.size()

            for (i in 0 until size) {
                val variableId = extraBindings!!.keyAt(i)
                val value = extraBindings!!.valueAt(i)
                if (variableId != VAR_NONE) {
                    binding.setVariable(variableId, value)
                }
            }
        }
        return true
    }

     fun registerExtraBindings(variableId:Int,value:Any){
         extraBindings!!.put(variableId,value)
     }
}