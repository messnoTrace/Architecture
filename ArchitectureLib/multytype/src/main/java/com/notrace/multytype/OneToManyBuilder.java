package com.notrace.multytype;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import static com.notrace.multytype.Preconditions.checkNotNull;

/**
 * @author drakeet
 */
class OneToManyBuilder<T> implements OneToManyFlow<T>, OneToManyEndpoint<T> {

    private final @NonNull
    ItemBindingHolder holder;
    private final @NonNull Class<? extends T> clazz;
    private ItemViewBinder<T>[] binders;


    OneToManyBuilder(@NonNull ItemBindingHolder holder, @NonNull Class<? extends T> clazz) {
        this.clazz = clazz;
        this.holder = holder;
    }


    @Override @CheckResult
    @SafeVarargs
    public final @NonNull OneToManyEndpoint<T> to(@NonNull ItemViewBinder<T>... binders) {
        checkNotNull(binders);
        this.binders = binders;
        return this;
    }


    @Override
    public void withLinker(@NonNull Linker<T> linker) {
        checkNotNull(linker);
        doRegister(linker);
    }


    @Override
    public void withClassLinker(@NonNull ClassLinker<T> classLinker) {
        checkNotNull(classLinker);
        doRegister(ClassLinkerWrapper.wrap(classLinker, binders));
    }


    private void doRegister(@NonNull Linker<T> linker) {
        for (ItemViewBinder<T> binder : binders) {
            holder.register(clazz, binder, linker);
        }
    }
}
