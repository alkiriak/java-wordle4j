package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.domain.WordleDictionary;
import ru.yandex.practicum.domain.WordleGame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleTest {

    private InputStream originalIn;
    private PrintStream originalOut;

    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        originalOut = System.out;

        output = new ByteArrayOutputStream();

        System.setOut(
                new PrintStream(
                        output,
                        true,
                        StandardCharsets.UTF_8
                )
        );
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Игра выводит результат и сообщение о победе")
    void playShouldPrintCorrectResultAndWinMessage() {
        // ARRANGE
        WordleDictionary dictionary = new WordleDictionary(
                List.of(
                        "балка",
                        "банка",
                        "бабка"
                )
        );
        WordleGame game = new WordleGame(
                dictionary,
                new PrintWriter(
                        new ByteArrayOutputStream()
                ),
                "балка"
        );
        System.setIn(new ByteArrayInputStream("балка\n".getBytes(StandardCharsets.UTF_8)));

        // ACT
        Wordle.play(game);

        // ASSERT
        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("> "));
        assertTrue(result.contains("балка"));
        assertTrue(result.contains("+++++"));
        assertTrue(result.contains("Вы угадали слово!"));
        assertTrue(result.contains("Загаданное слово: балка"));
    }

    @Test
    @DisplayName("Неверное слово не тратит попытку, игра продолжается")
    void invalidWordShouldNotConsumeAttempt() {
        // ARRANGE
        WordleDictionary dictionary = new WordleDictionary(
                List.of(
                        "балка",
                        "банка",
                        "бабка"
                )
        );
        WordleGame game = new WordleGame(
                dictionary,
                new PrintWriter(
                        new ByteArrayOutputStream()
                ),
                "балка"
        );
        System.setIn(new ByteArrayInputStream("дом\nбалка\n".getBytes(StandardCharsets.UTF_8)));

        // ACT
        Wordle.play(game);

        // ASSERT
        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Слово должно состоять из 5 русских букв"));
        assertTrue(result.contains("+++++"));
        assertTrue(result.contains("Вы угадали слово!"));
    }

    @Test
    @DisplayName("Пустой ввод автоматически делает ход подсказкой")
    void emptyInputShouldMakeAutomaticMove() {
        // ARRANGE
        WordleDictionary dictionary = new WordleDictionary(
                List.of(
                        "балка",
                        "банка",
                        "бабка"
                )
        );
        WordleGame game = new WordleGame(
                dictionary,
                new PrintWriter(
                        new ByteArrayOutputStream()
                ),
                "балка"
        );
        System.setIn(new ByteArrayInputStream("\n\n\n\n\n\n".getBytes(StandardCharsets.UTF_8)));

        // ACT
        Wordle.play(game);

        // ASSERT
        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("балка"));
        assertTrue(result.contains("+++++"));
        assertTrue(result.contains("Вы угадали слово!"));
    }

    @Test
    @DisplayName("Игра решается одними нажатиями Enter")
    void playShouldSolveGameWithOnlyEnterInputs() {
        // ARRANGE
        WordleDictionary dictionary = new WordleDictionary(
                List.of(
                        "балка",
                        "банка",
                        "бабка",
                        "волна"
                )
        );
        WordleGame game = new WordleGame(
                dictionary,
                new PrintWriter(
                        new ByteArrayOutputStream()
                ),
                "банка"
        );
        System.setIn(new ByteArrayInputStream("\n\n\n\n\n\n".getBytes(StandardCharsets.UTF_8)));

        // ACT
        Wordle.play(game);

        // ASSERT
        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("+++++"));
        assertTrue(result.contains("Вы угадали слово!"));
    }

    @Test
    @DisplayName("Шесть неверных попыток выводят сообщение о проигрыше")
    void sixIncorrectAttemptsShouldPrintLossMessage() {
        // ARRANGE
        WordleDictionary dictionary = new WordleDictionary(
                List.of(
                        "балка",
                        "банка"
                )
        );
        WordleGame game = new WordleGame(
                dictionary,
                new PrintWriter(
                        new ByteArrayOutputStream()
                ),
                "балка"
        );
        System.setIn(
                new ByteArrayInputStream(
                        (
                                """
                                банка
                                банка
                                банка
                                банка
                                банка
                                банка
                                """
                        ).getBytes(StandardCharsets.UTF_8)
                )
        );

        // ACT
        Wordle.play(game);

        // ASSERT
        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Попытки закончились."));
        assertTrue(result.contains("Загаданное слово: балка"));
    }
}
