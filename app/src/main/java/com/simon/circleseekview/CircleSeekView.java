package com.simon.circleseekview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ZHUQI on 2017/9/19.
 */

public class CircleSeekView extends View {

    private static final float CIRCLE_RATE = 0.37f;
    private int mWidth;
    private int mHeight;
    private Bitmap bitmapBg;
    private Bitmap bitmapThum;
    private Paint mPaint;
    private float thumLeftPosition;
    private float thumTopPosition;
    private int bitmapThumWidth;
    private int bitmapThumHeight;
    private int radius;
    private OnSelectColorListener mOnSelectColorListener;
    private int colorValue;
    private float circleD;
    private float top;
    private boolean isByUser;


    public CircleSeekView(Context context) {
        super(context);
    }

    public CircleSeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initBitmap();
    }

    private void initBitmap() {
        bitmapBg = BitmapFactory.decodeResource(getResources(), R.mipmap.k2_light_color_picker_bg);
        bitmapThum = BitmapFactory.decodeResource(getResources(), R.mipmap.k2_light_switch_thum);
        bitmapThumWidth = bitmapThum.getWidth()/2;
        bitmapThumHeight = bitmapThum.getHeight()/2;
    }

    private void initPaint(){
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
            radius = mWidth/2;

//            thumTopPosition = colorValue * CIRCLE_RATE *2 *radius/100;
//            //thumTopPosition = (colorValue-48)*CIRCLE_RATE*4*radius/100 + ((1-CIRCLE_RATE/2)*radius) ;
//            thumLeftPosition = (float) (Math.sqrt(CIRCLE_RATE*2*(radius) * CIRCLE_RATE*2*(radius) -
//                    Math.abs(thumTopPosition - radius) * Math.abs(thumTopPosition - radius))+radius);
        }else if(widthMode == MeasureSpec.AT_MOST){
            throw new IllegalArgumentException("width must be EXACTLY,you should set like android:width=\"200dp\"");
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }else if(widthMeasureSpec == MeasureSpec.AT_MOST){

            throw new IllegalArgumentException("height must be EXACTLY,you should set like android:height=\"200dp\"");
        }

        if(!isByUser){
            getFixedThumPostion(radius, 0);
            float top = thumTopPosition;
            getFixedThumPostion(radius, mHeight);
            float bottom = thumTopPosition;
            circleD = bottom - top;
            thumTopPosition = circleD*colorValue/100 + top;
            if(colorValue == 0 || colorValue == 100){
                thumLeftPosition = radius-bitmapThumWidth;
            }else {
                thumLeftPosition = (float) (Math.sqrt(CIRCLE_RATE*2*(radius) * CIRCLE_RATE*2*(radius) -
                        Math.abs(thumTopPosition+bitmapThumWidth - radius) * Math.abs(thumTopPosition+bitmapThumWidth - radius))+radius-bitmapThumWidth);
            }

            int rgb[] = getPointRGB((int)thumLeftPosition,(int)thumTopPosition);
            if(mOnSelectColorListener != null)
                mOnSelectColorListener.onSelectColor(rgb, getColorValue(),false);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmapBg,0,0,mPaint);
        canvas.drawBitmap(bitmapThum,thumLeftPosition,thumTopPosition,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ||
                event.getAction() == MotionEvent.ACTION_HOVER_MOVE ){
            isByUser = true;
            thumLeftPosition = event.getX() - bitmapThumWidth/2;
            thumTopPosition = event.getY()- bitmapThumHeight/2;
            getFixedThumPostion(thumLeftPosition,thumTopPosition);
            int rgb[] = getPointRGB((int)thumLeftPosition,(int)thumTopPosition);
            colorValue = (int) ((thumTopPosition-(1-CIRCLE_RATE/2)*radius)*100/(CIRCLE_RATE*4*(radius))) + 48;
            if(mOnSelectColorListener != null)
                mOnSelectColorListener.onSelectColor(rgb, getColorValue(),true);
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            if(mOnSelectColorListener != null)
                mOnSelectColorListener.onSelectColorFinish(getColorValue());
        }
        invalidate();
        return true;
    }

    /**
     * 根据y轴坐标换算成 0-100 的色温值
     * @return
     */
    public int getColorValue() {
        return colorValue;
    }

    public void setColorValue(int colorValue){
        isByUser = false;
        this.colorValue = colorValue;
    }

    /**
     * 根据当前坐标获取背景图片的RGB值
     * @param x
     * @param y
     * @return
     */
    private int[] getPointRGB(int x, int y) {
        int[] rgb = {Color.red(bitmapBg.getPixel(x,y)),
                Color.green(bitmapBg.getPixel(x,y)),
                Color.blue(bitmapBg.getPixel(x,y))};
        return rgb;
    }

    /**
     * 计算出圆点滑动轨迹内的位置坐标
     * @param x
     * @param y
     */
    private void getFixedThumPostion(float x, float y) {
        if(x > radius && y < radius){
            thumLeftPosition = (float) ((CIRCLE_RATE*mWidth *(x - radius))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius))) + radius-bitmapThumWidth;
            thumTopPosition = (float) (radius - (CIRCLE_RATE*mWidth *(radius - y))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius))) - bitmapThumHeight;
        }else if (x <= radius && y < radius){
            thumLeftPosition = (float) (radius - (CIRCLE_RATE*mWidth *(radius - x))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius)))-bitmapThumWidth;
            thumTopPosition = (float) (radius - (CIRCLE_RATE*mWidth *(radius - y))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius))) - bitmapThumHeight;
        }else if (x > radius && y >= radius){
            thumLeftPosition = (float) ((CIRCLE_RATE*mWidth *(x - radius))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius))) + radius-bitmapThumWidth;
            thumTopPosition = (float) ((CIRCLE_RATE*mWidth *(y -radius ))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius))) + radius - bitmapThumHeight;
        }else if (x <= radius && y >= radius){
            thumLeftPosition = (float) (radius - (CIRCLE_RATE*mWidth *(radius - x))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius)))-bitmapThumWidth;
            thumTopPosition = (float) ((CIRCLE_RATE*mWidth *(y -radius))/Math.sqrt((x - radius)*(x - radius) + (y - radius)*(y - radius))) + radius - bitmapThumHeight;
        }
    }

    public void setOnSelectColorListener(OnSelectColorListener listener) {
        this.mOnSelectColorListener = listener;
    }

    public interface OnSelectColorListener {
        void onSelectColor(int[] rgb, int colorValue,boolean isFromUser);
        void onSelectColorFinish(int colorValue);
    }

    public void recycleBitMap(){
        if(bitmapBg != null){
            bitmapBg.recycle();
            bitmapBg = null;
        }
        if(bitmapThum != null){
            bitmapThum.recycle();
            bitmapThum = null;
        }
    }

}


