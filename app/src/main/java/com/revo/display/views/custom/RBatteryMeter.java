package com.revo.display.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.revo.display.R;

public class RBatteryMeter extends View implements BatteryChangeListener{
    public static final float DEFAULT_MAX_CHARGE = 100;
    private final float CHARGE_TEXT_SIZE = 150f;
    private static final String TAG = RBatteryMeter.class.getSimpleName();
    private float centerX, centerY;
    private int fill;
    private Rect rect;
    private int ON_COLOR = Color.argb(255, 0xff, 0xA5, 0x00);
    private int OFF_COLOR = Color.argb(255, 0x3e, 0x3e, 0x3e);
    private int SCALE_COLOR = Color.argb(255, 255, 255, 255);
    private float SCALE_SIZE = 35f;
    private float READING_SIZE = 80f;
    private Paint readingPaint;
    private Paint chargePaint;
    private float mMaxCharge;
    private float mCurrentCharge;

    Drawable battery;

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
            OFF_COLOR = a.getColor(R.styleable.RBatteryMeter_batteryOffColor, OFF_COLOR);
            SCALE_COLOR = a.getColor(R.styleable.RBatteryMeter_batteryScaleColor, SCALE_COLOR);
            SCALE_SIZE = a.getDimension(R.styleable.RBatteryMeter_batteryScaleTextSize, SCALE_SIZE);
            READING_SIZE = a.getDimension(R.styleable.RBatteryMeter_batteryReadingTextSize, READING_SIZE);
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
        readingPaint.setTextSize(CHARGE_TEXT_SIZE);
        readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.WHITE);

        battery = getResources().getDrawable(R.drawable.empty_battery);
    }

    public void setCurrentCharge(float mCurrentCharge) {
        if (mCurrentCharge > this.mMaxCharge)
            this.mCurrentCharge = mMaxCharge;
        else if (mCurrentCharge < 0)
            this.mCurrentCharge = 0;
        else
            this.mCurrentCharge = mCurrentCharge;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawBar(canvas);
        drawBattery(canvas);
        drawReading(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        centerX = chosenDimension / 2;
        centerY = (chosenDimension)/2 - heightSize/8;
        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private static int chooseDimension(int mode, int size) {
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    private static int getPreferredSize() {
        return 300;
    }

    private void drawBar(Canvas canvas) {
        fill = (int)((getBottom()) * (mCurrentCharge/mMaxCharge));
        Log.d(TAG, "L: " + getLeft() + " R: " + getRight() + " T: " + getTop() + " B: " + getBottom() + " Fill: " + fill);
        rect = canvas.getClipBounds();
//        rect.height() = ;
        canvas.drawRect(rect, chargePaint);
    }

    private void drawBattery(Canvas canvas) {
        battery.setBounds(canvas.getClipBounds());
        battery.draw(canvas);
    }

    private void drawReading(Canvas canvas) {
        Path path = new Path();
        String message = String.format("%d", (int) this.mCurrentCharge);
        float[] widths = new float[message.length()];
        readingPaint.getTextWidths(message, widths);
        float advance = 0;
        for (double width : widths)
            advance += width;
        path.moveTo(centerX - advance / 2, centerY);
        path.lineTo(centerX + advance / 2, centerY);
        canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
    }

    @Override
    public void onChargeChanged(float newChargeValue) {
        this.setCurrentCharge(newChargeValue);
        chargePaint.setColor(Color.argb(140, 255 - (int)((mCurrentCharge/mMaxCharge)*255), (int)((mCurrentCharge/mMaxCharge)*255), 0));
        this.invalidate();
    }
}
