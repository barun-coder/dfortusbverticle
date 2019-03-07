package com.displayfort.dfortusb.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.universalvideoview.UniversalVideoView;

public class UniversalFullScreenVideoView extends UniversalVideoView {
    public UniversalFullScreenVideoView(Context context) {
        super(context);
    }

    public UniversalFullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UniversalFullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
}