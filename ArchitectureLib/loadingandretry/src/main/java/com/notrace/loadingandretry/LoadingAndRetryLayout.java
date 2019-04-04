package com.notrace.loadingandretry;

/**
 * create by chenyang on 2019/4/4
 **/

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 加载与重试的布局文件
 */
public class LoadingAndRetryLayout extends FrameLayout {
    private View mLoadingView;
    private View mRetryView;
    private View mContentView;
    private View mEmptyView;
    private LayoutInflater mInflater;
    private LoadingAndRetryManager manager;

    private static final String TAG = LoadingAndRetryLayout.class.getSimpleName();

    public LoadingAndRetryLayout(@NonNull Context context) {
        this(context, null);
    }

    public LoadingAndRetryLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingAndRetryLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void showLoading() {
        if (isMainThread()) {
            showView(mLoadingView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mLoadingView);
                }
            });
        }
    }

    public void showRetry() {
        if (isMainThread()) {
            showView(mRetryView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mRetryView);
                }
            });
        }

    }

    public void showContent() {
        if (isMainThread()) {
            showView(mContentView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mContentView);
                }
            });
        }
    }

    public void showEmpty() {
        if (isMainThread()) {
            showView(mEmptyView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mEmptyView);
                }
            });
        }
    }


    private void showView(View view) {
        if (view == null) {
            return;
        }

        if (view == mLoadingView) {
            mLoadingView.setVisibility(View.VISIBLE);
            if (mRetryView != null) {
                mRetryView.setVisibility(View.GONE);
            }
            if (mContentView != null) {
                mContentView.setVisibility(View.GONE);
            }
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.GONE);
            }

        } else if (view == mRetryView) {
            mRetryView.setVisibility(View.VISIBLE);
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.GONE);
            }

            if (mContentView != null) {
                mContentView.setVisibility(View.GONE);
            }

            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.GONE);
            }

        } else if (view == mContentView) {
            mContentView.setVisibility(View.VISIBLE);
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.GONE);
            }
            if (mRetryView != null) {
                mRetryView.setVisibility(View.GONE);
            }
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.GONE);
            }
        } else if (view == mEmptyView) {
            mEmptyView.setVisibility(View.VISIBLE);
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.GONE);
            }
            if (mRetryView != null) {
                mRetryView.setVisibility(View.GONE);
            }
            if (mContentView != null) {
                mContentView.setVisibility(View.GONE);
            }
        }


    }

    public View setContentView(int layoutId) {
        return setContentView(mInflater.inflate(layoutId, this, false));
    }

    public View setLoadingView(int layoutId) {
        return setLoadingView(mInflater.inflate(layoutId, this, false));
    }

    public View setEmptyView(int layoutId) {
        return setEmptyView(mInflater.inflate(layoutId, this, false));
    }

    public View setRetryView(int layoutId) {
        return setRetryView(mInflater.inflate(layoutId, this, false));
    }

    public View setLoadingView(View view) {
        View loadingView = mLoadingView;
        if (loadingView != null) {
            Log.w(TAG, "you have already set a loading view and would be instead of this new one.");
        }
        removeView(loadingView);
        addView(view);
        mLoadingView = view;
        return mLoadingView;
    }

    public View setEmptyView(View view) {
        View emptyView = mEmptyView;
        if (emptyView != null) {
            Log.w(TAG, "you have already set a empty view and would be instead of this new one.");
        }
        removeView(emptyView);
        addView(view);
        mEmptyView = view;
        return mEmptyView;
    }

    public View setRetryView(View view) {
        View retryView = mRetryView;
        if (retryView != null) {
            Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
        }
        removeView(retryView);
        addView(view);
        mRetryView = view;
        return mRetryView;

    }

    public View setContentView(View view) {
        View contentView = mContentView;
        if (contentView != null) {
            Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
        }
        removeView(contentView);
        addView(view);
        mContentView = view;
        return mContentView;
    }

    public View getRetryView() {
        return mRetryView;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getContentView() {
        return mContentView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public LoadingAndRetryManager getManager() {
        return manager;
    }

    public void setManager(LoadingAndRetryManager manager) {
        this.manager = manager;
    }
}
