package com.mrsgx.campustalk.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Shao on 2017/9/27.
 */

public class AndroidBugSolver {
    public static void addLayoutListener(final View main, final View scroll) {
                    main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Rect rect = new Rect();
                            main.getWindowVisibleDisplayFrame(rect);
                            int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                            if (mainInvisibleHeight == 100) {
                                int[] location = new int[2];
                                scroll.getLocationInWindow(location);
                                int scrollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                                main.scrollTo(0, scrollHeight);
                            } else {
                    main.scrollTo(0, 0);
                }
            }
        });
    }
}
