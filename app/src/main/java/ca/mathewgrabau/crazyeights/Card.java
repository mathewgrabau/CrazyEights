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

    public static final int SUIT_DIAMONDS = 100;
    public static final int SUIT_CLUBS = 200;
    public static final int SUIT_HEARTS = 300;
    public static final int SUIT_SPADES = 400;

    /**
     * Used to scale the values for the suits - they start at this. Can be used to extract the
     * value as needed.
     */
    public static final int SUIT_SCALING = 100;

    /**
     * Constructor for the card.
     * @param newId The identifier (resource) that is assigned to the card.
     */
    public Card(int newId) {
        id = newId;
        suit = Math.round((id / 100) * 100);
        rank = id - suit;

        if (rank == 8) {
            scoreValue = 50;
        } else if (rank == 14) {
            scoreValue = 1;
        } else if (rank > 9 && rank < 14) {
            scoreValue = 10;
        } else {
            scoreValue = rank;
        }
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

    /**
     * Gets the score value for the card.
     * @return
     */
    public int getScoreValue() {
        return scoreValue;
    }
}
