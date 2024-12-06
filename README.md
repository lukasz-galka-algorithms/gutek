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
    - Recommended version: `JDK 21` or newer (Java 21+ is supported)

4. **Lombok Plugin**: If you plan to develop using an IDE, ensure the `Lombok` plugin is installed to support Lombok annotations.

5. **JavaFX Plugin**: The project uses JavaFX for building the graphical user interface. If your IDE does not natively support JavaFX, you may need to install appropriate plugins.

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
   java -jar target/gutek-1.0.jar
4. **Precompiled releases**:
    - Precompiled versions of the application are available in the `releases` directory of the repository.
    - To run a precompiled version:
      - Download the desired release from the `releases` directory.
      - Navigate to the directory containing the downloaded `.jar` file.
      - Run the application using
      ```bash
      java -jar gutek.jar
      ```
      
## Documentation

The documentation for the framework is organized as follows:
   - **User Manual**: Located in `docs/user/USER_MANUAL.md`. This guide provides instructions for end-users on how to use the application effectively.
   - **Developer Manual**: Located in `docs/dev/DEVELOPER_MANUAL.md`. This guide provides details for developers on extending and maintaining the application.
   - **Javadoc Documentation**: Generated API documentation is available in `docs/dev/javadoc`. To view it, open `docs/dev/javadoc/index.html` in your web browser.