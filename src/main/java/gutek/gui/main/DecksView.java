package gutek.gui.main;

import gutek.entities.decks.DeckBase;
import gutek.entities.users.AppUser;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.controls.DeckCell;
import gutek.gui.deck.*;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The `DecksView` class represents the main view where all the decks associated with a user are displayed.
 * It provides options for navigating to different views related to deck management, such as adding cards,
 * revising, searching, and viewing statistics.
 */
@Component
public class DecksView extends AppView {
    /** List of deck cells representing individual decks. */
    private List<DeckCell> deckCellList;
    /** Panel containing the list of deck cells. */
    private JPanel deckListPanel;
    /** Scroll pane for handling a large number of deck cells. */
    private JScrollPane scrollPane;
    /** View for adding cards to a deck. */
    private final RevisionAddCardView addCardView;
    /** View for searching cards in a deck. */
    private final RevisionSearchView searchView;
    /** View for revising cards in a deck. */
    private final RevisionView revisionView;
    /** View for managing deck settings. */
    private final RevisionSettingsView settingsView;
    /** View for viewing deck statistics. */
    private final RevisionStatisticsView statisticsView;
    /** View for editing a card within a deck. */
    private final RevisionEditCardView revisionEditCardView;
    /** Service for handling deck operations. */
    private final DeckService deckService;

    /**
     * Constructs the `DecksView`, initializing the components and layout.
     *
     * @param frame the main frame of the application
     * @param translationService the service for managing translations
     * @param addCardView the view for adding new cards to a deck
     * @param searchView the view for searching cards in a deck
     * @param revisionView the view for revising cards in a deck
     * @param settingsView the view for managing deck settings
     * @param statisticsView the view for viewing deck statistics
     * @param revisionEditCardView the view for editing cards in a deck
     * @param deckService the service for managing decks
     */
    public DecksView(MainFrame frame, TranslationService translationService,
                     RevisionAddCardView addCardView,
                     RevisionSearchView searchView,
                     RevisionView revisionView,
                     RevisionSettingsView settingsView,
                     RevisionStatisticsView statisticsView,
                     RevisionEditCardView revisionEditCardView,
                     DeckService deckService) {
        super(frame, translationService);
        this.addCardView = addCardView;
        this.searchView = searchView;
        this.revisionView = revisionView;
        this.settingsView = settingsView;
        this.statisticsView = statisticsView;
        this.revisionEditCardView = revisionEditCardView;
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
     * Updates the size of the components, adjusting the deck cells according to the window size.
     */
    @Override
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();

        this.setPreferredSize(dimensionScaled);

        int scrollbarWidth = ((Integer) UIManager.get("ScrollBar.width")).intValue() + 5;

        for (DeckCell cell : deckCellList) {
            cell.updateSize(new Dimension(dimensionScaled.width - scrollbarWidth, dimensionScaled.height), scaleFactor);
        }

        deckListPanel.revalidate();
        deckListPanel.repaint();
    }
    /**
     * Updates the translations of the components, ensuring the UI displays the correct language.
     */
    @Override
    public void updateTranslation() {
        super.updateTranslation();

        for (DeckCell cell : deckCellList) {
            cell.updateTranslation();
        }
    }
    /**
     * Updates the view by reloading the list of decks for the current user and displaying them in the panel.
     */
    @Override
    public void updateView() {
        super.updateView();

        deckListPanel.removeAll();
        deckCellList.clear();

        AppUser user = frame.getLoggedUser();

        for (DeckBase deck : deckService.findDecksByUser(user)) {
            if (!deck.getIsDeleted()) {
                DeckCell deckCell = new DeckCell(deck, this,addCardView,
                        searchView, revisionView, settingsView, statisticsView, revisionEditCardView, frame, translationService, deckService);
                deckCellList.add(deckCell);
                deckListPanel.add(deckCell);
            }
        }


        deckListPanel.revalidate();
        deckListPanel.repaint();
    }
}
