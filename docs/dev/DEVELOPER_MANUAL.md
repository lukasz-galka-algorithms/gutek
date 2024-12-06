# Developer Manual

This developer manual provides detailed instructions for extending and maintaining **Gutek**. It covers adding new features, modifying existing functionality, and adapting the application to different requirements.

---

## **1. Adding Language Translations**

To add a new language to the application, follow these steps:

1. **Create a New Properties File**:
    - Navigate to the `src/main/resources` directory.
    - Add a new properties file using the file name format `messages_{language code}_{country code}.properties`.
    - For example, for Spanish, create a file named `messages_es_ES.properties`.

2. **Add Translations**:
    - Populate the new properties file with key-value pairs for all strings used in the application.
    - Use the same keys as in the existing translation files to ensure consistency.
    - Example for Spanish:
      ```properties
      window.title=Gutek: Revisión Inteligente
      window.ok_button=OK
      empty_translation=Traducción vacía
      
      ...
      
      revision_algorithm.supermemo2.reverse_button_4=Bueno
      revision_algorithm.supermemo2.reverse_button_5=Perfecto
      ```

3. **Add a Flag Icon**:
    - Include a flag icon for the new language in the `src/main/resources/images/flags/` directory.
    - The icon file must be in **PNG format** and named using the **country code** of the locale (e.g., `ES.png` for Spain).
    - The application uses the following method to prepare the flag icon:
      ```java
      private ImageView createFlagIcon(Locale locale) {
          String countryCode = locale.getCountry();
          return ImageUtil.createImageView("/images/flags/" + countryCode + ".png");
      }
      ```

4. **Rebuild the Application**:
    - After adding the new properties file and flag icon, recompile the application.
    - The new language will be automatically available in the **Language Menu** for selection.

By completing these steps, the application will seamlessly support the newly added language, including localized strings and a corresponding flag icon for easy identification in the interface.

---

## **2. Adding New Card Types**

To introduce a new card type in **Gutek**, follow these steps:

1. **Create a New Class**:
    - Navigate to the `src/main/gutek/entities/cards/` directory.
    - Create a new class that extends `CardBase`.
    - Annotate the class with the following annotations:
      - `@Entity`: Marks the class as a JPA entity to be mapped to a database table.
      - `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`: Specifies the inheritance strategy for JPA to create separate tables for each card type.
      - `@NoArgsConstructor`: Generates a default no-arguments constructor.
      - `@Getter`: Generates getter methods for all fields.
   
    Example:
    ```java
    @Entity
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    @NoArgsConstructor
    @Getter
    public class CardSuperMemo2 extends CardBase {
        // Class-specific fields and methods go here
    }
    ```
   
2. **Add Specific Fields**:
    - Define fields specific to the new card type. These fields should represent the additional data required for the card.
    
    Example:
    ```java
    protected int repetition;
    protected int reverseRepetition;
    ```

3. **Add a Constructor**:
    - Create a constructor that calls the base class constructor and initializes the specific fields for the new card type.
    
    Example:
    ```java
   public CardSuperMemo2(String front, String back, double easinessFactor, double reverseEasinessFactor, DeckBase deck) {
        super(front, back, deck);
        this.easinessFactor = easinessFactor;
        this.reverseEasinessFactor = reverseEasinessFactor;
        setRevisionDefault(easinessFactor);
        setReverseRevisionDefault(reverseEasinessFactor);
   }
   ```
   
4. **Add Specific Methods**:
   - Define methods to manipulate or reset the specific fields of the new card type. These methods ensure the behavior of the card is consistent with its intended functionality.
   
   Example:
    ```java
   public void setRevisionDefault(double easinessFactor) {
        this.repetition = 0;
        this.regularInterval = 1;
        this.incorrectCounter = 0;
        this.easinessFactor = easinessFactor;
   }
   ```

5. **Rebuild the Application**:
    - The system will automatically recognize the new type and managed it.
    - The database schema will be updated to create a dedicated table for the new card type.
    - The card type will be ready for use in revisions and other application features.

