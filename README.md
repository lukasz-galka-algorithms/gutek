# Gutek: Intelligent Revision Algorithms Framework

Gutek is a low-code framework for implementing and deploying intelligent revision algorithms. It provides a unified graphical interface and allows for the quick, easy, and low-code implementation of solutions to determine the optimal timing for revisions. Additionally, it is a ready-to-use solution for users who want to practice and repeat content, especially in language learning or memorization scenarios.

### Key Features:
- **Standard Revision Interface**: Users are presented with a word, and they must recall its translation.
- **Reverse Revision Interface**: A novel feature where users are given the translation and must recall the original word.
- **Built-in Algorithms**: The framework comes with two sample revision algorithms:
    - **SuperMemo 2**: A popular spaced repetition algorithm.
    - **Constant Coefficient Revision Algorithm**: A custom algorithm for repetition.
- **Automated Language Handling**: The framework automatically detects and applies translations for the application based on the provided translation files.
- **Deck & Card Management**: Words and their translations are organized into "cards," and these cards are grouped into "decks."
- **Customizable Cards**: The framework provides fully customizable cards, allowing users to personalize and adapt the content for their individual learning needs, making it a flexible solution for various applications.
- **Flexible Use Case**: The application is ideal for learning phrases, definitions, and foreign language vocabulary.
- **Statistics Presentation & Analysis**: Gutek includes functionality for displaying and analyzing revision progress with comprehensive statistical charts. Users can track their performance over time, visualize trends, and adjust learning strategies accordingly.
- **Revision Data Collection for Memory Analysis**: The application collects detailed data about revisions, which can be used for analyzing memory retention and forgetfulness. This feature is particularly useful for medical applications, such as studying cognitive decline or memory-related conditions.
- **Low-Code Extendable**: Gutek enables easy, low-code integration of new revision algorithms, making it a scalable solution for various learning applications. This allows developers to quickly add custom algorithms with minimal coding effort.
- **Cross-Platform Compatibility**: Since Gutek is built in Java, it is portable and can run on any system that supports the Java Virtual Machine (JVM), ensuring broad compatibility across different platforms.

## Requirements

To build and run this application, you will need the following tools installed on your system:

1. **Git**: To clone the repository.
    - Recommended version: `>= 2.20.0`

2. **Maven**: To build and package the application.
    - Recommended version: `>= 3.6.0`

3. **Java Development Kit (JDK)**: Required to compile and run the application.
    - Recommended version: `JDK 17` or newer (Java 17+ is supported)

## How to Use

1. **Clone the repository**:
   ```bash
   git clone https://github.com/lukasz-galka-korepetycje/gutek.git
2. **Build the project**: Run the following Maven commands to build and package the application (The executable `.jar` file will be generated in the `target` directory):
    ```bash
    mvn clean
    mvn package
3. **Run the application**: To run the application, use the following command in the terminal:
    ```bash
   java -jar target/gutek-1.0-exec.jar

## Developing New Algorithms

Gutek provides an automated interface for adding new revision algorithms. The entire framework is designed to automatically detect and integrate newly implemented algorithms, adjusting all graphical components to meet the specific requirements of your custom solution. This means that, as a developer, you only need to focus on implementing the logic of your algorithm, while the rest of the application—including UI components and database storage—will adapt accordingly.
To add your custom algorithm, follow these steps:

1. **Create a new class:**:
    * Place the class in the gutek.entities.algorithms package.
    * The class must extend the RevisionAlgorithm base class.
    * To ensure the class is stored in the database, annotate it with @Entity and @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS).
2. **Specify the Card Type (Generics)**:
    * When extending RevisionAlgorithm, you need to specify the type of card the algorithm will work with using generics. This type must extend CardBase. For example, if your algorithm is designed for CardConstantCoefficient cards, declare the class as RevisionAlgorithm\<CardConstantCoefficient\>.
    Example:
    ```java
    @Entity
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public class ConstantCoefficientRevisionAlgorithm extends RevisionAlgorithm<CardConstantCoefficient> {
        // Class implementation here...
    }
3. **Define Hyperparameters**:
   * You can define revision algorithm hyperparameters using custom annotations like @AlgorithmHiperparameter. Additionally, use validation annotations such as @NotNull, @NotEmpty, @AllowedValues, @Max, and @Min to enforce constraints on these parameters.
   Example:
   ```java
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_1")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient1;
4. **Implement Required Methods**:

   Override the necessary methods to define the revision logic, such as:
   * getAlgorithmName
   * createNewCard
   * initializeDefaultHiperparameters
   * reviseCard
   * reversReviseCard

   Override the methods for handling the user interface, including:
   * updateSize
   * updateTranslation
   * getRevisionButtonsPanel
   * getReverseRevisionButtonsPanel

5. **Developing New Card Types** (optional):
   * If your algorithm requires custom card types, create a new class representing your card.
   * Place the card class in the gutek.entities.cards package.
   * The class must extend the CardBase base class.
   * To ensure it is stored in the database, annotate the class with @Entity and @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS).
   Example:
    ```java
    @Entity
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public class CardConstantCoefficient extends CardBase {
        // Class implementation here...
    }

## Language Support

The application's available languages and all translations can be freely customized to suit your preferences. Adding support for a new language is automated—simply add a translation file in the format messages_{language_code}_{country_code}.properties to the src/main/resources directory with proper translations. The framework will automatically detect and add the new language settings.

## Source Code Documentation

The documentation is generated using Javadoc and is located in the `docs` folder. You can access the documentation by running in a browser the file docs/index.html.

## Database Settings

The framework uses Spring Data JPA with Hibernate to handle database connectivity, allowing seamless integration with any Spring Data JPA-compatible database such as MySQL, PostgreSQL, Oracle, or SQL Server. By default, Gutek is configured with an SQLite file-based database.

To configure the database, refer to the `src/main/resources/application.properties` file. The current settings are for SQLite, but you can switch to any other database by modifying the connection parameters in this file.

Additionally, to use a different database, you must add the appropriate JDBC driver dependency to the `pom.xml` file. For example, if you are using MySQL, include the following dependency:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
</dependency>