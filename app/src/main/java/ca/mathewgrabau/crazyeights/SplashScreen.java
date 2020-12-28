package ca.mathewgrabau.crazyeights;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SplashScreen extends View {
    private Bitmap titleGraphic;

    private String TAG = getContext().getClass().getName();

    public SplashScreen(Context context) {
        super(context);

        // Init/load the bitmap (image) for the screen
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.splash_graphic);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(titleGraphic, 100, 100, null);
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "Down");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "Up");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "Move");
                cx = (int)event.getX();
                cy = (int)event.getY();
                break;
        }

        // Schedule a repainting (invoke draw method for the View).
        invalidate();
        return true;
    }

     */
}
