package me.zhouzhuo810.cameracardcrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Rect to crop
 * Created by zhouzhuo810 on 2017/6/15.
 */
public class RectView extends View {
    
    private int width;
    private int height;
    private int cornerColor = 0xff00ff00;
    private int bgColor = 0x3f000000;
    private int rectLineColor = 0xffffffff;
    private int textColor = 0xffffffff;
    private int textSize = 30;
    private int topOffset = 0;
    
    private Paint bgPaint;
    private Paint rectPaint;
    private Paint cornerPaint;
    private Paint textPaint;
    
    private RectF topRect;
    private RectF leftRect;
    private RectF rightRect;
    private RectF bottomRect;
    
    private String hintText = CameraConfig.DEFAULT_HINT_TEXT;
    
    public RectView(Context context) {
        super(context);
        init(context, null);
    }
    
    public RectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public RectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context ctx, AttributeSet attrs) {
        bgPaint = new Paint();
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        
        cornerPaint = new Paint();
        cornerPaint.setColor(cornerColor);
        cornerPaint.setStyle(Paint.Style.STROKE);
        cornerPaint.setStrokeWidth(4f);
        cornerPaint.setAntiAlias(true);
        
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        
        rectPaint = new Paint();
        rectPaint.setColor(rectLineColor);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(2f);
        bgPaint.setAntiAlias(true);
    }
    
    public void setMaskColor(int color) {
        this.bgColor = color;
        bgPaint.setColor(bgColor);
    }
    
    
    public void setHintTextAndTextSize(String hint, int textSizeInPixel) {
        this.hintText = hint;
        this.textSize = textSizeInPixel;
        textPaint.setTextSize(textSize);
    }
    
    public void setRatioAndPercentOfScreen(int w, int h, float percent) {
        if (w >= h) {
            this.width = (int) (ScreenUtils.getScreenWidth(getContext()) * percent);
            this.height = width * h / w;
        } else {
            this.height = (int) ((ScreenUtils.getScreenHeight(getContext()) - dp2px(100f)) * percent);
            this.width = height * w / h;
        }
        
        //        Log.e("XXX", "w="+w+",h="+h+",percnet="+percent+",width="+width+",height="+height);
        invalidate();
    }
    
    /**
     * dp转换成px
     */
    private int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    
    public void updateRatio(int w, int h) {
        this.height = width * h / w;
        invalidate();
    }
    
    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
        if (topOffset == 0) {
            this.topOffset = -dp2px(40) / 2;
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width > 0) {
            drawBgWithoutRect(canvas);
            drawCorner(canvas);
            drawText(canvas);
        }
    }
    
    private void drawText(Canvas canvas) {
        if (hintText != null) {
            float textWidth = textPaint.measureText(hintText);
            canvas.drawText(hintText, getWidth() / 2.0f - textWidth / 2.0f, topRect.bottom - textSize, textPaint);
        }
    }
    
    private void drawCorner(Canvas canvas) {
        Path leftTopPath = new Path();
        leftTopPath.moveTo(leftRect.right + 20, leftRect.top);
        leftTopPath.lineTo(leftRect.right, leftRect.top);
        leftTopPath.lineTo(leftRect.right, leftRect.top + 20);
        canvas.drawPath(leftTopPath, cornerPaint);
        
        Path rightTop = new Path();
        rightTop.moveTo(rightRect.left - 20, rightRect.top);
        rightTop.lineTo(rightRect.left, rightRect.top);
        rightTop.lineTo(rightRect.left, rightRect.top + 20);
        canvas.drawPath(rightTop, cornerPaint);
        
        Path leftBottom = new Path();
        leftBottom.moveTo(leftRect.right + 20, leftRect.bottom);
        leftBottom.lineTo(leftRect.right, leftRect.bottom);
        leftBottom.lineTo(leftRect.right, leftRect.bottom - 20);
        canvas.drawPath(leftBottom, cornerPaint);
        
        Path rightBottom = new Path();
        rightBottom.moveTo(rightRect.left - 20, rightRect.bottom);
        rightBottom.lineTo(rightRect.left, rightRect.bottom);
        rightBottom.lineTo(rightRect.left, rightRect.bottom - 20);
        canvas.drawPath(rightBottom, cornerPaint);
    }
    
    private void drawBgWithoutRect(Canvas canvas) {
        topRect = new RectF(0, 0f + topOffset, getWidth(), (getHeight() - height) / 2.0f + topOffset);
        leftRect = new RectF(0, (getHeight() - height) / 2.0f + topOffset, (getWidth() - width) / 2.0f, (getHeight() + height) / 2.0f + topOffset);
        rightRect = new RectF((getWidth() + width) / 2.0f, (getHeight() - height) / 2.0f + topOffset, getWidth(), (getHeight() + height) / 2.0f + topOffset);
        bottomRect = new RectF(0, (getHeight() + height) / 2.0f + topOffset, getWidth(), getHeight());
        canvas.drawRect(topRect, bgPaint);
        canvas.drawRect(leftRect, bgPaint);
        canvas.drawRect(rightRect, bgPaint);
        canvas.drawRect(bottomRect, bgPaint);
    }
    
    public int getCropLeft() {
        return (int) leftRect.right;
    }
    
    public int getCropTop() {
        return (int) topRect.bottom;
    }
    
    public int getCropWidth() {
        return (int) (rightRect.left - leftRect.right);
    }
    
    public int getCropHeight() {
        return (int) (bottomRect.top - topRect.bottom);
    }
    
    public void setCornerColor(int rectCornerColor) {
        this.rectLineColor = rectCornerColor;
        rectPaint.setColor(rectLineColor);
    }
}
