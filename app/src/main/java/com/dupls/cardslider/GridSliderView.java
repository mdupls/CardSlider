package com.dupls.cardslider;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AbsListView;
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
    private boolean mCanSwipe;
    private boolean mHasScrolledBeforeLiftingUp;
    private boolean mScrolling;

    private VelocityTracker mVelocityTracker;
    private OnScrollListener mWrappedOnScrollListener;

    public GridSliderView(Context context) {
        super(context);

        init();
    }

    public GridSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public GridSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public GridSliderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        setOnScrollListener(mOnScrollListener);
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView listView, int state) {
            mScrolling = state != OnScrollListener.SCROLL_STATE_IDLE;
            if (mScrolling) {
                mHasScrolledBeforeLiftingUp = true;
            }

            if (mWrappedOnScrollListener != null) {
                mWrappedOnScrollListener.onScrollStateChanged(listView, state);
            }
        }

        @Override
        public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mWrappedOnScrollListener != null) {
                mWrappedOnScrollListener.onScroll(listView, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Always intercept the touch events so that we can recognize horizontal swipes.
        return true;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) ev.getX();
                mStartY = (int) ev.getY();
                mSwipeView = getSwipeable(mStartX, mStartY);
                if (mSwipeView != null) {
                    mSwipeView.setElevation(3);

                    mHasScrolledBeforeLiftingUp = false;
                    mCanSwipe = false;

                    mMovementX = 0;
                    mMovementY = 0;

                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSwipeView != null) {
                    if (!mScrolling && !mHasScrolledBeforeLiftingUp) {
                        mMovementX = (int) ev.getX() - mStartX;
                        mMovementY = (int) ev.getY() - mStartY;

                        if (!mCanSwipe) {
                            mCanSwipe = Math.abs(mMovementY) < Math.abs(mMovementX) * 0.75f;
                        }

                        if (mCanSwipe) {
                            swipe(ev);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                if (mSwipeView != null) {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float velocity = mVelocityTracker.getXVelocity();

                    boolean delete = false;
                    if(mCanSwipe) {
                        if (Math.abs(velocity) > 200) {
                            delete(velocity > 0 ? 1 : -1);
                            delete = true;
                        }
                    }

                    if(!delete) {
                        up(ev);
                    }

                    mVelocityTracker.recycle();
                    mVelocityTracker = null;

                    mHasScrolledBeforeLiftingUp = false;
                    mCanSwipe = false;
                }
                break;
        }

        if (mCanSwipe) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    private void swipe(MotionEvent ev) {
        mSwipeView.setTranslationX(ev.getRawX() - mStartX);

        float alpha = 1;
        float x = mSwipeView.getTranslationX();
        if (x < 0) {
            alpha = 1 - (-x / mSwipeView.getWidth());
        } else if (x > 0) {
            alpha = 1 - (x / mSwipeView.getWidth());
        }

        mSwipeView.setAlpha(alpha);
    }

    private void up(MotionEvent ev) {
        float translationX = mSwipeView.getTranslationX();

        if(Math.abs(translationX) / (mSwipeView.getWidth() / 2.0f) > 0.75f) {
            delete(translationX > 0 ? 1 : -1);
        } else {
            mSwipeView.animate().setDuration(100).alpha(1).translationX(0);
            mSwipeView.setElevation(0);
            mSwipeView = null;
        }
    }

    private void delete(int direction) {
        mSwipeView.animate().setDuration(100).alpha(0).translationX(direction * mSwipeView.getWidth()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(mSwipeView != null) {
                    mSwipeView.animate().setDuration(100).alpha(1).translationX(0);
                    mSwipeView = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if(mSwipeView != null) {
                    mSwipeView.animate().setDuration(100).alpha(1).translationX(0);
                    mSwipeView = null;
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    protected View getSwipeable(int x, int y) {
        int position = pointToPosition(x, y);
        if (position != INVALID_POSITION) {
            return getChildAt(position - getFirstVisiblePosition());
        }
        return null;
    }


}
