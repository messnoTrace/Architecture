package com.notrace.refreshlayout.loadmore;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

/**
 * create by chenyang on 2019/4/4
 **/
public class LoadMoreAdapter extends RecyclerView.Adapter {
    private static final int TYPE_LOAD_MORE = Integer.MAX_VALUE;
    private View bottomCustomView;
    private View emptyView;

    private RecyclerView.Adapter realAdapter;
    protected LoadMoreAdapter(RecyclerView.Adapter realAdapter, View loadMoreView, View emptyView) {
        this.realAdapter = realAdapter;
        if (emptyView != null) {
            LinearLayout linearLayout = new LinearLayout(loadMoreView.getContext());
            linearLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(loadMoreView);
            linearLayout.addView(emptyView, 0);
            emptyView.setVisibility(View.GONE);
            this.emptyView = emptyView;
            this.bottomCustomView = linearLayout;
        } else {
            this.bottomCustomView = loadMoreView;
        }
        realAdapter.registerAdapterDataObserver(observer);
    }

    /**
     * 需要在设置adapter之前设置LayoutManager,此时才能正确处理
     * SpanSizeLookup
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup old = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    RecyclerView.Adapter wrapperAdapter = recyclerView.getAdapter();
                    if (isFullSpanType(wrapperAdapter.getItemViewType(position))) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return old.getSpanSize(position);
                }
            });
        }
        realAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        int type = getItemViewType(position);
        if (isFullSpanType(type)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
        if (!isLocalHolder(holder))
            realAdapter.onViewDetachedFromWindow(holder);
    }

    public void resetEmptyViewGone() {
        observer.resetEmptyView();
    }

    private AdapterDataObserver observer = new AdapterDataObserver();

    private class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
        boolean emptyViewVisible = false;
        @Override
        public void onChanged() {
            LoadMoreAdapter.this.notifyDataSetChanged();
            if (emptyView == null)
                return;
            if (realAdapter == null || realAdapter.getItemCount() == 0) {
                if (!emptyViewVisible && emptyView != null) {
                    emptyView.setVisibility(View.VISIBLE);
                    View parent = (View) emptyView.getParent();
                    parent.getLayoutParams().height = RecyclerView.LayoutParams.MATCH_PARENT;
                    emptyViewVisible = true;
                }
            } else {
                if (emptyViewVisible && emptyView != null) {
                    resetEmptyView();
                }

            }
        }

        public void resetEmptyView() {
            if (emptyView == null) return;
            emptyView.setVisibility(View.GONE);
            View parent = (View) emptyView.getParent();
            parent.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
            emptyViewVisible = false;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            LoadMoreAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            LoadMoreAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            LoadMoreAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            LoadMoreAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            LoadMoreAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    private boolean isFullSpanType(int type) {
        return type == TYPE_LOAD_MORE ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOAD_MORE) {
            return new LoadMoreViewHolder(bottomCustomView);
        }
        return realAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < realAdapter.getItemCount()) {
            realAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (position < realAdapter.getItemCount()) {
            realAdapter.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public final int getItemCount() {
        return realAdapter.getItemCount()+1;
    }

    @Override
    public final long getItemId(int position) {
        if (position < realAdapter.getItemCount()) {
            return realAdapter.getItemId(position);
        }
        return super.getItemId(position);
    }

    @Override
    public final int getItemViewType(int position) {
        if (position == realAdapter.getItemCount()) {
            return TYPE_LOAD_MORE;
        } else {
            return realAdapter.getItemViewType(position);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (!isLocalHolder(holder))
            realAdapter.onViewRecycled(holder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        if (!isLocalHolder(holder))
            return realAdapter.onFailedToRecycleView(holder);
        return super.onFailedToRecycleView(holder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (!isLocalHolder(holder))
            realAdapter.onViewDetachedFromWindow(holder);
    }

    private boolean isLocalHolder(RecyclerView.ViewHolder holder) {
        return holder instanceof LoadMoreViewHolder;
    }

    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        View view;
        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }
}
