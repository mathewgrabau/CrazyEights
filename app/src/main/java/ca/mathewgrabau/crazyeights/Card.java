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

    /**
     * Constructor for the card.
     * @param newId The identifier (resource) that is assigned to the card.
     */
    public Card(int newId) {
        id = newId;
        suit = Math.round((id / 100) * 100);
        rank = id - suit;
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

    /**
     * Gets the suit that was assigned to the card.
     * @return
     */
    public int getSuit() {
        return suit;
    }

    /**
     * Gets the assigned rank for the card.
     * @return
     */
    public int getRank() {
        return rank;
    }

}