---

## **3. Adding New Revision Algorithms**

To implement a new revision algorithm in **Gutek**, follow these steps:

1. **Create a New Class**:
    - Navigate to the `src/main/gutek/entities/algorithms/` directory.
    - Create a new class that extends `RevisionAlgorithm` and specify the card type it will operate on (e.g., `CardSuperMemo2`).
      - The card type in the generic parameter defines the type of cards the algorithm will manage and revise.
    - Annotate the class with the following annotations:
      - `@Entity`: Marks the class as a JPA entity to be mapped to a database table.
      - `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`: Ensures a separate table is created for each algorithm type.
      - `@Getter` and `@Setter`: Automatically generate getter and setter methods for fields.

    Example:
    ```java
    @Entity
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    @Getter
    @Setter
    public class SuperMemo2RevisionAlgorithm extends RevisionAlgorithm<CardSuperMemo2> {
         // Class-specific fields and methods go here
    }
    ```

2. **Define Algorithm Hyperparameters**:
   - Add fields annotated with `@AlgorithmHiperparameter` to define the hyperparameters specific to the algorithm.
   - These hyperparameters will be configurable through the user interface.
   - Use additional validation annotations like `@NotNull`, `@NotEmpty`, `@AllowedValues`, `@Min`, or `@Max` to ensure data integrity and prevent invalid values.
   - Each hyperparameter defined with `@AlgorithmHiperparameter` must include a **translation key** for its description.
   - Add the corresponding translation keys to all translation files (e.g., `messages_en_US.properties`, `messages_es_ES.properties`).
   - Hyperparameters annotated with @AlgorithmHiperparameter will automatically appear in the settings for the algorithm.
   - Users can adjust these parameters in the interface, and the values will be validated according to the annotations.
   - Implement the `initializeDefaultHiperparameters()` method in the algorithm class to assign default values to all hyperparameter.
   
   Example:
   ```java
   @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.supermemo2.easiness_factor")
   @NotNull
   @NotEmpty
   @Min(value = 1.3)
   private Double initialEasinessFactor;
   
   @Override
   public void initializeDefaultHiperparameters() {
        this.initialEasinessFactor = 2.5;
    }
   ```

3. **Configure Algorithm Fields as Transient Elements**:
   - When adding fields to the revision algorithm, you need to distinguish between those that should be persisted in the database and those that are used solely for the user interface (e.g., buttons, icons). UI-related fields must be marked with the `@Transient` annotation to prevent them from being persisted.
   - Declare fields for UI components such as buttons and icons using the `@Transient` annotation.
   - These fields will be used to create graphical elements dynamically during runtime but won't be stored in the database.
   - Implement the `initializeGUI(double width, double height, double scaleFactor)` method in the algorithm class to create and initialize UI elements like buttons and icons.
   - Use helper methods for creating icons and setting button styles.

   Example:
   ```java
    @Transient
    private Button buttonGrade1;

    @Transient
    private ImageView buttonGrade1Icon;
   
    @Override
    public void initializeGUI(double width, double height, double scaleFactor){
        this.buttonGrade1 = new Button();

        initializeIcons();
        updateSize(width, height, scaleFactor);
        updateTranslation();
    }
   
    private void initializeIcons() {
        buttonGrade1Icon = ImageUtil.createImageView("/images/icons/grade1.png");
        buttonGrade1.setGraphic(buttonGrade1Icon);
    }
   
    @Override
    public void updateSize(double width, double height, double scaleFactor) {
        double buttonFontSize = 12 * scaleFactor;
        String buttonsStyle = "-fx-font-size: " + buttonFontSize + "px;";
        String buttonRadiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        buttonGrade1.setStyle(buttonsStyle + buttonRadiusStyle);
        updateIcons(scaleFactor);
    }
   
    @Override
    public void updateTranslation() {
        buttonGrade1.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_1"));
    }
   ```

