package cn.shawn.view.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import cn.shawn.view.R;

/**
 * Created by root on 17-6-17.
 */

public class SubmitButton extends View {

    public static final String TAG =SubmitButton.class.getSimpleName();

    private Paint mPaint, mPathPaint;

    private Rect mBounds;

    private RectF mCenterBounds;

    private Rect mTextBounds;

    private Rect mDynamicBounds;

    private RectF mCenterCircleBounds, mLeftCircleBounds, mRightCircleBounds;

    private Path mPath;

    private PathMeasure mPathMeasure;

    private String mText;

    private int mBaseline;

    private int mTextSize;

    private int mTextColor;

    private int mBackgroundColor;

    private int mCircleLightColor;

    private int mCircleDashColor;

    private int mTipsColor;

    private int mCollapseTime;

    private float mCurrCollapsingFraction = 1f;

    private int mDynamicX;

    private ValueAnimator mLoadingAnimator, mTransAnimator, mCollapsingAnimator;

    private boolean sLoading = false;

    private boolean sCollapsing = false;

    private boolean sPathTrans = false;

    private int mCurrLoadingIndex;

    private OnSubmitClickListener mSubmitListener;

    public void setSubmitListener(OnSubmitClickListener submitListener) {
        this.mSubmitListener = submitListener;
    }

    public SubmitButton(Context context) {
        this(context, null);
    }

