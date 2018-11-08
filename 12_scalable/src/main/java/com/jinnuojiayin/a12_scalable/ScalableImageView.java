package com.jinnuojiayin.a12_scalable;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

/**
 * Created by xyl on 2018/11/8.
 */
public class ScalableImageView extends View {
    private static final float IMAGE_WIDTH = Utils.dp2px(300);
    private static final float OVER_SCALE_FACTOR = 1.5f;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap;
    private GestureDetectorCompat mGestureDetectorCompat;
    private boolean big;//是否是放大模式
    private float scaleFraction;//放缩属性百分比 0f - 1f
    private ObjectAnimator objectAnimator;
    private float originalOffsetX;
    private float originalOffsetY;
    private float offsetX;
    private float offsetY;
    private float smallScale;
    private float bigScale;
    OverScroller scroller;//

    public ScalableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmap = Utils.getAvatar(context.getResources(), (int) IMAGE_WIDTH);
        mGestureDetectorCompat = new GestureDetectorCompat(context, new ScalableOnGestureListener());
        scroller = new OverScroller(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        originalOffsetX = (getWidth() - bitmap.getWidth()) / 2f;
        originalOffsetY = (getHeight() - bitmap.getHeight()) / 2f;
        //图片宽高比
        float ratioBitmap = (float) bitmap.getWidth() / bitmap.getHeight();
        //view宽高比
        float ratio = (float) getWidth() / getHeight();
        if (ratioBitmap > ratio) {
            smallScale = (float) getWidth() / bitmap.getWidth();
            bigScale = (float) getHeight() / bitmap.getHeight() * OVER_SCALE_FACTOR;
        } else {
            smallScale = (float) getHeight() / bitmap.getHeight();
            bigScale = (float) getWidth() / bitmap.getWidth() * OVER_SCALE_FACTOR;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(offsetX, offsetY);
        float scale = smallScale + (bigScale - smallScale) * scaleFraction;
        canvas.scale(scale, scale, getWidth() / 2f, getHeight() / 2f);
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint);
        Log.e("ScalableImageView", "传入的图片宽度大小：" + IMAGE_WIDTH + "宽：" + bitmap.getWidth() + ",高：" + bitmap.getHeight());
    }

    public float getScaleFraction() {
        return scaleFraction;
    }

    public void setScaleFraction(float scaleFraction) {
        this.scaleFraction = scaleFraction;
        //刷新view
        invalidate();
    }

    /**
     * 属性动画自定义属性
     * scaleFraction必须实现get() set()方法
     *
     * @return
     */
    private ObjectAnimator getAnimator() {
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(this, "scaleFraction", 0, 1);
        }
        return objectAnimator;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //view的触摸时间交给GestureDetectorCompat来处理
        return mGestureDetectorCompat.onTouchEvent(event);
    }

    public class ScalableOnGestureListener extends GestureDetector.SimpleOnGestureListener implements Runnable {

        @Override
        public boolean onDown(MotionEvent e) {
            //一定要返回true
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //双击事件
            big = !big;
            if (big) {
                getAnimator().start();
            } else {
                getAnimator().reverse();
                //将值重置
                offsetX = 0;
                offsetY = 0;
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //手指滑动
            if (big) {
                offsetX -= distanceX;
                offsetX = Math.min(offsetX, (bitmap.getWidth() * bigScale - getWidth()) / 2);
                offsetX = Math.max(offsetX, -(bitmap.getWidth() * bigScale - getWidth()) / 2);
                offsetY -= distanceY;
                offsetY = Math.min(offsetY, (bitmap.getHeight() * bigScale - getHeight()) / 2);
                offsetY = Math.max(offsetY, -(bitmap.getHeight() * bigScale - getHeight()) / 2);
                invalidate();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //惯性滑动
            if (big) {
                scroller.fling((int) offsetX, (int) offsetY, (int) velocityX, (int) velocityY,
                        -(int) (bitmap.getWidth() * bigScale - getWidth()) / 2,
                        (int) (bitmap.getWidth() * bigScale - getWidth()) / 2,
                        -(int) (bitmap.getHeight() * bigScale - getHeight()) / 2,
                        (int) (bitmap.getHeight() * bigScale - getHeight()) / 2);
                postOnAnimation(this);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                offsetX = scroller.getCurrX();
                offsetY = scroller.getCurrY();
                invalidate();
                postOnAnimation(this);
            }
        }
    }
}