4. **Add a Default Constructor**:
   - Create a default constructor that calls the base class constructor and initializes the fields.

   Example:
    ```java
   public SuperMemo2RevisionAlgorithm() {
        super();
   }
   ```

5. **Register Default Revision Strategies**:
   - To register and setup available revision strategies for the new revision algorithm, you must override the `initializeDefaultRevisionStrategies()` method. This method is responsible for registering the revision strategies that will be available for the algorithm.
   - Steps to Implement:
     - Start by clearing any previously registered strategies to ensure a clean setup.
     - Instantiate the required revision strategies for the algorithm.
     - Use the RevisionStrategy class, ensuring that it matches the generic type specified for the algorithm (e.g., CardSuperMemo2).
     - Each strategy must be initialized with a function to generate a `Pane` containing the buttons used for the strategy and a function to handle the card revision logic for the strategy.
     - Add the created strategies to the `revisionStrategies` list.
 
   Example:
   ```java
    @Override
    public void initializeDefaultRevisionStrategies() {
        revisionStrategies.clear();
        RevisionStrategy<CardSuperMemo2> regularStrategy = new RegularTextModeRevisionStrategy<>(this::getRegularRevisionButtonsPane, this::regularReviseCard);
        revisionStrategies.add(regularStrategy);
        RevisionStrategy<CardSuperMemo2> reverseStrategy = new ReverseTextModeRevisionStrategy<>(this::getReverseRevisionButtonsPane, this::reverseReviseCard);
        revisionStrategies.add(reverseStrategy);
    }
   ```

6. **Override the Remaining Revision Algorithm Methods**:
   - To finalize the implementation of a new revision algorithm, you must override additional methods from the `RevisionAlgorithm` base class. These methods handle specific behaviors and ensure the algorithm functions smoothly.
   - Implement `getAlgorithmName()` method. This method returns the algorithm's name using a translation key for localization.
   
   Example:
   ```java
   @Override
   public String getAlgorithmName() {
        return translationService.getTranslation(ALGORITHM_NAME_KEY);
   }
   ```

   - Implement `updateTranslation()` method. This method is responsible for updating the text of algorithm-related UI elements to reflect the current language settings. It uses translation keys for localization, and corresponding entries must be added to all translation files (e.g., messages_en_US.properties, messages_es_ES.properties).

   Example:
   ```java
   @Override
   public void updateTranslation() {
        buttonGrade1.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_1"));
        buttonGrade2.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_2"));
   }
   ```

   - Implement `updateSize(double width, double height, double scaleFactor)` method. This method is responsible for updating the sizes and styles of graphical elements associated with the algorithm. It ensures that UI components dynamically adjust their dimensions and styles based on the provided window width, height, and scaling factor.

   Example:
   ```java
   @Override
   public void updateSize(double width, double height, double scaleFactor) {
        double buttonFontSize = 12 * scaleFactor;
        String buttonsStyle = "-fx-font-size: " + buttonFontSize + "px;";
        String buttonRadiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        buttonGrade1.setStyle(buttonsStyle + buttonRadiusStyle);
        buttonGrade2.setStyle(buttonsStyle + buttonRadiusStyle);

        buttonGrade1.setPrefSize(width / 5, height);
        buttonGrade2.setPrefSize(width / 5, height);

        updateIcons(scaleFactor);
   }
   ```
   
   - Implement `createNewCard(String front, String back)` method. This method is used to create new cards that are compatible with the specific revision algorithm. The returned card type must match the generic parameter specified in the RevisionAlgorithm\<T\> class (e.g., CardSuperMemo2 in RevisionAlgorithm\<CardSuperMemo2\>). It initializes the card with necessary parameters, such as front, back, and hyperparameter values.

   Example:
   ```java
   @Override
   public CardSuperMemo2 createNewCard(String front, String back) {
        return new CardSuperMemo2(front, back, initialEasinessFactor, reverseInitialEasinessFactor, null);
   }
   ```
   
