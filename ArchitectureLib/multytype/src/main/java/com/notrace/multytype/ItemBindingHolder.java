package com.notrace.multytype;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import static com.notrace.multytype.Preconditions.checkNotNull;

/**
 * create by chenyang on 2019/4/3
 **/
public class ItemBindingHolder {

    private String TAG = "ItemBindingHolder";

    private @NonNull
    TypePool typePool;

    public ItemBindingHolder() {
        typePool = new MultiTypePool();
    }

    public ItemBindingHolder(@NonNull Class clazz, @NonNull ItemViewBinder binder) {
        super();
        register(clazz, binder);
    }

    /**
     * Registers a type class and its item view binder. If you have registered the class,
     * it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param clazz the class of a item
     * @param binder the item view binder
     * @param <T> the item data type
     */
    public <T> void register(@NonNull Class<? extends T> clazz, @NonNull ItemViewBinder<T> binder) {
        checkNotNull(clazz);
        checkNotNull(binder);
        checkAndRemoveAllTypesIfNeeded(clazz);
        register(clazz, binder, new DefaultLinker<T>());
    }


    <T> void register(
            @NonNull Class<? extends T> clazz,
            @NonNull ItemViewBinder<T> binder,
            @NonNull Linker<T> linker) {
        typePool.register(clazz, binder, linker);
    }


    /**
     * Registers a type class to multiple item view binders. If you have registered the
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param clazz the class of a item
     * @param <T> the item data type
     * @return {@link OneToManyFlow} for setting the binders
     * @see #register(Class, ItemViewBinder)
     */
    @CheckResult
    public @NonNull <T> OneToManyFlow<T> register(@NonNull Class<? extends T> clazz) {
        checkNotNull(clazz);
        checkAndRemoveAllTypesIfNeeded(clazz);
        return new OneToManyBuilder<>(this, clazz);
    }


    private void checkAndRemoveAllTypesIfNeeded(@NonNull Class<?> clazz) {
        if (typePool.unregister(clazz)) {
            Log.w(TAG, "You have registered the " + clazz.getSimpleName() + " type. " +
                    "It will override the original binder(s).");
        }
    }

    @NonNull
    public TypePool getTypePool() {
        return typePool;
    }
}
