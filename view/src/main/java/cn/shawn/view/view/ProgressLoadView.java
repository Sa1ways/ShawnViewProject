package cn.shawn.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import cn.shawn.view.R;

/**
 * Created by root on 17-6-19.
 * time consumed 1 hour
 */

public class ProgressLoadView extends View {

    public static final String TAG = ProgressLoadView.class.getSimpleName();

    private String mTitle = "100%";

    private int mTextColor;

    private int mBackgroundColor;

    private int mTextSize;

    private Paint mPaint;

    private int mIndicatorPadding ;

    private int mProgressBarMargin ;

    private int mProgressBarHeight = dp2px(4);

    private float mSingleWidth;

    private int mCurrProgress;

    private int mBaseline;

    private int mTriangleHeight;

    private float mTriangleHeightFraction = 0.15f;

    private RectF mIndicatorBounds, mProgressBounds;

    private Path mTrianglePath;

    private int mDeltaX;

    public void setCurrProgress(int currProgress) {
        this.mCurrProgress = currProgress;
        invalidate();
    }

    public ProgressLoadView(Context context) {
        this(context, null);
    }

    public ProgressLoadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressLoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        int textWidth = (int) mPaint.measureText(mTitle, 0, mTitle.length());
        int textHeight = (int) (metrics.bottom - metrics.top);
        //
        mTrianglePath = new Path();
        mIndicatorBounds = new RectF();
        mProgressBounds = new RectF();
        mIndicatorBounds.right = textWidth + 2 * mIndicatorPadding;
        mIndicatorBounds.bottom = (textHeight  + 2 * mIndicatorPadding);
        mProgressBounds.top = (int) (mIndicatorBounds.height()*(1+mTriangleHeightFraction)+mProgressBarMargin);
        mProgressBounds.bottom = mProgressBounds.top + mProgressBarHeight;
        mTriangleHeight = (int) (mIndicatorBounds.height() * mTriangleHeightFraction);
        mBaseline = (int) (mIndicatorBounds.centerY() - (metrics.bottom + metrics.top)/2);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressLoadView);
        mTextSize = array.getDimensionPixelSize(R.styleable.ProgressLoadView_textSize, sp2px(14));
        mTextColor = array.getColor(R.styleable.ProgressLoadView_textColor, Color.WHITE);
        mBackgroundColor = array.getColor(R.styleable.ProgressLoadView_backgroundColor, Color.BLUE);
        mProgressBarHeight = array.getDimensionPixelOffset(R.styleable.ProgressLoadView_progressBarHeight,dp2px(2.5f));
        mIndicatorPadding = array.getDimensionPixelOffset(R.styleable.ProgressLoadView_indicatorPadding,dp2px(1.5f));
        mProgressBarMargin = array.getDimensionPixelOffset(R.styleable.ProgressLoadView_progressBarMargin,dp2px(1.5f));
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (mIndicatorBounds.height()*(1+mTriangleHeightFraction) +
                2*mProgressBarMargin +mProgressBarHeight);
        setMeasuredDimension(getMeasureSize(widthMeasureSpec),height);
        mSingleWidth = getMeasuredWidth() / 100;
        mDeltaX = (int) (getMeasuredWidth() - 100 * mSingleWidth);
    }

    private int getMeasureSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }else{//(mode == MeasureSpec.AT_MOST)
            result = dp2px(400);
            result = Math.min(result, size);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // drawProgress
        mPaint.setColor(mBackgroundColor);
        mProgressBounds.right = mCurrProgress * mSingleWidth;
        canvas.drawRect(mProgressBounds, mPaint);
        if(mProgressBounds.right > mIndicatorBounds.centerX()
                && mIndicatorBounds.right < (getMeasuredWidth() - mDeltaX)){
            float delta =  (mProgressBounds.right - mIndicatorBounds.centerX());
            mIndicatorBounds.left += delta;
            mIndicatorBounds.right += delta;
        }
        // drawRoundRect
        canvas.drawRoundRect(mIndicatorBounds,10,10,mPaint);
        // drawTriangle
        mTrianglePath.reset();
        mTrianglePath.moveTo(mIndicatorBounds.centerX() - mTriangleHeight/2,mIndicatorBounds.bottom);
        mTrianglePath.lineTo(mIndicatorBounds.centerX(), mIndicatorBounds.bottom + mTriangleHeight);
        mTrianglePath.lineTo(mIndicatorBounds.centerX() + mTriangleHeight/2,mIndicatorBounds.bottom);
        mTrianglePath.close();
        canvas.drawPath(mTrianglePath, mPaint);
        // drawText
        mPaint.setColor(mTextColor);
        canvas.drawText(String.valueOf(mCurrProgress)+"%",mIndicatorBounds.centerX(),mBaseline,mPaint);
    }

    private int sp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                , value, getContext().getResources().getDisplayMetrics());
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , value, getContext().getResources().getDisplayMetrics());
    }


}
