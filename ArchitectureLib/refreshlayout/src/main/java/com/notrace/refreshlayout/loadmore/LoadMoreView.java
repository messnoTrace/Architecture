package com.notrace.refreshlayout.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.notrace.refreshlayout.R;

/**
 * create by chenyang on 2019/4/4
 **/
public class LoadMoreView extends RelativeLayout implements LoadMoreRecyclerView.LoadMoreStateChangeListener{
    TextView footerTextView;
    ProgressBar footerProgressBar;
    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View footerView = LayoutInflater.from(context).inflate(R.layout.load_more_refresh_footer, this, false);
        footerTextView = (TextView) footerView.findViewById(R.id.pull_to_load_text);
        footerProgressBar = (ProgressBar) footerView.findViewById(R.id.pull_to_load_progress);
        addView(footerView);
    }



    @Override
    public void onStateChange(int statue) {
        switch (statue) {
            case STATE_ERROR:
                footerProgressBar.setVisibility(GONE);
                footerTextView.setText(R.string.load_more_error);
                break;
            case STATE_GONE:
                footerProgressBar.setVisibility(GONE);
                footerTextView.setText(R.string.load_more_success);
                break;
            case STATE_LOADING:
                footerProgressBar.setVisibility(VISIBLE);
                footerTextView.setText(R.string.load_more_loading);
                break;
            case STATE_THE_END:
                footerProgressBar.setVisibility(GONE);
                footerTextView.setText(R.string.load_more_no_more);
                break;
            case STATE_RESET:
                footerProgressBar.setVisibility(GONE);
                footerTextView.setText("");
                break;
        }
    }
}
