package com.notrace.network.rx

import com.notrace.network.util.convert
import io.reactivex.*
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription

/**
 *create by chenyang on 2019/4/2
 **/
abstract class CommonSingleObsever<T> : OnError, SingleObserver<T> {
    override fun onSubscribe(d: Disposable) {

    }

    override fun onError(e: Throwable) {
        e.convert().trans(this)
    }
}

abstract class CommonCompletableObserver : OnError, CompletableObserver {
    override fun onSubscribe(d: Disposable) {}

    override fun onComplete() {}

    final override fun onError(e: Throwable) {
        e.convert().trans(this)
    }
}

abstract class CommonFlowableSubscriber<T> : OnError, FlowableSubscriber<T> {
    override fun onSubscribe(s: Subscription) {}

    override fun onComplete() {}

    final override fun onError(e: Throwable) {
        e.convert().trans(this)
    }
}


abstract class CommonMaybeObserver<T> : OnError, MaybeObserver<T> {
    override fun onSubscribe(d: Disposable) {}

    override fun onComplete() {}

    final override fun onError(e: Throwable) {
        e.convert().trans(this)
    }
}

abstract class CommonObserver<T> : OnError, Observer<T> {
    override fun onSubscribe(d: Disposable) {}

    override fun onComplete() {}

    final override fun onError(e: Throwable) {
        e.convert().trans(this)
    }
}