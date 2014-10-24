package com.revo.display.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.revo.display.R;

public class RBatteryMeter extends View implements BatteryChangeListener{
    public static final float DEFAULT_MAX_CHARGE = 100;
    private final float CHARGE_TEXT_SIZE = 150f;
    private static final String TAG = RBatteryMeter.class.getSimpleName();
    final RectF oval = new RectF();
    private float centerX;
    private float centerY;
    private float radius;
    private int ON_COLOR = Color.argb(255, 0xff, 0xA5, 0x00);
    private int OFF_COLOR = Color.argb(255, 0x3e, 0x3e, 0x3e);
    private int SCALE_COLOR = Color.argb(255, 255, 255, 255);
    private float SCALE_SIZE = 35f;
    private float READING_SIZE = 80f;
    private Paint onMarkPaint;
    private Paint offMarkPaint;
    private Paint scalePaint;
    private Paint readingPaint;
    private Path onPath;
    private Path offPath;
    private float mMaxCharge;
    private float mCurrentCharge;

    public RBatteryMeter(Context context) {
        super(context);
        Log.d(TAG, "Battery Meter(Context) called");
    }

    public RBatteryMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Battery MEter(Context, AttributeSet) called");
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
        onMarkPaint = new Paint();
        onMarkPaint.setStyle(Paint.Style.STROKE);
        onMarkPaint.setColor(ON_COLOR);
        onMarkPaint.setStrokeWidth(35f);
        onMarkPaint.setShadowLayer(5f, 0f, 0f, ON_COLOR);
        onMarkPaint.setAntiAlias(true);

        offMarkPaint = new Paint(onMarkPaint);
        offMarkPaint.setColor(OFF_COLOR);
        offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(0f, 0f, 0f, OFF_COLOR);

        scalePaint = new Paint(offMarkPaint);
        scalePaint.setStrokeWidth(2f);
        scalePaint.setTextSize(SCALE_SIZE);
        scalePaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        scalePaint.setColor(SCALE_COLOR);

        readingPaint = new Paint(scalePaint);
        readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(3f, 0f, 0f, Color.WHITE);
        readingPaint.setTextSize(CHARGE_TEXT_SIZE);
        readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.WHITE);

        onPath = new Path();
        offPath = new Path();
    }

    public float getCurrentCharge() {return mCurrentCharge;}

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
        Log.d(TAG, "Size changed to " + width + "x" + height);

        // Setting up the oval area in which the arc will be drawn
        if (width > height) {
            radius = height / 4;
        } else {
            radius = width / 4;
        }
        oval.set(centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawScaleBackground(canvas);
        drawScale(canvas);
        drawLegend(canvas);
        drawReading(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
//		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        centerX = chosenDimension / 2;
        centerY = chosenDimension / 2;
        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    private int getPreferredSize() {
        return 300;
    }

    private void drawScaleBackground(Canvas canvas) {
        canvas.drawARGB(0, 0, 0, 0);
        Log.d(TAG, "drawScaleBackground");
        offPath.reset();
        for (int i = -180; i < 0; i += 4) {
            offPath.addArc(oval, i, 2f);
        }
        canvas.drawPath(offPath, offMarkPaint);
    }

    private void drawScale(Canvas canvas) {
        onPath.reset();
        for (int i = -180; i < (mCurrentCharge / mMaxCharge) * 180 - 180; i += 4) {
            onPath.addArc(oval, i, 10f);
        }
        onMarkPaint.setColor(Color.argb(255, 255 - (int)((mCurrentCharge/mMaxCharge)*255), (int)((mCurrentCharge/mMaxCharge)*255), 0));
        canvas.drawPath(onPath, onMarkPaint);
    }

    private void drawLegend(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(-180, centerX, centerY);
        Path circle = new Path();
        double halfCircumference = radius * Math.PI;
        double increments = 20;
        for (int i = 0; i < this.mMaxCharge; i += increments) {
            circle.addCircle(centerX, centerY, radius, Path.Direction.CW);
            canvas.drawTextOnPath(String.format("%d", i), circle, (float) (i * halfCircumference / this.mMaxCharge), -30f, scalePaint);
        }

        canvas.restore();
    }

    private void drawReading(Canvas canvas) {
        Path path = new Path();
        String message = String.format("%d", (int) this.mCurrentCharge);
        float[] widths = new float[message.length()];
        readingPaint.getTextWidths(message, widths);
        float advance = 0;
        for (double width : widths)
            advance += width;
        //Log.d(TAG,"advance: "+advance);
        path.moveTo(centerX - advance / 2, centerY);
        path.lineTo(centerX + advance / 2, centerY);
        canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
    }

    @Override
    public void onChargeChanged(float newChargeValue) {
        this.setCurrentCharge(newChargeValue);
        this.invalidate();
    }
}
