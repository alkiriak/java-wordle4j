package ru.yandex.practicum;

import ru.yandex.practicum.exception.game.BaseWordleGameException;
import ru.yandex.practicum.domain.WordleDictionary;
import ru.yandex.practicum.domain.WordleDictionaryLoader;
import ru.yandex.practicum.domain.WordleGame;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Wordle {

    private static final String LOG_FILE_NAME = "wordle.log";
    private static final String DEFAULT_DICTIONARY_FILE = "words_ru.txt";

    public static void main(String[] args) {
        final PrintWriter log;
        try {
            log = new PrintWriter(new FileWriter(LOG_FILE_NAME, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Не удалось создать лог-файл: " + e.getMessage());
            return;
        }

        final String dictionaryFile = args.length > 0 ? args[0] : DEFAULT_DICTIONARY_FILE;

        try (log) {
            run(log, dictionaryFile);
        } catch (Exception e) {
            System.err.println("Программа завершилась с ошибкой, подробности в " + LOG_FILE_NAME);
        }
    }

    private static void run(PrintWriter log, String dictionaryFile) throws Exception {
        try {
            final WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            final WordleDictionary dictionary = loader.load(dictionaryFile);
            final WordleGame game = new WordleGame(dictionary, log);
            play(game);
        } catch (Exception e) {
            log.println("Ошибка программы: " + e.getMessage());
            e.printStackTrace(log);
            throw e;
        }
    }

    static void play(WordleGame game) {
        try (final Scanner scanner = new Scanner(System.in)) {
            while (!game.isFinished()) {
                System.out.print("> ");

                final String input;
                try {
                    input = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    // eof
                    System.out.println("До скорых встреч!");
                    return;
                }

                final String guess = input.isBlank() ? game.getHint() : input;

                try {
                    final String result = game.makeMove(guess);
                    System.out.println(WordleDictionary.normalize(guess));
                    System.out.println(result);
                } catch (BaseWordleGameException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (game.isWon()) {
            System.out.println("Вы угадали слово!");
        } else {
            System.out.println("Попытки закончились.");
        }

        System.out.println("Загаданное слово: " + game.getAnswer());
    }
}
