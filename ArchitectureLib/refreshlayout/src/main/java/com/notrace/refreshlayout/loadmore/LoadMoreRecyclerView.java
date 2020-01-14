package com.notrace.refreshlayout.loadmore;

/**
 * create by chenyang on 2019/4/4
 **/
import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.notrace.refreshlayout.R;


/**
 * 加载更多的RecyclerView
 * @author like
 * @date 16/8/11
 */
public class LoadMoreRecyclerView extends RecyclerView {
    private View loadMoreView;
    private View emptyView;
    private boolean allDataLoaded;
    private boolean loading;
    private LoadMoreListener loadMoreListener;
    private Adapter realAdapter;
    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadMoreView = new LoadMoreView(context);
        setItemAnimator(null);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadMoreRecyclerView, defStyle, 0);
        boolean disableEmpty = a.getBoolean(R.styleable.LoadMoreRecyclerView_load_more_empty_show_disable, false);
        if (!disableEmpty) {
            @LayoutRes int emptyLayout = a.getResourceId(R.styleable.LoadMoreRecyclerView_load_more_empty_layout, -1);
            @StringRes int tipStringId = a.getResourceId(R.styleable.LoadMoreRecyclerView_load_more_empty_tip_text, -1);
            boolean tipWithImg = a.getBoolean(R.styleable.LoadMoreRecyclerView_load_more_empty_tip_with_img, false);
            if (emptyLayout != -1) {
                emptyView = LayoutInflater.from(context).inflate(emptyLayout, null, false);
            } else {
                emptyView = LayoutInflater.from(context).inflate(R.layout.load_more_tip_layout_empty, null, false);
                emptyView.findViewById(R.id.iv_tip_image).setVisibility(tipWithImg ? VISIBLE : GONE);
                if (tipStringId != -1) {
                    ((TextView)emptyView.findViewById(R.id.tv_tip_message)).setText(tipStringId);
                }
            }
        }

        a.recycle();

        addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();


                boolean triggerCondition = !loading
                        && dy > 0
                        && !allDataLoaded
                        && (getAdapter() != null && getAdapter().getItemCount() > 1)
                        && visibleItemCount > 0
                        && firstItemNotVisible(recyclerView)
                        && canTriggerLoadMore(recyclerView);
                if (triggerCondition) {
                    if (loadMoreListener != null) {
                        loadMoreListener.onLoadMore(recyclerView);
                    }
                    loading = true;
                    listener.onStateChange(LoadMoreStateChangeListener.STATE_LOADING);
                    // 停止fling
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.stopScroll();
                        }
                    }, 200);

                }
            }


            public boolean firstItemNotVisible(RecyclerView recyclerView) {
                LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    return ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition() != 0;
                }
                return true;
            }

            public boolean canTriggerLoadMore(RecyclerView recyclerView) {
                View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                int position = recyclerView.getChildLayoutPosition(lastChild);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                return totalItemCount - 1 == position;
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        realAdapter = adapter;
        if (emptyView != null) {
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        super.setAdapter(new LoadMoreAdapter(adapter, loadMoreView, emptyView));
    }

    /**
     * 获取真实设置数据的的adapter
     * @return
     */
    public Adapter getRealAdapter() {
        return realAdapter;
    }

    public void setCustomLoadMoreView(View customLoadMoreView){
        loadMoreView = customLoadMoreView;
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void loadMoreSucces() {
        listener.onStateChange(LoadMoreStateChangeListener.STATE_GONE);
    }

    public void loadMoreFail() {
        listener.onStateChange(LoadMoreStateChangeListener.STATE_ERROR);
        if (realAdapter != null)
            realAdapter.notifyDataSetChanged();
    }

    public void noMoreData() {
        allDataLoaded = true;
        listener.onStateChange(LoadMoreStateChangeListener.STATE_THE_END);
        if (realAdapter != null)
            realAdapter.notifyDataSetChanged();
    }

    public void resetState() {
        allDataLoaded = false;
        Adapter adapter = getAdapter();
        if (adapter != null && adapter instanceof LoadMoreAdapter) {
            ((LoadMoreAdapter)adapter).resetEmptyViewGone();
        }
        listener.onStateChange(LoadMoreStateChangeListener.STATE_RESET);
    }

    LoadMoreStateChangeListener listener = new LoadMoreStateChangeListener() {
        @Override
        public void onStateChange(int statue) {
            if (statue != STATE_LOADING) {
                loading = false;
            }
            if (loadMoreView != null && loadMoreView instanceof LoadMoreStateChangeListener) {
                LoadMoreStateChangeListener loadMoreListener = (LoadMoreStateChangeListener) loadMoreView;
                loadMoreListener.onStateChange(statue);
            }
        }
    };

    public interface LoadMoreStateChangeListener {
        int STATE_GONE = 1;
        int STATE_LOADING = 2;
        int STATE_ERROR = 3;
        int STATE_THE_END = 4;
        int STATE_RESET = 5;
        void onStateChange(int statue);
    }

    public interface LoadMoreListener {
        void onLoadMore(RecyclerView recyclerView);
    }
}
