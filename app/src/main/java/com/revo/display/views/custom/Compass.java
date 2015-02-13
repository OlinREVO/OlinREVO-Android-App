package com.revo.display.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

import com.revo.display.R;

/**
 * Created by isaac on 10/24/14.
 */
public class Compass extends View implements ValueChangeListener {
    private static final String TAG = Compass.class.getSimpleName();
    private static final long DIRECTION_INCREMENT = 5;
    private static final float DEFAULT_TEXT_SIZE = 75f;
    private static final float TEXT_RADIUS_COEFF = 0.3f;

    private int direction;
    private Point center;
    private double radius;
    private double innerRadius;

    private Paint bgPaint;
    private Paint fgPaint;
    private Paint textPaint;
    private final Rect textBounds = new Rect();

    public Compass(Context context) {
        super(context);
        Log.d(TAG, "Compass (Context) called");
        center = new Point();
        initDrawingTools();
    }

    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Compass (Context, AttributeSet) called");
        center = new Point();
        initDrawingTools();

        // TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
        //        R.styleable.Compass,
        //        0, 0);
        // TODO: add visual attributes
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void onValueChanged(float newDirection) {
        newDirection = newDirection % 360;
        int roundedDirection = (int) roundDown(newDirection, DIRECTION_INCREMENT);
        setDirection((int) roundedDirection);
        this.invalidate();
        Log.d("Compass", "" + roundedDirection);
    }

    private double roundDown(double val, double increment) {
        return val - (val % increment);
    }

    // 
    // VISUAL STUFF
    //

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        // TODO: set compass dimensions
        int dim = Math.min(width, height);
        radius = (double) dim / 2;
        innerRadius = (0.9 * radius);
        center.x = width / 2;
        center.y = height / 2;
        setTextSize(textPaint, radius);
    }

    public void initDrawingTools() {
        fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaint.setStyle(Paint.Style.FILL);
        fgPaint.setColor(Color.WHITE);
        fgPaint.setStrokeWidth(35f);
        fgPaint.setAntiAlias(true);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStrokeWidth(35f);
        bgPaint.setAntiAlias(true);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(35f);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(DEFAULT_TEXT_SIZE);
    }

    private void setTextSize(Paint paint, double radius) {
        float textSize = (float) (TEXT_RADIUS_COEFF * radius);
        paint.setTextSize(textSize);
    }

    private void drawBackGround(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
    }

    private void drawCompassBorder(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, (float) radius, fgPaint);
        canvas.drawCircle(center.x, center.y, (float) innerRadius, bgPaint);
    }

    private void drawDirectionText(Canvas canvas) {
        // get the direction out of 360
        String directionText = String.format("%d", direction);
        drawTextCentered(canvas, directionText, center.x, center.y, textPaint);
    }

    private void drawCardinalLabels(Canvas canvas) {
        String dirs[] = {"N", "E", "S", "W"};
        int degreeDiff = 90;
        for (String dir : dirs) {
            float x = center.x;
            float y = (float) (center.y - innerRadius - textPaint.ascent());
            drawTextCentered(canvas, dir, x, y, textPaint);
            canvas.rotate(degreeDiff, center.x, center.y);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        drawCompassBorder(canvas);
        drawDirectionText(canvas);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate((float) -direction, center.x, center.y);

        drawCardinalLabels(canvas);

        canvas.restore();
    }

    private void drawTextCentered(Canvas canvas, String text, float x, float y, Paint paint) {
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x - textBounds.exactCenterX(), y - textBounds.exactCenterY(), paint);
    }
}
