package gutek.gui.main;

import gutek.entities.decks.DeckBase;
import gutek.entities.users.AppUser;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.controls.TrashDeckCell;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The `TrashView` class represents the user interface for managing deleted decks.
 * It displays a list of deleted decks with options to restore or permanently delete them.
 */
@Component
public class TrashView extends AppView {
    /** List of deck cells representing deleted decks. */
    private List<TrashDeckCell> deckCellList;
    /** Panel containing the list of deleted decks. */
    private JPanel deckListPanel;
    /** Scroll pane for scrolling through the list of deleted decks. */
    private JScrollPane scrollPane;
    /** Service responsible for managing decks. */
    private final DeckService deckService;
    /**
     * Constructs a new `TrashView` object.
     *
     * @param frame the main application frame
     * @param translationService the service responsible for handling translations
     * @param deckService the service responsible for managing decks
     */
    public TrashView(MainFrame frame, TranslationService translationService,
                     DeckService deckService) {
        super(frame, translationService);
        this.deckService = deckService;

        setLayout(new BorderLayout());

        deckCellList = new ArrayList<>();
        deckListPanel = new JPanel();
        deckListPanel.setLayout(new BoxLayout(deckListPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(deckListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }
    /**
     * Updates the size and layout of the UI components according to the current window size and scaling factor.
     */
    @Override
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();

        this.setPreferredSize(dimensionScaled);

        int scrollbarWidth = ((Integer) UIManager.get("ScrollBar.width")).intValue() + 5;

        for (TrashDeckCell cell : deckCellList) {
            cell.updateSize(new Dimension(dimensionScaled.width - scrollbarWidth, dimensionScaled.height), scaleFactor);
        }

        deckListPanel.revalidate();
        deckListPanel.repaint();
    }
    /**
     * Updates the translations for the UI components based on the current locale.
     */
    @Override
    public void updateTranslation() {
        super.updateTranslation();

        for (TrashDeckCell cell : deckCellList) {
            cell.updateTranslation();
        }
    }
    /**
     * Updates the view with the list of deleted decks belonging to the currently logged-in user.
     * It removes all existing deck cells and reloads them based on the user's deleted decks.
     */
    @Override
    public void updateView() {
        super.updateView();

        deckListPanel.removeAll();
        deckCellList.clear();

        AppUser user = frame.getLoggedUser();

        for (DeckBase deck : deckService.findDecksByUser(user)) {
            if(deck.getIsDeleted()) {
                TrashDeckCell deckCell = new TrashDeckCell(deck,this, translationService, deckService);
                deckCellList.add(deckCell);
                deckListPanel.add(deckCell);
            }
        }


        deckListPanel.revalidate();
        deckListPanel.repaint();
    }
}
