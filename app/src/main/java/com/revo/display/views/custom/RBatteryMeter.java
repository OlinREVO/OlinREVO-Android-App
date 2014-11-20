package com.revo.display.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.revo.display.R;

public class RBatteryMeter extends View implements BatteryChangeListener {
    public static final float DEFAULT_MAX_CHARGE = 100;
    Drawable battery;
    private float centerX, centerY;
    private int ON_COLOR = Color.argb(255, 0xff, 0xA5, 0x00);
    private Paint readingPaint;
    private Paint chargePaint;
    private float mMaxCharge;
    private float mCurrentCharge;

    public RBatteryMeter(Context context) {
        super(context);
    }

    public RBatteryMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RBatteryMeter, 0, 0);
        try {
            mMaxCharge = a.getFloat(R.styleable.RBatteryMeter_maxCharge, DEFAULT_MAX_CHARGE);
            mCurrentCharge = a.getFloat(R.styleable.RBatteryMeter_currentCharge, 0);
            ON_COLOR = a.getColor(R.styleable.RBatteryMeter_batteryOnColor, ON_COLOR);
        } finally {
            a.recycle();
        }
        initDrawingTools();
    }

    private void initDrawingTools() {
        chargePaint = new Paint();
        chargePaint.setStyle(Paint.Style.FILL);
        chargePaint.setColor(ON_COLOR);
        chargePaint.setStrokeWidth(35f);
        chargePaint.setShadowLayer(5f, 0f, 0f, ON_COLOR);
        chargePaint.setAntiAlias(true);

        readingPaint = new Paint();
        readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        readingPaint.setTextSize(200f);
        readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.WHITE);

        battery = getResources().getDrawable(R.drawable.empty_battery);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawBar(canvas);
        drawReading(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        centerX = widthSize / 2;
        centerY = heightSize / 2;

        setMeasuredDimension(widthSize, heightSize);
    }

    private void drawBar(Canvas canvas) {
        Rect rect = canvas.getClipBounds();
        rect.top = (int) (rect.bottom * (1 - (mCurrentCharge / mMaxCharge)));
        if (rect.top == rect.bottom) {
            rect.top += 1;
        }

        rect.left = (int) centerX / 4;
        rect.right = getMeasuredWidth() - rect.left;

        battery.setBounds(new Rect(rect.left, 0, rect.right, rect.bottom));
        battery.draw(canvas);
        canvas.drawRect(rect, chargePaint);
    }

    private void drawReading(Canvas canvas) {
        Path path = new Path();
        String message = String.format("%d", (int) this.mCurrentCharge);
        float[] widths = new float[message.length()];
        readingPaint.getTextWidths(message, widths);

        float advance = 0f;
        for (float width : widths) {
            advance += width;
        }

        float y = centerY + readingPaint.getTextSize() / 2;
        path.moveTo(centerX - advance / 2, y);
        path.lineTo(centerX + advance / 2, y);

        canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
    }

    @Override
    public void onChargeChanged(float newChargeValue) {
        this.setCurrentCharge(newChargeValue);
        chargePaint.setColor(Color.argb(140, 255 - (int) ((mCurrentCharge / mMaxCharge) * 255), (int) ((mCurrentCharge / mMaxCharge) * 255), 0));
        this.invalidate();
    }

    public void setCurrentCharge(float mCurrentCharge) {
        if (mCurrentCharge > this.mMaxCharge)
            this.mCurrentCharge = mMaxCharge;
        else if (mCurrentCharge < 0)
            this.mCurrentCharge = 0;
        else
            this.mCurrentCharge = mCurrentCharge;
    }
}
