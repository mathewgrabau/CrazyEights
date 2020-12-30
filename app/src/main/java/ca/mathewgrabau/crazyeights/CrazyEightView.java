package ca.mathewgrabau.crazyeights;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private Bitmap nextCardButton;
    private Paint paint;

    /**
     * Indicates if the player is currently the one playing.
     */
    private boolean isPlayerTurn;

    private int movingIndex;
    private int movingX;
    private int movingY;

    private int validSuit;
    private int validRank;

    private ComputerPlayer computerPlayer;

    private int currentScore;
    private int computerScore;
    private int playerScore;

    public CrazyEightView(Context context) {
        super(context);
        this.context = context;
        scale = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(scale * 15);
        computerPlayer = new ComputerPlayer();
        movingIndex = -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("Opponent score " + Integer.toString(computerScore), 10,
                paint.getTextSize() + 10, paint);
        canvas.drawText("Player score " + Integer.toString(playerScore), 10,
                screenHeight -paint.getTextSize() - 10, paint);

        // Drawing the draw pile (rendering a single card for it)
        // Putting this first in the drawing order so that we can use the development that
        float cardBackLeft = (screenWidth / 2) - (cardBack.getWidth() - 10);
        float cardBackTop = (screenHeight / 2) - (cardBack.getHeight()  / 2);
        canvas.drawBitmap(cardBack, cardBackLeft, cardBackTop, null);

        // Show the discard pile (rendering the first card in it)
        if (!discardPile.isEmpty()) {
            canvas.drawBitmap(discardPile.get(0).getBitmap(),
                    (screenWidth / 2) + 10,
                    (screenHeight / 2) - (cardBack.getHeight() / 2),
                    null);
        }

        // Render computer cards (just the backs of course)
        for (int i = 0; i < computerHand.size(); ++i) {
            canvas.drawBitmap(cardBack, i * (scale*5), paint.getTextSize()+(50 * scale),
                    null);
        }

        // Draw the next arrow
        if (playerHand.size() > 7) {
            canvas.drawBitmap(nextCardButton,
                    screenWidth - nextCardButton.getWidth() - (30 * scale),
                    screenHeight - nextCardButton.getHeight() - scaledCardHeight - (90 * scale),
                    null);
        }

        // Render player hand
        for (int i = 0; i < playerHand.size(); ++i) {
            if (i == movingIndex) {
                // Animating the card that we are moving
                canvas.drawBitmap(playerHand.get(i).getBitmap(),
                        movingX, movingY, null);
            } else {
                // Need to only draw the cards.
                if (i < 7) {
                    int left = i * (scaledCardWidth + 5);
                    int top = (int)(screenHeight - scaledCardHeight - paint.getTextSize() - (50 * scale));
                    Log.d(TAG, "card " + i + " at " + left + ", " + top);
                    // Draw the stationary card
                    canvas.drawBitmap(playerHand.get(i).getBitmap(),
                            left,
                            top,
                            null);
                }
            }
        }

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        // Okay location for calling because this invoked only once (due to the settings applied).
        initializeDeck();
        dealCards();
        drawCard(discardPile);
        validSuit = discardPile.get(0).getSuit();
        validRank = discardPile.get(0).getRank();

        scaledCardWidth = (int)(screenWidth / 8);
        scaledCardHeight = (int)(scaledCardWidth * 1.28);

        // Loading the bitmap and scaling it (for the card back)
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.card_back);
        cardBack = Bitmap.createScaledBitmap(tempBitmap, scaledCardWidth, scaledCardHeight, false);

        nextCardButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_next);

        // Determine who is playing now
        isPlayerTurn = new Random().nextBoolean();
        if (!isPlayerTurn) {
            computerPlay();
        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int eventX = (int)event.getX();
        final int eventY = (int)event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isPlayerTurn) {
                    // Test which card is being selected from the list of cards
                    for (int i = 0; i < 7; ++i) {
                        if (eventX > i * (scaledCardWidth + 5) && eventX < i * (scaledCardWidth + 5) + scaledCardWidth &&
                            eventY > screenHeight - scaledCardHeight- paint.getTextSize() - (50 * scale)) {
                            movingIndex = i;
                            movingX = eventX - (int)(30 * scale);
                            movingY = eventY - (int)(70 * scale);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // Update the location for the card.
                movingX = eventX - (int)(30 * scale);
                movingY = eventY - (int)(70 * scale);
                break;

            case MotionEvent.ACTION_UP:
                boolean isWithinX = eventX > (screenWidth / 2) - (100 * scale) &&
                        eventX < (screenWidth / 2) + (100 * scale);
                boolean isWithinY = eventY > (screenHeight / 2) - (100 * scale) &&
                        eventY < (screenHeight / 2) + (100 * scale);
                // Check for an event within the drop zone.
                if (movingIndex > -1 && isWithinX && isWithinY) {
                    Card playerCard = playerHand.get(movingIndex);
                    // Check to see if the play is valid or not
                    int movingRank = playerCard.getRank();
                    int movingSuit = playerCard.getSuit();

                    // Seeing if the card that was played was valid.
                    if (movingRank == 8 || movingRank == validRank || movingSuit == validSuit) {
                        // Setting the next card that is being done
                        validRank = movingRank;
                        validSuit = movingSuit;
                        discardPile.add(0, playerCard);
                        playerHand.remove(movingIndex);
                        if (playerHand.isEmpty()) {
                            endHand();
                        } else {
                            if (validRank == 8) {
                                changeSuit();
                            } else {
                                isPlayerTurn = false;
                                computerPlay();
                            }
                        }
                    } else {
                        // Show a quick message to the user that it is not valid.
                        Toast.makeText(context, "That move is not valid", Toast.LENGTH_SHORT).show();
                    }
                }

                // Checking if the user is trying to draw a card.
                boolean inDrawZoneX = eventX > (screenWidth / 2) - (100 * scale) &&
                        eventX < (screenWidth / 2) + (100 * scale);
                boolean inDrawZoneY = eventY > (screenHeight / 2) - (100 * scale) &&
                        eventY < (screenHeight / 2) + (100 * scale);
                if (movingIndex == -1 && isPlayerTurn && inDrawZoneX && inDrawZoneY) {
                    if (isValidDraw()) {
                        drawCard(playerHand);
                    } else {
                        Toast.makeText(context, "You have a valid play.", Toast.LENGTH_SHORT).show();
                    }
                }

                // Cancel the move card action/event
                movingIndex = -1;
                break;
        }

        invalidate();
        return true;
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
                // After getting the discarded cards back into the deck, shuffle them.
                Collections.shuffle(deck, new Random());
            }
        }
    }

    /**
     * Execute the computer's turn.
     */
    private void computerPlay() {
        int tempPlay = 0;
        while (tempPlay == 0) {
            tempPlay = computerPlayer.playCard(computerHand, validSuit, validRank);
            if (tempPlay == 0) {
                drawCard(computerHand);
            }
        }
        // If the computer is trying to play an eight, need to
        if (tempPlay == 108 || tempPlay == 208 || tempPlay == 308 || tempPlay == 408) {
            validRank = 8;  // The card in play is an 8
            String suitText = "";
            switch (validSuit) {
                case Card.SUIT_DIAMONDS:
                    suitText = "Diamonds";
                    break;
                case Card.SUIT_CLUBS:
                    suitText = "Clubs";
                    break;
                case Card.SUIT_HEARTS:
                    suitText = "Hearts";
                    break;
                case Card.SUIT_SPADES:
                    suitText = "Spades";
                    break;
            }

            Toast.makeText(context, "Computer chose " + suitText, Toast.LENGTH_SHORT).show();
        } else {
            // If not playing an eight, set it to what they are actually playing.
            validSuit = Math.round((tempPlay / Card.SUIT_SCALING) * Card.SUIT_SCALING);
            validRank = tempPlay - validSuit;
        }

        // Go through and putting that card that was played onto the discard pile.
        for (int i = 0; i < computerHand.size(); ++i) {
            Card currentCard = computerHand.get(i);
            if (tempPlay == currentCard.getId()) {
                discardPile.add(0, computerHand.get(i));
                computerHand.remove(i);
            }
        }

        // Finalize the game if the computer is done.
        if (computerHand.isEmpty()) {
            endHand();
        }

        isPlayerTurn = true;
    }

    /**
     * Shows the dialog that user needs to use to choose the suit when playing an 8.
     */
    private void changeSuit() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_suit_dialog);
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.suitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.suits, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Button okButton = (Button)dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                validSuit = (spinner.getSelectedItemPosition() + 1) * 100;
                String suitText = "";
                switch (validSuit) {
                    case 100:
                        suitText = "Diamonds";
                        break;
                    case 200:
                        suitText = "Clubs";
                        break;
                    case 300:
                        suitText = "Hearts";
                        break;
                    case 400:
                        suitText = "Spades";
                        break;
                }

                dialog.dismiss();
                Toast.makeText(context, "You choose " + suitText, Toast.LENGTH_SHORT).show();
                isPlayerTurn = false;
                computerPlay();
            }
        });
        // Need to get an invalidate in there.
        dialog.show();
        invalidate();
        // Showing the user information that is being done.
    }

    private void endHand() {
        String endHandMessage = "";
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.end_hand_dialog);
        updateScores();

        final TextView endHandText = (TextView)dialog.findViewById(R.id.endHandText);
        if (playerHand.isEmpty()) {
            if (playerScore >= 300) {
                endHandMessage = String.format("You won. You have %d points. Play again?",
                        playerScore);
            } else {
                endHandMessage = String.format("You lost, you only got %d", currentScore);
            }
        } else if (computerHand.isEmpty()) {
            if (computerScore >= 300) {
                endHandMessage = String.format("Opponent scored %d. You lost. Play again?",
                        computerScore);
            } else {
                endHandMessage = String.format("Opponent has lost. They scored %d points.",
                        currentScore);
            }
        }
        endHandText.setText(endHandMessage);
        Button nextHandButton = (Button)dialog.findViewById(R.id.nextHandButton);
        if (computerScore >= 300 || playerScore >= 300) {
            nextHandButton.setText("New Game");
        }
        nextHandButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view) {
               if (computerScore >= 300 || playerScore >= 300) {
                   playerScore = 0;
                   computerScore = 0;
               }
               initNewHand();
               dialog.dismiss();
           }
        });
        dialog.show();
    }

    /**
     * Checks for a draw being valid for the player (if they are trying to draw a card).
     * @return
     */
    private boolean isValidDraw() {
        boolean canDraw = true;
        for (int i = 0; i < playerHand.size(); ++i) {
            int currentId = playerHand.get(i).getId();
            int currentRank = playerHand.get(i).getRank();
            int currentSuit = playerHand.get(i).getSuit();
            if (validSuit == currentSuit || validRank == currentRank || currentId == 108 ||
                currentId == 208 || currentId == 308 || currentId == 408) {
                canDraw = false;
            }
        }

        return canDraw;
    }

    private void updateScores() {
        for (int i = 0; i < playerHand.size(); ++i) {
            computerScore += playerHand.get(i).getScoreValue();
            currentScore += playerHand.get(i).getScoreValue();
        }
        for (int i = 0; i < computerHand.size(); ++i) {
            playerScore += computerHand.get(i).getScoreValue();
            currentScore += computerHand.get(i).getScoreValue();
        }
    }

    private void initNewHand() {
        currentScore = 0;
        if (playerHand.isEmpty()) {
            isPlayerTurn = true;
        } else if (computerHand.isEmpty()) {
            isPlayerTurn = false;
        }
    }

}
