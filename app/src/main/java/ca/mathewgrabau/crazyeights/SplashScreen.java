package ca.mathewgrabau.crazyeights;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class SplashScreen extends View {
    private Context context;    // Needed launch the next screen
    private Bitmap titleGraphic;
    private Bitmap playButtonUp;
    private Bitmap playButtonDown;
    private boolean playButtonPressed;
    private int screenWidth;
    private int screenHeight;

    private String TAG = getContext().getClass().getName();

    public SplashScreen(Context context) {
        super(context);

        this.context = context;

        // Init/load the bitmap (image) for the screen
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.splash_graphic);

        playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
        playButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_up);
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

        int playButtonLeftPosition = (screenWidth - playButtonUp.getWidth()) / 2;
        if (playButtonPressed) {
            canvas.drawBitmap(playButtonDown, playButtonLeftPosition, (int)(screenHeight * 0.5),
                    null);
        } else {
            canvas.drawBitmap(playButtonUp, playButtonLeftPosition, (int)(screenHeight * 0.5),
                    null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        int touchX = (int)event.getX();
        int touchY = (int)event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int buttonLeft = (screenWidth - playButtonUp.getWidth()) / 2;
                int buttonRight = buttonLeft + playButtonUp.getWidth();
                int buttonTop = (int)(screenHeight * 0.5);
                int buttonBottom = buttonTop + playButtonUp.getHeight();

                boolean inBounds = touchX > buttonLeft && touchX < buttonRight &&
                        touchY > buttonTop && touchY < buttonBottom;
                if (inBounds) {
                    playButtonPressed = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                if (playButtonPressed) {
                    // Launch to main game screen, issuing the new intent
                    Intent mainGameIntent = new Intent(context, CrazyEight.class);
                    context.startActivity(mainGameIntent);
                }

                // Remove flag regardless, if no touch event then it can't be pressed at all.
                playButtonPressed = false;
                break;
        }

        invalidate();
        return true;
    }
}
