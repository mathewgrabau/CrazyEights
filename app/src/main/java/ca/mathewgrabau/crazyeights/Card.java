package ca.mathewgrabau.crazyeights;

import android.graphics.Bitmap;

/**
 * Class that represents a card for displaying on the screen (using a Bitmap).
 */
public class Card {
    private int id;
    private int suit;
    private int rank;
    private Bitmap bitmap;
    private int scoreValue;

    public Card(int newId) {
        id = newId;
    }

    /**
     * Change the bitmap that is displayed on the card.
     * @param newBitmap
     */
    public void setBitmap(Bitmap newBitmap) {
        bitmap = newBitmap;
    }

    /**
     * Gets the currently set bitmap.
     * @return The current bitmap.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Gets identifier that was assigned to the instance.
     * @return
     */
    public int getId() {
        return id;
    }
}
