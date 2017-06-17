package cn.shawn.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import cn.shawn.view.R;

/**
 * Created by root on 17-6-15.
 */

public class SortTextView extends View {

    public static final String TAG =SortTextView.class.getSimpleName();

    private String mText;

    private int mTextSize;

    private int mTextDefaultColor;

    private int mTextSortColor;

    private int mArrowSortColor;

    private int mArrowDefaultColor;

    private int mPaddingLeft, mPaddingRight, mPaddingTop, mPaddingBottom;

    private int mArrowPaddingLeftRight, mArrowWidth;

    private int mArrowHeight = dp2px(4);

    private Path mPathUp,mPathDown;

    private Paint mPaint;

    private Rect mTextBound, mArrowBound;

    private SortState mState = SortState.STATE_DEFAULT;

    private OnSortStateChangeListener mSortStateListener;

    public void setSortStateListener(OnSortStateChangeListener sortStateListener) {
        this.mSortStateListener = sortStateListener;
    }

    public SortTextView(Context context) {
        this(context, null);
    }

    public SortTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextBound = new Rect();
        mArrowBound = new Rect();
        mPathDown = new Path();
        mPathUp = new Path();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SortTextView);
        mText = array.getString(R.styleable.SortTextView_text);
        mTextSize = array.getDimensionPixelSize(R.styleable.SortTextView_textSize,sp2px(15));
        mTextDefaultColor = array.getColor(R.styleable.SortTextView_textDefaultColor,Color.DKGRAY);
        mTextSortColor = array.getColor(R.styleable.SortTextView_textSortColor,Color.RED);
        mArrowSortColor = array.getColor(R.styleable.SortTextView_arrowSortColor, Color.RED);
        mArrowDefaultColor = array.getColor(R.styleable.SortTextView_arrowDefaultColor,Color.DKGRAY);
        mPaddingLeft = array.getDimensionPixelSize(R.styleable.SortTextView_paddingLeft, dp2px(5));
        mPaddingRight = array.getDimensionPixelSize(R.styleable.SortTextView_paddingLeft, dp2px(5));
        mPaddingTop = array.getDimensionPixelSize(R.styleable.SortTextView_paddingLeft, dp2px(5));
        mPaddingBottom = array.getDimensionPixelSize(R.styleable.SortTextView_paddingLeft, dp2px(5));
        mArrowPaddingLeftRight = array.getDimensionPixelSize(R.styleable.SortTextView_arrowPaddingLeftRight, dp2px(5));
        mArrowWidth = array.getDimensionPixelSize(R.styleable.SortTextView_arrowWidth,dp2px(6));
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mTextBound.left = mPaddingLeft;
        mTextBound.top = mPaddingTop;
        mTextBound.right = (int) (mPaddingLeft + mPaint.measureText(mText,0,mText.length()));
        mTextBound.bottom = (int) (mTextBound.top+ metrics.bottom - metrics.top);

        mArrowBound.left = mPaddingLeft+mTextBound.width()+mArrowPaddingLeftRight;
        mArrowBound.top = mPaddingTop + dp2px(2);
        mArrowBound.right = mArrowBound.left + mArrowWidth;
        mArrowBound.bottom = mArrowBound.top + mTextBound.height() - dp2px(4);

        mPathUp.moveTo(mArrowBound.left, mArrowBound.top+mArrowHeight);
        mPathUp.lineTo(mArrowBound.left + mArrowWidth/2, mArrowBound.top);
        mPathUp.lineTo(mArrowBound.right, mArrowBound.top + mArrowHeight);

        mPathDown.moveTo(mArrowBound.left, mArrowBound.bottom-mArrowHeight);
        mPathDown.lineTo(mArrowBound.left + mArrowWidth/2, mArrowBound.bottom);
        mPathDown.lineTo(mArrowBound.right, mArrowBound.bottom - mArrowHeight);

        int width = mTextBound.width() + mArrowBound.width() + mPaddingLeft +
                mPaddingRight + 2 * mArrowPaddingLeftRight;
        int height = mTextBound.height() + mPaddingTop + mPaddingBottom;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        int baseline = (int) (mTextBound.centerY() + (metrics.bottom - metrics.top) / 2 - metrics.bottom);

        mPaint.setStrokeWidth(0);
        mPaint.setColor(mState != SortState.STATE_DEFAULT?mTextSortColor:mTextDefaultColor);
        canvas.drawText(mText, mTextBound.centerX(), baseline, mPaint);

        mPaint.setStrokeWidth(dp2px(1.5f));
        //draw up
        mPaint.setColor((mState== SortState.STATE_DEFAULT
                || mState == SortState.STATE_DOWN)?mArrowDefaultColor: mArrowSortColor);
        canvas.drawPath(mPathUp, mPaint);
        //draw down
        mPaint.setColor((mState== SortState.STATE_DEFAULT
                || mState == SortState.STATE_UP)?mArrowDefaultColor: mArrowSortColor);
        canvas.drawPath(mPathDown, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                switch (mState){
                    case STATE_DEFAULT:
                        mState = SortState.STATE_UP;
                        break;
                    case STATE_UP:
                        mState = SortState.STATE_DOWN;
                        break;
                    case STATE_DOWN:
                        mState = SortState.STATE_UP;
                        break;
                }
                invalidate();
                if(mSortStateListener != null)
                   mSortStateListener.onStateChanged(mState);
                break;
        }
        return super.onTouchEvent(event);
    }

    public void resetSort(){
        mState = SortState.STATE_DEFAULT;
        postInvalidate();
    }

    public interface OnSortStateChangeListener{
        void onStateChanged(SortState state);
    }

    public enum SortState {
        STATE_DEFAULT,STATE_UP,STATE_DOWN
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
