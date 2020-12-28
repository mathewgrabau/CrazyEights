package ca.mathewgrabau.crazyeights;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class SplashScreen extends View {
    private Bitmap titleGraphic;
    private int screenWidth;
    private int screenHeight;

    private String TAG = getContext().getClass().getName();

    public SplashScreen(Context context) {
        super(context);

        // Init/load the bitmap (image) for the screen
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.splash_graphic);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Position the graphic into the center of the screen (horizontally)
        int titleLeft = (screenWidth - titleGraphic.getWidth()) / 2;
        canvas.drawBitmap(titleGraphic, titleLeft, 100, null);
    }
}
