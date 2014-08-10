package com.dupls.cardslider;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

public class GridSliderView extends GridView {

    private View mSwipeView;
    private Bitmap mFloatBitmap;
    private ImageView mImageView;
    private int mMovementX;
    private int mMovementY;
    private int mStartX;
    private int mStartY;
    private boolean mSwiping;
    private boolean mScrolling;

    public GridSliderView(Context context) {
        super(context);
    }

    public GridSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GridSliderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch(ev.getAction()) {
//        }
//
//        return super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) ev.getX();
                mStartY = (int) ev.getY();
                mSwipeView = getSwipeable(mStartX, mStartY);
                if(mSwipeView != null) {
                    mSwipeView.setElevation(3);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mMovementX = (int)ev.getX() - mStartX;
                mMovementY = (int)ev.getY() - mStartY;

                if(!mScrolling && !mSwiping) {
                    mScrolling = Math.abs(mMovementY) > Math.abs(mMovementX) * 0.5f;
                    mSwiping = !mScrolling;
                }

                if(mSwiping && mSwipeView != null) {
                    swipe(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                mSwiping = false;
                mScrolling = false;
                if(mSwipeView != null) {
                    up(ev);
                }
                break;
        }

        if(mSwiping) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    private void swipe(MotionEvent ev) {
        mSwipeView.setTranslationX(ev.getRawX() - mStartX);

        float alpha = 1;
        float x = mSwipeView.getTranslationX();
        if(x < 0) {
            alpha = 1 - (-x / mSwipeView.getWidth());
        } else if(x > 0) {
            alpha = 1 - (x / mSwipeView.getWidth());
        }

        mSwipeView.setAlpha(alpha);
    }

    private void up(MotionEvent ev) {
        mSwipeView.animate().setDuration(200).alpha(1).translationX(0);
        mSwipeView.setElevation(0);
        mSwipeView = null;
    }

    protected View getSwipeable(int x, int y) {
        int position = pointToPosition(x, y);
        if(position != INVALID_POSITION) {
            return getChildAt(position - getFirstVisiblePosition());
        }
        return null;
    }

    public View createFloatView(View v) {
        // Create a copy of the drawing cache so that it does not get
        // recycled by the framework when the list tries to clean up memory
        //v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        v.setDrawingCacheEnabled(true);
        mFloatBitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        if (mImageView == null) {
            mImageView = new ImageView(getContext());
        }
        mImageView.setPadding(0, 0, 0, 0);
        mImageView.setImageBitmap(mFloatBitmap);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(v.getWidth(), v.getHeight()));

        return mImageView;
    }

}
