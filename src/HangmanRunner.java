import java.util.*;
import java.util.stream.Collectors;

public class HangmanRunner {
    private static final String INITIAL_PROMPT = """
            Привет, хочешь начать новую игру?
            (1) Да
            (2) Выйти из приложения
            """;

    private static final List<String> WORDS = List.of(
            "ЯБЛОКО", "ТЕЛЕФОН", "КОМПЬЮТЕР", "СОБАКА", "СОЛНЦЕ",
            "БИБЛИОТЕКА", "АВТОМОБИЛЬ", "КОРОБКА", "МОЛОКО", "ПИАНИНО"
    );

    private static final String[] HANGMAN_STAGES = {
            """
             ______
             |    |
             |   
            _|_
            """,
            """
             ______
             |    |
             |    O
             |  
            _|_
            """,
            """
             ______
             |    |
             |    O
             |    |
             |   
            _|_
            """,
            """
             ______
             |    |
             |    O
             |   /|
             |  
            _|_
            """,
            """
             ______
             |    |
             |    O
             |   /|\\
             |   
            _|_
            """,
            """
             ______
             |    |
             |    O
             |   /|\\
             |   / 
            _|_
            """,
            """
             ______
             |    |
             |    O
             |   /|\\
             |   / \\
            _|_
            """
    };

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    private static final int MAX_ERRORS = 6;

    public static void main(String[] args) {
        while (true) {
            System.out.println(INITIAL_PROMPT);
            int choice = getMenuChoice();

            if (choice == 1) {
                startGameRound();
            } else {
                break;
            }
        }
        scanner.close();
    }

    private static int getMenuChoice() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 1 || choice == 2) {
                    return choice;
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
            }
            System.out.println("Пожалуйста, введите 1 или 2");
        }
    }

    private static void startGameRound() {
        String secretWord = selectRandomWord();
        StringBuilder maskedWord = new StringBuilder("_".repeat(secretWord.length()));
        Set<Character> incorrectGuesses = new HashSet<>();
        int errorsMade = 0;

        while (true) {
            printCurrentState(errorsMade, maskedWord, incorrectGuesses);

            char letter = promptForLetter();

            if (isLetterAlreadyGuessed(maskedWord, incorrectGuesses, letter)) {
                System.out.println("Вы уже пробовали эту букву!");
                continue;
            }

            if (updateGameState(secretWord, maskedWord, incorrectGuesses, letter)) {
                errorsMade++;
                if (errorsMade == MAX_ERRORS) {
                    endGame(false, secretWord);
                    return;
                }
            }

            if (isWordGuessed(maskedWord)) {
                endGame(true, secretWord);
                return;
            }
        }
    }

    private static String selectRandomWord() {
        return WORDS.get(random.nextInt(WORDS.size()));
    }

    private static void printCurrentState(int errorsMade, StringBuilder maskedWord,
                                          Set<Character> incorrectGuesses) {
        printHangmanStage(errorsMade);
        System.out.println("Слово: " + maskedWord);

        if (!incorrectGuesses.isEmpty()) {
            String incorrectStr = incorrectGuesses.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            System.out.println("Ошибки (" + incorrectGuesses.size() + "): " + incorrectStr);
        }
    }

    private static char promptForLetter() {
        while (true) {
            System.out.print("Введите букву: ");
            String input = scanner.nextLine().toUpperCase();

            if (input.length() == 1) {
                char letter = input.charAt(0);
                if (letter >= 'А' && letter <= 'Я') {
                    return letter;
                }
            }
            System.out.println("Пожалуйста, введите одну русскую букву!");
        }
    }

    private static boolean isLetterAlreadyGuessed(StringBuilder maskedWord,
                                                  Set<Character> incorrectGuesses,
                                                  char letter) {
        return maskedWord.indexOf(String.valueOf(letter)) != -1 || incorrectGuesses.contains(letter);
    }

    private static boolean updateGameState(String secretWord, StringBuilder maskedWord,
                                           Set<Character> incorrectGuesses, char letter) {
        boolean letterFound = false;

        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                maskedWord.setCharAt(i, letter);
                letterFound = true;
            }
        }

        if (!letterFound) {
            incorrectGuesses.add(letter);
            return true;
        }
        return false;
    }

    private static boolean isWordGuessed(StringBuilder maskedWord) {
        return maskedWord.indexOf("_") == -1;
    }

    private static void printHangmanStage(int stage) {
        System.out.println(HANGMAN_STAGES[stage]);
    }

    private static void endGame(boolean isWin, String secretWord) {
        if (isWin) {
            System.out.println("Поздравляем! Вы угадали слово: " + secretWord);
        } else {
            printHangmanStage(MAX_ERRORS);
            System.out.println("Игра окончена! Загаданное слово: " + secretWord);
        }
    }
}