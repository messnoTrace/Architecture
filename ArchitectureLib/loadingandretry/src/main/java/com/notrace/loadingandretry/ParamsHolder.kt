package com.notrace.loadingandretry

import android.widget.ImageView
import android.widget.TextView

/**
 *create by chenyang on 2019/4/4
 *
 * 空布局UI 默认图片+文字+按钮
 **/
internal class ParamsHolder {
    var emptyText: TextView?=null
    var emptyImage: ImageView?=null
    var retryInvoke: Function0<Any>? = null
}
