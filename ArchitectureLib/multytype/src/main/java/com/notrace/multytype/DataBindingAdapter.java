package com.notrace.multytype;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * create by chenyang on 2019/4/3
 **/
public class DataBindingAdapter {

    @BindingAdapter(value = {"bindingHolder", "items"})
    public static <T> void setItems(final RecyclerView recyclerView, ItemBindingHolder bindingHolder, List<T> items) {
        if (bindingHolder == null) {
            throw new IllegalArgumentException("itemBinding must not be null");
        }
        MultiTypeAdapter adapter;
        MultiTypeAdapter oldAdapter = (MultiTypeAdapter) recyclerView.getAdapter();
        if (oldAdapter == null) {
            adapter = new MultiTypeAdapter();
        } else {
            adapter = oldAdapter;
        }

        if (items == null) {
            items = new ArrayList<>();
        }

        adapter.registerAll(bindingHolder.getTypePool());

        if (oldAdapter != adapter) {
            recyclerView.setAdapter(adapter);
        }

        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }
    @BindingAdapter(value = {"itemDecoration"})
    public static void addItemDecoration(RecyclerView recyclerView,RecyclerView.ItemDecoration itemDecoration){
        recyclerView.addItemDecoration(itemDecoration);
    }
}
