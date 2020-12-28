package ca.mathewgrabau.crazyeights;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CrazyEightView extends View {
    private final String TAG = getContext().getClass().getName();

    private Context context;
    /**
     * Scaling factor when drawing graphics to accommodate for different screen sizes.
     */
    private float scale;

    private int scaledCardWidth;
    private int scaledCardHeight;
    private int screenWidth;
    private int screenHeight;

    private List<Card> deck = new ArrayList<Card>();
    private List<Card> computerHand = new ArrayList<Card>();
    private List<Card> playerHand = new ArrayList<Card>();
    private List<Card> discardPile = new ArrayList<Card>();

    private Bitmap cardBack;
    private Paint paint;

    public CrazyEightView(Context context) {
        super(context);
        this.context = context;
        scale = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Render computer cards (just the backs of course)
        for (int i = 0; i < computerHand.size(); ++i) {
            canvas.drawBitmap(cardBack, i * (scale*5), paint.getTextSize()+(50 * scale),
                    null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        // Okay location for calling because this invoked only once (due to the settings applied).
        initializeDeck();
        dealCards();

        scaledCardWidth = (int)(screenWidth / 8);
        scaledCardHeight = (int)(scaledCardWidth * 1.28);

        // Loading the bitmap and scaling it (for the card back)
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.card_back);
        cardBack = Bitmap.createScaledBitmap(tempBitmap, scaledCardWidth, scaledCardHeight, false);
    }

    /**
     * Prepares the card objects for all the cards in a deck.
     */
    private void initializeDeck() {
        // Ranks are 2 to Ace (14)
        // Suits are hundreds to allow for developing
        final int SUIT_INCREMENT = 100;
        final int RANK_START = 2;
        final int RANK_END = 14 + 1;

        for (int suit = 0; suit < 4; ++suit) {
            for (int cardRank = RANK_START; cardRank < RANK_END; ++ cardRank) {
                final int currentId = cardRank + (suit * SUIT_INCREMENT) + SUIT_INCREMENT;
                Card tempCard = new Card(currentId);
                final String resourceName = "card" + currentId;
                final int resourceId = getResources().getIdentifier(resourceName,
                        "drawable", context.getPackageName());
                Log.d(TAG, "Loading resource " + resourceName);
                Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(),
                        resourceId);
                scaledCardWidth = (int)(screenWidth / 8);
                scaledCardHeight = (int)(scaledCardWidth * 1.28);
                Bitmap scaledBitmap = null;
                if (tempBitmap != null) {
                    scaledBitmap = Bitmap.createScaledBitmap(tempBitmap, scaledCardWidth,
                            scaledCardHeight, false);
                    tempCard.setBitmap(scaledBitmap);
                    deck.add(tempCard);
                } else{
                    Log.e(TAG, "Could not load bitmap " + resourceName);
                }
            }
        }
    }

    /**
     * Deals cards into the hands (collections) for both the player and the computer.
     */
    private void dealCards() {
        Collections.shuffle(deck, new Random());
        for (int i = 0; i < 7; ++i) {
            drawCard(playerHand);
            drawCard(computerHand);
        }
    }

    private void drawCard(final List<Card> hand) {
        hand.add(0, deck.get(0));
        deck.remove(0);

        // Reshuffle old cards back into the deck when out of cards
        if (deck.isEmpty()) {
            for (int i = discardPile.size() - 1; i > 0; i--) {
                deck.add(discardPile.get(i));
                discardPile.remove(i);
            }

            // After getting the discarded cards back into the deck, shuffle them.
            Collections.shuffle(deck, new Random());
        }
    }
}
