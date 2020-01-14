package com.notrace.support.multitype

import android.util.Log
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.*

class ItemBindingHolder{
    private val TAG = "ItemBindingHolder"
    @NonNull
    lateinit var types:Types

    constructor() {
        types = MutableTypes()
    }

    constructor(@NonNull clazz: Class<*>, @NonNull binder: CommonItemViewBinder<Any>):super(){
        register(clazz, binder)
    }

    /**
     * Registers a type class and its item view binder. If you have registered the class,
     * it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter
     * again.
     *
     *
     * @param clazz the class of a item
     * @param binder the item view binder
     * @param <T> the item data type
    </T> */
    fun <T> register(@NonNull clazz: Class<out T>, @NonNull binder: CommonItemViewBinder<T>) {
        checkNotNull(clazz)
        checkNotNull(binder)
        checkAndRemoveAllTypesIfNeeded(clazz)
        register(clazz, binder, CommonLinker<T>())
    }


    internal fun <T> register(
        @NonNull clazz: Class<out T>,
        @NonNull binder: CommonItemViewBinder<T>,
        @NonNull linker: Linker<T>) {
        types.register(Type<T>(clazz, binder, linker))
    }


    /**
     * Registers a type class to multiple item view binders. If you have registered the
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter
     * again.
     *
     *
     * @param clazz the class of a item
     * @param <T> the item data type
     * @return [OneToManyFlow] for setting the binders
     * @see .register
    </T> */
//    @CheckResult
//    @NonNull
//    fun <T> register(@NonNull clazz: Class<out T>): OneToManyFlow<T> {
//        checkNotNull(clazz)
//        checkAndRemoveAllTypesIfNeeded(clazz)
//        return OneToManyBuilder(this.getada, clazz)
//    }


    private fun checkAndRemoveAllTypesIfNeeded(@NonNull clazz: Class<*>) {
        if (types.unregister(clazz)) {
            Log.w(TAG, "You have registered the " + clazz.simpleName + " type. " +
                    "It will override the original binder(s).")
        }
    }

    @NonNull
    private fun getAllTypes(): Types {
        return types
    }
}