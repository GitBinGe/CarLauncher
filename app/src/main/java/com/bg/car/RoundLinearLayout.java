package com.bg.car;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

/**
 * Created by BinGe on 2016/10/19.
 * 圆角线性布局
 */

public class RoundLinearLayout extends LinearLayout {
    public RoundLinearLayout(Context context) {
        super(context);
    }

    public RoundLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float scale = dm.density * 30;
        canvas.save();
        Path p = new Path();
        p.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), scale, scale, Path.Direction.CCW);
        canvas.clipPath(p);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
