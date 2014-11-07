package com.revo.display.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

import com.revo.display.sensors.OrientationChangeListener;
import com.revo.display.sensors.OrientationSensor;

/**
 * Created by isaac on 10/24/14.
 */
public class Compass extends View implements OrientationChangeListener {
    private static final String TAG = Compass.class.getSimpleName();
    private static final double DIRECTION_INCREMENT = 5;

    private double direction;
    private Point center;
    private double radius;
    private double innerRadius;

    private Paint bgPaint;
    private Paint fgPaint;
    private Paint textPaint;

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

    public void setDirection(double direction) {
        this.direction = direction;
    }

    @Override
    public void onDirectionChange(float direction) {
        System.out.println("Direction: " + direction);

        // round direction to multiples of DIRECTION_INCREMENT
        direction = (float) (direction - (direction % DIRECTION_INCREMENT));
        if (direction != this.direction) {
            setDirection(direction);
            this.invalidate();
        }
    }

    // 
    // VISUAL STUFF
    //

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        // TODO: set compass dimensions
        int dim = Math.min(width, height);
        radius = (double) dim / 4;
        innerRadius = (0.9 * radius);
        center.x = width / 2;
        center.y = height / 2;

        System.out.println("\n\nSIZE CHANGED");
        System.out.println("=============");
        System.out.println("radius: " + radius);
        System.out.println("=============\n\n");
    }

    public void initDrawingTools() {
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.WHITE);
        bgPaint.setStrokeWidth(35f);
        bgPaint.setAntiAlias(true);

        fgPaint = new Paint();
        fgPaint.setStyle(Paint.Style.FILL);
        fgPaint.setColor(Color.BLACK);
        fgPaint.setStrokeWidth(35f);
        fgPaint.setTextSize(100);
        fgPaint.setAntiAlias(true);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(35f);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(75);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void drawBackGround(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
    }

    private void drawCompassBorder(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, (float) radius, bgPaint);
        canvas.drawCircle(center.x, center.y, (float) innerRadius, fgPaint);
    }

    private void drawDirectionText(Canvas canvas) {
        // get the direction out of 360
        double posDirection = direction < 0 ? 360 + direction : direction;
        String directionText = String.format("%.0f°", posDirection);
        canvas.drawText(directionText, (float) center.x, (float) center.y, textPaint);
    }

    private void drawCardinalLabels(Canvas canvas) {
        String dirs[] = {"N", "E", "S", "W"};
        for (String dir : dirs) {
            canvas.drawText(dir,
                    center.x,
                    (float) (center.y - innerRadius - textPaint.ascent()),
                    textPaint);
            canvas.rotate(-90, center.x, center.y);
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
}
