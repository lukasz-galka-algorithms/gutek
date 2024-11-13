package gutek.domain.revisions;

import gutek.entities.cards.CardBase;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * Interface defining the behavior for a regular text mode revision process.
 *
 * <p>This interface provides methods for generating a panel of revision buttons and for handling
 * revision logic based on user interaction with these buttons.
 *
 * @param <T> the type of card being revised, extending {@link CardBase}
 */
public interface RegularTextModeRevision<T extends CardBase>{

    /**
     * Returns a panel containing the buttons for the normal revision process.
     *
     * @param card the card being revised
     * @return a panel containing the revision buttons
     */
    Pane getRegularRevisionButtonsPane(T card);

    /**
     * Handles the revision logic when a button is clicked during the normal revision process.
     *
     * @param clickedButton the button that was clicked
     * @param card the card being revised
     * @return {@code true} if the revision process is complete, {@code false} otherwise
     */
    boolean regularReviseCard(Button clickedButton, T card);
}