    public SubmitButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubmitButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SubmitButton);
        mText = array.getString(R.styleable.SubmitButton_text);
        mTextColor = array.getColor(R.styleable.SubmitButton_textColor, Color.WHITE);
        mTextSize = array.getDimensionPixelSize(R.styleable.SubmitButton_textSize, sp2px(14));
        mBackgroundColor = array.getColor(R.styleable.SubmitButton_backgroundColor, Color.BLUE);
        mCircleLightColor = array.getColor(R.styleable.SubmitButton_circleLightColor,Color.WHITE);
        mCircleDashColor =array.getColor(R.styleable.SubmitButton_circleDashColor,Color.parseColor("#d2ffffff"));
        mTipsColor = array.getColor(R.styleable.SubmitButton_tipsColor,Color.WHITE);
        mCollapseTime = array.getInteger(R.styleable.SubmitButton_collapseTime,1000);
        array.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(dp2px(2));
        mPathPaint.setColor(Color.WHITE);

        mBounds = new Rect();
        mCenterBounds = new RectF();
        mTextBounds = new Rect();
        mCenterCircleBounds = new RectF();
        mLeftCircleBounds = new RectF();
        mRightCircleBounds = new RectF();
        mDynamicBounds = new Rect();
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasureSize(widthMeasureSpec),getMeasureSize(heightMeasureSpec));
        mBounds.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        int textWidth = (int) mPaint.measureText(mText, 0 ,mText.length());
        int textHeight = (int) (metrics.bottom - metrics.top);
        mTextBounds.set((getMeasuredWidth() - textWidth)/2, (getMeasuredHeight() - textHeight)/2,
                (getMeasuredWidth() + textWidth)/2, (getMeasuredHeight() + textHeight)/2 );
        mBaseline = (int) (mTextBounds.centerY() + (metrics.bottom - metrics.top)/2-metrics.bottom);
        mCenterCircleBounds.set((getMeasuredWidth()-getMeasuredHeight()/5)/2,
                getMeasuredHeight() / 5 * 2,
                (getMeasuredWidth() + getMeasuredHeight() / 5)/2,
                getMeasuredHeight() / 5 *3);
        mLeftCircleBounds.set((mCenterCircleBounds.centerX() - getMeasuredHeight()/ 5 * 2),
                mCenterCircleBounds.top,
                (mCenterCircleBounds.centerX() - getMeasuredHeight()/ 5 ),
                mCenterCircleBounds.bottom);
        mRightCircleBounds.set((mCenterCircleBounds.centerX() + getMeasuredHeight()/ 5 * 1),
                mCenterCircleBounds.top,
                (mCenterCircleBounds.centerX() + getMeasuredHeight()/ 5 *2),
                mCenterCircleBounds.bottom);
        mCenterBounds.set((getMeasuredWidth() - getMeasuredHeight())/2, 0,
                (getMeasuredWidth() + getMeasuredHeight())/2, getMeasuredHeight());
        mDynamicX = (int) ((mBounds.width() - mCenterBounds.width())/2);
        mDynamicBounds.top = 0;
        mDynamicBounds.bottom = mBounds.bottom;
        mPath.moveTo(mCenterBounds.left + mCenterBounds.width()/4, mCenterBounds.height()/2);
        mPath.lineTo(mCenterBounds.left + mCenterBounds.width()/16 * 7,mCenterBounds.height()/16 *11);
        mPath.lineTo(mCenterBounds.left + mCenterBounds.width()/8 * 6,mCenterBounds.height()/8 *3);
        mPathMeasure = new PathMeasure(mPath, true);
    }

    private int getMeasureSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }else{//(mode == MeasureSpec.AT_MOST) //这里默认宽度用match_parent 设定,高度wrap_content
            result = dp2px(60);
            result = Math.min(result, size);
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mBackgroundColor);
        mDynamicBounds.left = (int) (mBounds.centerX() - mCurrCollapsingFraction*mDynamicX);
        mDynamicBounds.right = (int) (mBounds.centerX() + mCurrCollapsingFraction*mDynamicX);
        canvas.drawRect(mDynamicBounds,mPaint);
        canvas.drawArc(mDynamicBounds.left - mBounds.height()/2,0,mDynamicBounds.left + mBounds.height()/2,
                mBounds.bottom,90,180,false,mPaint);
        canvas.drawArc(mDynamicBounds.right - mBounds.height()/2,0,mDynamicBounds.right + mBounds.height()/2,
                mBounds.bottom,-90,180,false,mPaint);
        if(sPathTrans){
            canvas.drawPath(mPath,mPathPaint);
            return;
        }
        if(sCollapsing){
            mPaint.setColor(mTextColor);
            mPaint.setAlpha((int) (255 *  mCurrCollapsingFraction));
            canvas.drawText(mText, mBounds.centerX(), mBaseline, mPaint);
            return;
        }
        if(!sLoading){
            mPaint.setColor(mTextColor);
            canvas.drawText(mText, mBounds.centerX(), mBaseline, mPaint);
        }else{
            mPaint.setColor(mCurrLoadingIndex == 0 ? mCircleLightColor:mCircleDashColor);
            canvas.drawArc(mLeftCircleBounds, 0, 360,false, mPaint);
            mPaint.setColor(mCurrLoadingIndex == 1 ? mCircleLightColor:mCircleDashColor);
            canvas.drawArc(mCenterCircleBounds, 0, 360,false, mPaint);
            mPaint.setColor(mCurrLoadingIndex == 2 ? mCircleLightColor:mCircleDashColor);
            canvas.drawArc(mRightCircleBounds, 0, 360,false, mPaint);
        }
    }

    public void startLoading(){
        if(mLoadingAnimator != null && mLoadingAnimator.isRunning()){
            return;
        }
        sLoading = true;
        mLoadingAnimator = ValueAnimator.ofInt(0, 1, 2, 3);
        mLoadingAnimator.setDuration(mCollapseTime/2);
        mLoadingAnimator.setInterpolator(new LinearInterpolator());
        mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrLoadingIndex = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mLoadingAnimator.start();
    }

    public void stopLoading(){
        if(mLoadingAnimator != null && mLoadingAnimator.isRunning()){
            mLoadingAnimator.cancel();
            mLoadingAnimator = null;
        }
        sLoading = false;
        sCollapsing = true;
        doCollapseAnimation();
    }

    private void doTransAnimation(){
        if(mTransAnimator != null && mTransAnimator.isRunning()){
            mTransAnimator.cancel();
            mTransAnimator = null;
        }
        sPathTrans =true;
        mTransAnimator = ValueAnimator.ofFloat(1, 0);
        mTransAnimator.setDuration(mCollapseTime);
        mTransAnimator.setInterpolator(new LinearInterpolator());
        mTransAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                DashPathEffect effect = new DashPathEffect(
                        new float[]{mPathMeasure.getLength(),mPathMeasure.getLength()}
                        ,mPathMeasure.getLength()*fraction);
                mPathPaint.setPathEffect(effect);
                postInvalidate();
            }
        });
        mTransAnimator.start();
    }

    private void doCollapseAnimation(){
        if(mCollapsingAnimator != null && mCollapsingAnimator.isRunning()){
            mCollapsingAnimator.cancel();
            mCollapsingAnimator = null;
        }
        mCollapsingAnimator = ValueAnimator.ofFloat(1);
        mCollapsingAnimator.setDuration(mCollapseTime);
        mCollapsingAnimator.setInterpolator(new LinearInterpolator());
        mCollapsingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrCollapsingFraction = (1-animation.getAnimatedFraction());
                postInvalidate();
            }
        });
        mCollapsingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                doTransAnimation();
            }
        });
        mCollapsingAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!sLoading || !sCollapsing || !sPathTrans){
                    // 加载中时不消费
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mSubmitListener != null)
                    mSubmitListener.onSubmitClick();
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnSubmitClickListener{
        void onSubmitClick();
        void onFinish();
    }

    private int sp2px(int value){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                ,value,getContext().getResources().getDisplayMetrics());
    }

    private int dp2px(float value){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                ,value,getContext().getResources().getDisplayMetrics());
    }
}
