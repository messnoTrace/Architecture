package com.notrace.refreshlayout.loadingandretry

import androidx.databinding.BindingAdapter
import androidx.annotation.IntDef
import androidx.core.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
import com.notrace.loadingandretry.R

/**
 *create by chenyang on 2019/4/4
 **/

const val STATE_LOADING = 1
const val STATE_RETRY = 2
const val STATE_EMPTY = 3
const val STATE_CONTENT = 4

@BindingAdapter(value = ["retryCallback"], requireAll = false)
fun configEmptyAndRetry(view: View, callback: Function0<Any>) {
    // 掉用一次，以保障ParamsHolder被初始化
    getManager(view)
    getParamsHolder(view)?.retryInvoke = callback
}

@BindingAdapter(value = ["emptyText"])
fun configEmptyText(view: View, text: String) {
    // 掉用一次，以保障ParamsHolder被初始化
    getManager(view)
    getParamsHolder(view)?.run {
        emptyText?.text = text
    }
}


@BindingAdapter(value = ["emptyImageVisible"])
fun configImageImage(view: View, visible: Boolean) {
    // 掉用一次，以保障ParamsHolder被初始化
    getManager(view)
    getParamsHolder(view)?.run {
        emptyImage?.visibility = if (visible) View.VISIBLE else View.GONE
    }
}

@BindingAdapter(value = ["emptyState", "retryState"])
fun emptyState(view: View, empty: Boolean, retry: Boolean) {
    getManager(view).run {
        when {
            retry -> showRetry()
            empty -> showEmpty()
            else -> showContent()
        }
    }
}

private fun getParamsHolder(view: View): com.notrace.loadingandretry.ParamsHolder? {
    val obj = view.getTag(R.id.support_empty_and_retry_holder)
    return obj as? com.notrace.loadingandretry.ParamsHolder
}

/**
 * 不进行loading的处理
 * @param view
 * @return
 */
private fun getManager(view: View): com.notrace.loadingandretry.LoadingAndRetryManager {
    val manager: com.notrace.loadingandretry.LoadingAndRetryManager
    val parent = view.parent
    if (parent is com.notrace.loadingandretry.LoadingAndRetryLayout) {
        manager = parent.manager
    } else {
        val holder = com.notrace.loadingandretry.ParamsHolder()
        view.setTag(R.id.support_empty_and_retry_holder, holder)
        manager = com.notrace.loadingandretry.LoadingAndRetryManager.generate(
            view,
            object : com.notrace.loadingandretry.OnLoadingAndRetryListener() {
                override fun setRetryEvent(retryView: View) {
                    if (retryView is NestedScrollView) {
                        (retryView as ViewGroup).getChildAt(0).setOnClickListener { holder.retryInvoke?.invoke() }
                    } else {
                        retryView.setOnClickListener {
                            holder.retryInvoke?.invoke()
                        }
                    }
                }

                override fun setEmptyEvent(emptyView: View?) {
                    holder.emptyText = emptyView!!.findViewById(R.id.tv_tip_text)
                    holder.emptyImage = emptyView.findViewById(R.id.iv_tip_image)
                }

                override fun generateRetryLayoutId(): Int {
                    return R.layout.layout_refresh_retry
                }

                override fun generateEmptyLayoutId(): Int {
                    return R.layout.layout_refresh_empty
                }
            })
    }
    return manager
}

@IntDef(STATE_LOADING, STATE_EMPTY, STATE_RETRY, STATE_CONTENT)
@Retention(AnnotationRetention.SOURCE)
internal annotation class StateScope