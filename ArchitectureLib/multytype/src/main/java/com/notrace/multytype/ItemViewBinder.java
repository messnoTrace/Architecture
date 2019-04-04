/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.notrace.multytype;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import com.notrace.multytype.MultiTypeAdapter;

import java.util.List;

/***
 * @author drakeet
 */
public class ItemViewBinder<T> {

    /**
     * 不需要绑定的情况下使用此值作为variableId
     */
    public static final int VAR_NONE = 0;
    /* internal */ MultiTypeAdapter adapter;


    private int variableId;
    private @LayoutRes
    int layoutId;
    private SparseArray<Object> extraBindings;
    private int indexVariableId;

    public ItemViewBinder(int variableId, int layoutId) {
        this.variableId = variableId;
        this.layoutId = layoutId;
    }

    public int getVariableId() {
        return variableId;
    }

    public int getLayoutId() {
        return layoutId;
    }


    public final ItemViewBinder<T> bindExtra(int variableId, Object value) {
        if (extraBindings == null) {
            extraBindings = new SparseArray<>(1);
        }
        extraBindings.put(variableId, value);
        return this;
    }

    /**
     * 绑定布局文件中标识接收position值的变量id，接收者需要是Integer
     *
     * @param variableId
     * @return
     */
    public final ItemViewBinder<T> bindIndexId(int variableId) {
        indexVariableId = variableId;
        return this;
    }

    public final ItemViewBinder<T> clearExtras() {
        if (extraBindings != null) {
            extraBindings.clear();
        }
        return this;
    }

    public ItemViewBinder<T> removeExtra(int variableId) {
        if (extraBindings != null) {
            extraBindings.remove(variableId);
        }
        return this;
    }

    public final Object extraBinding(int variableId) {
        if (extraBindings == null) {
            return null;
        }
        return extraBindings.get(variableId);
    }

    /**
     * Get the adapter position of current item,
     * the internal position equals to {@link ViewHolder#getAdapterPosition()}.
     * <p><b>NOTE</b>: Below v2.3.5 we may provide getPosition() method to get the position,
     * It exists BUG, and sometimes can not get the correct position,
     * it is recommended to immediately stop using it and use the new
     * {@code getPosition(ViewHolder)} instead.</p>
     *
     * @param holder The ViewHolder to call holder.getAdapterPosition().
     * @return The adapter position.
     * @since v2.3.5. If below v2.3.5, use {@link ViewHolder#getAdapterPosition()} instead.
     */
    protected final int getPosition(@NonNull final ViewHolder holder) {
        return holder.getAdapterPosition();
    }


    /**
     * Get the {@link MultiTypeAdapter} for sending notifications or getting item count, etc.
     * <p>
     * Note that if you need to change the item's parent items, you could call this method
     * to get the {@link MultiTypeAdapter}, and call {@link MultiTypeAdapter#getItems()} to get
     * a list that can not be added any new item, so that you should copy the items and just use
     * {@link MultiTypeAdapter#setItems(List)} to replace the original items list and update the
     * views.
     * </p>
     *
     * @return The MultiTypeAdapter this item is currently associated with.
     * @since v2.3.4
     */
    protected final @NonNull
    MultiTypeAdapter getAdapter() {
        if (adapter == null) {
            throw new IllegalStateException("ItemViewBinder " + this + " not attached to MultiTypeAdapter. " +
                    "You should not call the method before registering the binder.");
        }
        return adapter;
    }

    protected void onViewRecycled(@NonNull BindingViewHolder holder) {
    }

    protected boolean onFailedToRecycleView(@NonNull BindingViewHolder holder) {
        return false;
    }

    protected void onViewAttachedToWindow(@NonNull BindingViewHolder holder) {
    }

    protected void onViewDetachedFromWindow(@NonNull BindingViewHolder holder) {
    }

    /**
     * Return the stable ID for the <code>item</code>. If {@link RecyclerView.Adapter#hasStableIds()}
     * would return false this method should return {@link RecyclerView#NO_ID}. The default
     * implementation of this method returns {@link RecyclerView#NO_ID}.
     *
     * @param item The item within the MultiTypeAdapter's items data set to query
     * @return the stable ID of the item
     * @see RecyclerView.Adapter#setHasStableIds(boolean)
     * @since v3.2.0
     */
    protected long getItemId(@NonNull T item) {
        return RecyclerView.NO_ID;
    }

    public boolean onBind(ViewDataBinding binding, T item, int position) {
        if (variableId == VAR_NONE) {
            return false;
        }
        boolean result = binding.setVariable(variableId, item);
        if (!result) {
            throw new MissingVariableException(binding, variableId, layoutId);
        }

        if (indexVariableId != VAR_NONE) {
            binding.setVariable(indexVariableId, position);
        }

        if (extraBindings != null) {
            for (int i = 0, size = extraBindings.size(); i < size; i++) {
                int variableId = extraBindings.keyAt(i);
                Object value = extraBindings.valueAt(i);
                if (variableId != VAR_NONE) {
                    binding.setVariable(variableId, value);
                }
            }
        }
        return true;
    }
}
