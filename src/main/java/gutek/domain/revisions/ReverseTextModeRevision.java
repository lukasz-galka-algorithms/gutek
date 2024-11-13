package gutek.domain.revisions;

import gutek.entities.cards.CardBase;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * Interface defining the behavior for a reverse text mode revision process.
 *
 * <p>This interface provides methods for generating a panel of revision buttons and for handling
 * revision logic based on user interaction with these buttons.
 *
 * @param <T> the type of card being revised, extending {@link CardBase}
 */
public interface ReverseTextModeRevision<T extends CardBase> {

    /**
     * Returns a panel containing the buttons for the reverse revision process.
     *
     * @param card the card being revised
     * @return a panel containing the reverse revision buttons
     */
    Pane getReverseRevisionButtonsPane(T card);

    /**
     * Handles the revision logic when a button is clicked during the reverse revision process.
     *
     * @param clickedButton the button that was clicked
     * @param card the card being revised
     * @return {@code true} if the reverse revision process is complete, {@code false} otherwise
     */
    boolean reverseReviseCard(Button clickedButton, T card);
}
