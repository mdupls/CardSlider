package com.dupls.cardslider;

import android.content.Context;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;

public class GridSliderViewTest extends ActivityInstrumentationTestCase2<CardActivity> {

    private GridSliderView mSliderView;

    public GridSliderViewTest() {
        super(CardActivity.class);
    }

    public void testConstructor() {
        init();

        assertNotNull(mSliderView);
    }

    public void testHasItems() {
        init();

        assertTrue(mSliderView.getChildCount() > 0);
    }

    @UiThreadTest
    public void testSlideItem() {
        init();

        mSliderView.scrollTo(0, 600);

        View child = mSliderView.getChildAt(10);

        assertEquals(0, mSliderView.pointToPosition(100, 100));
        assertEquals(0, mSliderView.pointToPosition(child.getLeft() + 20, child.getTop() + 20));
    }

    private void init() {
        mSliderView = (GridSliderView) getActivity().findViewById(R.id.gridView);
    }

}
