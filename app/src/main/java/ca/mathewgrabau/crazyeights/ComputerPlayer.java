package ca.mathewgrabau.crazyeights;

import java.util.List;

public class ComputerPlayer {
    public int playCard(List<Card> hand, int suit, int rank) {
        int play = 0;
        for (int i = 0; i < hand.size(); ++i) {
            int currentId = hand.get(i).getId();
            int currentRank = hand.get(i).getRank();
            int currentSuit = hand.get(i).getSuit();

            // Seeing if we can match the rank.
            if (currentRank != 8) {
                if (rank == 8) {
                    if (suit == currentSuit) {
                        play = currentId;
                    }
                } else if (suit == currentSuit || rank == currentRank) {
                    play = currentId;
                }
            }
        }

        // No play yet (matching suit or rank). See if there's an eight that we can use.
        if (play == 0) {
            for (int i = 0; i < hand.size(); ++i) {
                int currentId = hand.get(i).getId();
                if (currentId == 108 || currentId == 208 || currentId == 308 || currentId == 408) {
                    play = currentId;
                }
            }
        }

        return play;
    }
}
