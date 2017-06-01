package com.mproject.exercisedemo.progressdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.mproject.exercisedemo.progressdemo.R;

/**
 * 自定义的progerssbar
 *
 * @author lcl
 *         created by 2016-08-04 10:33
 */
public class IndicatorProgressBar extends ProgressBar {

    private TextPaint mTextPaint;
    private Drawable mDrawableIndicator;
    private OnTextSetListener mOnTextSetListener ;
    private int offset = 5;

    public IndicatorProgressBar(Context context) {
        this(context, null);
    }

    public IndicatorProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public IndicatorProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        initProgressBar();


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InvalidateProgressBarAttrs, defStyleAttr, 0);
        if (a != null) {
            try {
                initTextPaint(a);
                mDrawableIndicator = a.getDrawable(R.styleable.InvalidateProgressBarAttrs_progressIndicator);
                offset = (int) a.getDimension(R.styleable.InvalidateProgressBarAttrs_offset, 0);

            } finally {
                a.recycle();
            }
        }
    }

    /**
     * 初始化progressBar
     */
    private void initProgressBar() {

    }


    /**
     * 初始化TextPaint
     *
     * @param a
     */
    private void initTextPaint(TypedArray a) {
        //消除锯齿
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        //适配手机密度
        mTextPaint.density = getResources().getDisplayMetrics().density;

        mTextPaint.setTextSize(a.getDimension(R.styleable.InvalidateProgressBarAttrs_textSize, 10));
        mTextPaint.setColor(a.getColor(R.styleable.InvalidateProgressBarAttrs_textColor, Color.WHITE));

        //设置文本的对齐方式
        int alignIndex = (a.getInt(R.styleable.InvalidateProgressBarAttrs_textAlign, 1));
        if (alignIndex == 0) {
            mTextPaint.setTextAlign(Paint.Align.LEFT);
        } else if (alignIndex == 1) {
            mTextPaint.setTextAlign(Paint.Align.CENTER);
        } else if (alignIndex == 2) {
            mTextPaint.setTextAlign(Paint.Align.RIGHT);
        }

        //是否设置粗体
        int textStyle = (a.getInt(R.styleable.InvalidateProgressBarAttrs_textStyle, 1));
        if (textStyle == 0) {
            //参数skewX为倾斜因子，正数表示向左倾斜，负数表示向右倾斜。
            mTextPaint.setTextSkewX(0.0f);
            mTextPaint.setFakeBoldText(false);
        } else if (textStyle == 1) {
            mTextPaint.setTextSkewX(0.0f);
            mTextPaint.setFakeBoldText(true);
        } else if (textStyle == 2) {
            mTextPaint.setTextSkewX(-0.25f);
            mTextPaint.setFakeBoldText(false);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mDrawableIndicator != null) {
            //获取系统进度条的宽度 这个宽度也是自定义进度条的宽度 所以在这里直接赋值
            final int width = getMeasuredWidth();
            //画的时候需要将系统进度条与指示器分隔开来
            final int height = getMeasuredHeight() + getIndicatorHeight();
            setMeasuredDimension(width, height);
        }
    }

    /**
     * @return 获取指示器的高度
     */
    private int getIndicatorHeight() {

        if (mDrawableIndicator == null) {
            return 0;
        }

//        Rect rect = mDrawableIndicator.getBounds();
        //获取drawable的宽高
        Rect rect = mDrawableIndicator.copyBounds();
        int indicatorHeight = rect.height();
        return indicatorHeight;
    }

    /**
     *
     * @return 获取指示器的宽度
     */
    private int getIndicatorWidth() {

        if (mDrawableIndicator == null) {
            return 0;
        }

        Rect rect = mDrawableIndicator.copyBounds();
        int indicatorWidth = rect.width();

        return indicatorWidth;
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //开始获取层级drawable
        Drawable progressDrawable = getProgressDrawable();

        if (mDrawableIndicator != null) {
            if (progressDrawable != null && progressDrawable instanceof LayerDrawable) {
                //layer类型的drawable 所以要看层级 有几层
                LayerDrawable d = (LayerDrawable) progressDrawable;
                //getNumberOfLayers() 就是有基层drawable
                for (int i = 0; i < d.getNumberOfLayers(); i++) {
                    Rect rect = d.getDrawable(i).getBounds();
                    //开始设置rect的位置
                    //个人认为是Y轴上的位置
                    rect.top = getIndicatorHeight();
                    //rect.height()也就是每层drawable的高度当然也包括了指示器  也就是两层drawable的高度, 所以这块比较迷惑
                    rect.bottom = rect.height() + getIndicatorHeight();
                }
            } else if (progressDrawable != null) {
                Rect rect = progressDrawable.getBounds();
                rect.top = mDrawableIndicator.getIntrinsicHeight();
                rect.bottom = rect.height() + getIndicatorHeight();
            }
        }

        //开始更新进度条
        updateProgressBar();
        super.onDraw(canvas);

        //开始画进度条
        if (mDrawableIndicator != null) {
            canvas.save();
            int dx = 0;

            // 获取系统进度条最右边的位置 也就是头部的位置
            if (progressDrawable != null && progressDrawable instanceof LayerDrawable) {
                LayerDrawable d = (LayerDrawable) progressDrawable;
                Drawable indicator_progress = d.findDrawableByLayerId(R.id.indicator_progress);
                dx = indicator_progress.getBounds(). right;
            } else if (progressDrawable != null) {
                dx = progressDrawable.getBounds().right ;
            }

            //加入offset  比较迷惑算法
            dx = dx - getIndicatorWidth() / 2 - offset + getPaddingLeft();

            // 移动画笔位置
            canvas.translate(dx, 0);
            // 画出指示器
            mDrawableIndicator.draw(canvas);
            // 画出进度条上的文字
            //讨厌的三目运算符
            String text = "";
            //mOnTextSetListener != null ? mOnTextSetListener .getText(getProgress()) : Math.round(getScale(getProgress()) * 100.0f) + "%"
            if (mOnTextSetListener != null){
                text = mOnTextSetListener .getText();
            }

            canvas.drawText(text, getIndicatorWidth() / 2, getIndicatorHeight() / 2 + 1, mTextPaint );

            canvas.restore();
        }
    }

    private void updateProgressBar() {
        //开始获取层级drawable
        Drawable progressDrawable = getProgressDrawable();

        if (progressDrawable != null && progressDrawable instanceof LayerDrawable) {

            LayerDrawable d = (LayerDrawable) progressDrawable;
            //当前的比列
            final float scale = getScale(getProgress());
            //获取层级中制定的drawable
            //层级中的指示器drawable
            Drawable indicator_progress = d.findDrawableByLayerId(R.id.indicator_progress);
            Rect rect = d.getBounds();

            //指示器drawable的宽度
            final int width = rect.right - rect.left;

            if (indicator_progress != null) {
                Rect indicatorProgressBounds = indicator_progress.getBounds();
                //根据当前的进度来画指示器drawable的宽度
                indicatorProgressBounds.right = indicatorProgressBounds.left + (int) (width * scale + 0.5f);
                //设置指示器drawable的宽高
                indicator_progress.setBounds(indicatorProgressBounds);
            }

            //在指示器drawable上面再添加一层drawable 更好的增加进度条特效; 也就是叠加的图层
            Drawable indicatorPatternOverlay = d.findDrawableByLayerId(R.id.indicator_pattern);

            if (indicatorPatternOverlay != null) {

                if (indicator_progress != null) {
                    // 使叠加图层适应进度条大小
                    Rect indicatorPatternOverlayBounds = indicator_progress.copyBounds();

                    final int left = indicatorPatternOverlayBounds.left;
                    final int right = indicatorPatternOverlayBounds.right;

                    //这里比较迷惑  不知道算法
                    indicatorPatternOverlayBounds.left = (left + 1 > right) ? left : left + 1;
                    indicatorPatternOverlayBounds.right = (right > 0) ? right - 1 : right;
                    //设置叠加层drawable的宽度
                    indicatorPatternOverlay.setBounds(indicatorPatternOverlayBounds);
                } else {
                    Rect indicatorPatternOverlayBounds = indicatorPatternOverlay.getBounds();
                    indicatorPatternOverlayBounds.right = indicatorPatternOverlayBounds.left + (int) (width * scale + 0.5f);
                    indicatorPatternOverlay.setBounds(indicatorPatternOverlayBounds);
                }
            }
        }
    }

    /**
     * 获取当前的进度在进度条中的比列
     *
     * @param progress
     * @return
     */
    private float getScale(int progress) {
        //讨厌三目运算符
        //float scale = getMax() > 0 ? (float) progress / (float) getMax() : 0;
        float scale = 0 ;
        if (getMax() > 0 ){
            scale =  (float) progress / (float) getMax();
        }
        return scale;
    }

    /**
     * 设置指示器的文字颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    /**
     * 设置指示器的文字大小
     *
     * @param size
     */
    public void setTextSize(float size) {
        mTextPaint.setTextSize(size);
    }

    /**
     * 设置指示器的文字粗体
     *
     * @param bold
     */
    public void setTextBold(boolean bold) {
        mTextPaint.setFakeBoldText(true);
    }

    /**
     * 设置指示器的文字位置
     *
     * @param align
     */
    public void setTextAlign(Paint.Align align) {
        mTextPaint.setTextAlign(align);
    }

    public Drawable getmDrawableIndicator() {
        return mDrawableIndicator;
    }

    public void setmDrawableIndicator(Drawable mDrawableIndicator) {
        this.mDrawableIndicator = mDrawableIndicator;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setmOnTextSetListener(OnTextSetListener mOnTextSetListener) {
        this.mOnTextSetListener = mOnTextSetListener;
    }

    public interface OnTextSetListener {
        public String getText();
    }


}