7. **Rebuild the Application**:
   - The system will automatically recognize the new revision algorithm and managed it.
   - The database schema will be updated to create a dedicated table for the new revision algorithm.
   - The revision algorithm will be ready for use in revisions and other application features.

---

## **4. Adding New Revision Strategies**

To create and register a new revision strategy in **Gutek**, follow these steps:

1. **Create a New Class**:
   - Navigate to the `src/main/gutek/domain/revisions/` directory.
   - Create a new class that extends `RevisionStrategy`.
   - Specify the generic type \<T\> for the `RevisionStrategy`, which must extend CardBase.

   Example:
    ```java
   public class RegularTextModeRevisionStrategy<T extends CardBase> extends RevisionStrategy<T>{
        // Implementation of methods specific to the strategy
   }
   ```

2. **Add a Constructor**:
   - Create a constructor that calls the base class constructor and initializes the specific fields for the new revision strategy.
   - Pass two functions to the base class:
     - A function that generates a `Pane` containing the strategy's buttons.
     - A function that handles the card revision logic.

   Example:
    ```java
   public RegularTextModeRevisionStrategy(Function<T, Pane> getButtonsPaneFunction, BiPredicate<Button, T> reviseCardFunction) {
        super(getButtonsPaneFunction, reviseCardFunction);
   }

3. **Implement Abstract Methods**:
   - Ensure to add corresponding translation key-value pairs to all translation files (e.g., messages_en_US.properties, messages_es_ES.properties) for proper localization support.
   - Implement `getRevisionStrategyTranslationKey()` method. This method returns the translation key for the strategy's name to support localization.

   Example:
   ```java
   @Override
   public String getRevisionStrategyTranslationKey() {
        return "regular_text_mode";
   }
   ```

   - Implement `getRevisionStrategyColor()` method. This method returns the color associated with the strategy for UI representation.

   Example:
   ```java
   @Override
   public Color getRevisionStrategyColor() {
        return Color.MAGENTA;
   }
   ```

   - Implement `getRevisionStrategyScene()` method. This method specifies the scene to be used for this strategy in the application.

   Example:
   ```java
   @Override
   public MainStageScenes getRevisionStrategyScene() {
        return MainStageScenes.REVISION_REGULAR_SCENE;
   }
   ```

   - Implement `getRevisionStrategyCardsCount(DeckService deckService, DeckBase deckBase)` method. This method returns the count of cards available for this strategy.

   Example:
   ```java
   @Override
   public int getRevisionStrategyCardsCount(DeckService deckService, DeckBase deckBase) {
        return deckService.getRegularRevisionCardsCount(deckBase);
   }
   ```
   
   - Implement `getNextRevisionDate(T card)` method. This method calculates and returns the next revision date for a given card.

   Example:
   ```java
   @Override
   public LocalDate getNextRevisionDate(T card) {
        return card.getNextRegularRevisionDate();
   }
   ```
   
4. **Register the Strategy in the Algorithm**:
   - To make the new strategy available in a revision algorithm, register it in the `initializeDefaultRevisionStrategies` method of the algorithm class.

   Example:
   ```java
   @Override
   public void initializeDefaultRevisionStrategies() {
        revisionStrategies.clear();

        RevisionStrategy<CardSuperMemo2> customStrategy = new CustomRevisionStrategy<>(
             this::getCustomRevisionButtonsPane, // Function to generate the buttons pane
             this::customReviseCard // Function to handle the revision logic
        );
        revisionStrategies.add(customStrategy);
   }
   ```

5. **Rebuild the Application**:
   - The system will automatically recognize the new revision strategy and managed it.
   - The revision strategy will be ready for use in revisions and other application features where the strategy is registered.

---

## **5. Adding New Charts to Statistics**

To add a new chart type in **Gutek**, follow these steps:

1. **Create a New Class**:
   - Navigate to the `src/main/gutek/domain/charts/charts/` directory.
   - Create a new class that extends `StatisticsChart`.
   - Annotate the class with `@Component` to make it a Spring-managed bean. This ensures that the chart will be automatically detected and available in the application.
  
   Example:
    ```java
   @Component
   public class AddedNewChart extends StatisticsChart {
        // Implementation for the chart logic
   }
   ```

2. **Add a Constructor**:
   - Use the constructor to inject necessary services like `TranslationService` or other services required for chart data.

   Example:
    ```java
   public AddedNewChart(TranslationService translationService, DeckService deckService) {
        super(translationService);
        this.deckService = deckService;
   }
   ```

3. **Implement Abstract Methods**:
   - Ensure to add corresponding translation key-value pairs to all translation files (e.g., messages_en_US.properties, messages_es_ES.properties) for proper localization support.
   - Implement `getChart(int range, DeckBase deck, Integer revisionStrategyIndex)` method. This method generates the chart with the required data and formatting. Use JavaFX chart classes such as BarChart, LineChart, etc., to create the visualization. Ensure the chart is populated with data specific to the `range`, `deck`, and optionally `revisionStrategyIndex`.

   Example:
   ```java
   @Override
   public Chart getChart(int range, DeckBase deck, Integer revisionStrategyIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(translationService.getTranslation("deck_view.statistics.day"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(translationService.getTranslation("deck_view.statistics.cards_number"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(getChartTitle(deck, revisionStrategyIndex));
        int[] addedNewCardsPerDay = countAddedNewCardsPerDay(range, deck);
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(translationService.getTranslation("deck_view.statistics.cards_number"));

        for (int i = addedNewCardsPerDay.length - 1; i >= 0; i--) {
            dataSeries.getData().add(new XYChart.Data<>(String.valueOf(-i), addedNewCardsPerDay[i]));
        }
        barChart.getData().add(dataSeries);
        return barChart;
   }
   ```

   - Implement `getChartTitle(DeckBase deck, Integer revisionStrategyIndex)` method. This method provides the title of the chart, localized using the `TranslationService`.

   Example:
   ```java
   @Override
   public String getChartTitle(DeckBase deck, Integer revisionStrategyIndex) {
        return translationService.getTranslation("deck_view.statistics.added_new_title");
   }
   ```

   - Implement `isRevisionStrategyIndependent()` method. This method returns true if the chart does not depend on any specific revision strategy, otherwise return false.

   Example:
   ```java
   @Override
   public boolean isRevisionStrategyIndependent() {
        return true;
   }
   ```

4. **Rebuild the Application**:
   - Spring will automatically detect the new chart because it is annotated with `@Component`.
   - The chart will appear in the appropriate sections of the application where charts are presented.

---

## **6. Registering and Managing New Scenes**

This section provides detailed instructions for adding and registering new scenes in Gutek. Scenes in Gutek follow the JavaFX Model-View-Controller (MVC) pattern, with views defined in FXML files, controllers in Java classes, and the main application stage managing scene transitions.

1. **Create a New FXML File**:
   - Navigate to the `src/main/resources/fxml/` directory.
   - Create an FXML file for the new scene, following the naming convention `NewSceneNameView.fxml`.
   - Define the structure of the view in the FXML file.
   
   Example:
    ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <?import javafx.scene.control.Label?>
   <?import javafx.scene.layout.VBox?>
   <?import javafx.scene.layout.BorderPane?>
   <BorderPane fx:id="rootPane" xmlns:fx="http://javafx.com/fxml" fx:controller="gutek.gui.controllers.main.AuthorsFXMLController">
        <center>
             <VBox alignment="CENTER" spacing="20">
                 <Label fx:id="authorLabel" text="Łukasz Gałka" style="-fx-font-size: 30px; -fx-font-weight: bold;"/>
                 <Label fx:id="yearLabel" text="Anno Domini 2024" style="-fx-font-size: 30px; -fx-font-weight: bold;"/>
             </VBox>
        </center>
   </BorderPane>
   ```
   
2. **Create a New Controller**:
   - Navigate to the `src/main/java/gutek/gui/controllers/` directory.
   - Create a new controller class for the scene by extending `FXMLController` base class.
   - Annotate the class with `@Component` to make it a Spring-managed bean. This ensures that the controller will be automatically detected and available in the application.

   Example:
   ```java
   @Component
   public class AuthorsFXMLController extends FXMLController {
        // Implementation for the controller logic
   }
   ```

   - Add a constructor. Use dependency injection for required services and components, such as `MainStage`, `FXMLFileLoader`, `TranslationService`, and `MenuBarFXMLController`. The superclass constructor of FXMLController must be called in the subclass constructor to properly initialize the core functionality provided by the base class. This ensures the controller is fully functional and integrates seamlessly with the application's lifecycle and dependency management. When calling the base `FXMLController` constructor in your subclass, provide the following arguments:
     - stage: Inject the MainStage instance provided by dependency injection.
     - fxmlFileLoader: Inject the FXMLFileLoader instance provided by dependency injection.
     - fxmlFilePath: Pass the string literal path to the FXML file associated with the controller.
     - translationService: Inject the TranslationService instance provided by dependency injection.
   
   Example:
   ```java
   public AuthorsFXMLController(MainStage stage,
        FXMLFileLoader fxmlFileLoader,
        TranslationService translationService,
        MenuBarFXMLController menuBarFXMLController) {
        super(stage, fxmlFileLoader, "/fxml/main/AuthorsView.fxml", translationService);
        this.menuBarFXMLController = menuBarFXMLController;
   }
   ```

   - The FXMLController serves as the base class for all controllers. Subclasses can override the base class methods.
     - Override `updateTranslation()` method if needed. This method is responsible for updating all translatable components in the current scene. It ensures that the user interface reflects the correct language based on the application's active locale.
     - Ensure to add corresponding translation key-value pairs to all translation files (e.g., messages_en_US.properties, messages_es_ES.properties) for proper localization support.
     
     Example:
     ```java
     @Override
     public void updateTranslation() {
          menuBarFXMLController.updateTranslation();
          menuDeckFXMLController.updateTranslation();

          frontLabel.setText(translationService.getTranslation("deck_view.add_card.front"));
          backLabel.setText(translationService.getTranslation("deck_view.add_card.back"));
          addButton.setText(translationService.getTranslation("deck_view.add_card.add"));
          importButton.setText(translationService.getTranslation("deck_view.add_card.import"));
     }
     ```

     - Override `updateSize()` method if needed. This method is responsible for dynamically adjusting the size and styles of all UI components within the current scene. It ensures that the user interface scales appropriately based on the application window's dimensions and the current window scaling factor.

     Example:
     ```java
     @Override
     public void updateSize() {
         menuBarFXMLController.updateSize();
         menuDeckFXMLController.updateSize();

         double scaleFactor = stage.getStageScaleFactor();
         String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
         String radiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

         frontLabel.setStyle(fontSizeStyle);
         backLabel.setStyle(fontSizeStyle);
         frontTextField.setStyle(fontSizeStyle + radiusStyle);
         backTextField.setStyle(fontSizeStyle + radiusStyle);
         addButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;" + radiusStyle);
         importButton.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;" + radiusStyle);

         frontLabel.setPrefSize(200 * scaleFactor,40 * scaleFactor);
         backLabel.setPrefSize(200 * scaleFactor,40 * scaleFactor);
         frontTextField.setPrefSize(200 * scaleFactor,40 * scaleFactor);
         backTextField.setPrefSize(200 * scaleFactor,40 * scaleFactor);
         addButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
         importButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        updateIcons(scaleFactor);
     }
     ```

      - Override `updateView()` method if needed. This method is responsible for refreshing and updating the dynamic content of the current scene. This involves adding, removing, or modifying elements in the user interface based on the application's current state or user interactions.

     Example:
     ```java
     @Override
     public void updateView(){
          menuBarFXMLController.updateView();
          menuDeckFXMLController.updateView();
     }
     ```

      - Override `updateView()` method if needed. This method is responsible for refreshing and updating the dynamic content of the current scene. This involves adding, removing, or modifying elements in the user interface based on the application's current state or user interactions.

     Example:
     ```java
     @Override
     public void updateView(){
          menuBarFXMLController.updateView();
          menuDeckFXMLController.updateView();
     }
     ```

      - Override `loadViewFromFXML()` method if needed. This method is responsible for initializing and loading the user interface for a scene from its associated FXML file. The base implementation ensures that the view is properly connected to its controller, which is managed as a Spring bean. If the default behavior does not meet the requirements of a specific scene, the method can be overridden.

     Example:
     ```java
     @Override
     public void loadViewFromFXML() {
          if (this.root == null) {
               this.root = fxmlFileLoader.loadFXML(fxmlFilePath, this);
          }
     }
     ```

     - Override `initWithParams(Object... params)` method if needed. This method is essential for initializing a scene with optional parameters. It allows dynamic configuration of the scene, ensuring that the necessary data and settings are applied before the scene is displayed.

     Example:
     ```java
     @Override
     public void initWithParams(Object... params) {
          if (params != null && params.length > 0 && params[0] instanceof DeckBase deckBase) {
               this.deck = deckBase;
               menuDeckFXMLController.initWithParams(deck);
          }
          menuBarFXMLController.initWithParams();

          menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

          addButton.setOnAction(e -> handleAddCard());
          importButton.setOnAction(e -> handleImportCards());

          initializeIcons();
     }
     ```

3. **Add the Scene to the Enum**:
   - Open the `MainStageScenes` enum from the directory `src/main/java/gutek/gui/controllers/`.
   - Add a new constant for the new scene.

   Example:
   ```java
   public enum MainStageScenes {
        NEW_SCENE
        // ...
   }
   ```

4. **Register the Scene in MainStage**:
   - Open the `MainStage` class from the directory `src/main/java/gutek/gui/controllers/`.
   - Add an entry for the new scene in the `getSceneControllerMap()` method.

   Example:
   ```java
   private Map<MainStageScenes, Class<? extends FXMLController>> getSceneControllerMap() {
        Map<MainStageScenes, Class<? extends FXMLController>> map = new EnumMap<>(MainStageScenes.class);
        map.put(MainStageScenes.NEW_SCENE, NewSceneFXMLController.class);
        // ...
        return map;
   }
   ```
   
5. **Switching Scenes**:
   - To switch to the new scene programmatically, call the `setScene(MainStageScenes nextScene, Object... nextSceneParams)` method on the `MainStage` object, passing the corresponding `MainStageScenes` constant and optionally parameters for the scene (these parameters are forwarded to the `initWithParams(Object... params)` method of the scene's controller, enabling dynamic configuration).

   Example:
   ```java
   stage.setScene(MainStageScenes.REVISION_ADD_NEW_CARD_SCENE, deck);
   ```

6. **Rebuild the Application**:
   - Spring will automatically detect the new scene controller.
   - You can switch to the new scene from anywhere in the application using the `MainStage.setScene()` method.

---

## **7. Changing the Database Settings**

The framework uses Spring Data JPA with Hibernate to handle database connectivity, allowing seamless integration with any Spring Data JPA-compatible database such as MySQL, PostgreSQL, Oracle, or SQL Server. By default, Gutek is configured with an SQLite file-based database.

To configure the database, refer to the `src/main/resources/application.properties` file. The current settings are for SQLite, but you can switch to any other database by modifying the connection parameters in this file.

Additionally, to use a different database, you must add the appropriate JDBC driver dependency to the `pom.xml` file. For example, if you are using MySQL, include the following dependency:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
</dependency>
```

---

## **8. Source Code Documentation**

The documentation is generated using Javadoc and is located in the `docs/dev/javadoc` folder. You can access the documentation by running in a browser the file `docs/dev/javadoc/index.html`.
