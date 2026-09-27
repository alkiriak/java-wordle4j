package ru.yandex.practicum.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.game.WordNotFoundInDictionaryException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleGameTest {

    private WordleDictionary dictionary;
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        dictionary = new WordleDictionary(
                List.of(
                        "балка",
                        "банка",
                        "бабка",
                        "волна",
                        "бедра",
                        "герой",
                        "сосна"
                )
        );

        StringWriter logOutput = new StringWriter();
        log = new PrintWriter(logOutput);
    }

    @Test
    @DisplayName("Новая игра начинается с шестью шагами и не завершена")
    void gameShouldStartWithSixSteps() {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        int steps = game.getRemainingSteps();

        // ASSERT
        assertEquals(6, steps);
        assertFalse(game.isWon());
        assertFalse(game.isLost());
        assertFalse(game.isFinished());
    }

    @Test
    @DisplayName("Правильное угадывание приводит к победе и тратит один шаг")
    void correctGuessShouldWinGame() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        String result = game.makeMove("балка");

        // ASSERT
        assertEquals("+++++", result);
        assertTrue(game.isWon());
        assertFalse(game.isLost());
        assertTrue(game.isFinished());
        assertEquals(5, game.getRemainingSteps());
    }

    @Test
    @DisplayName("Верное по формату, но неверное слово тратит один шаг без победы")
    void incorrectValidGuessShouldConsumeOneStep() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        String result = game.makeMove("банка");

        // ASSERT
        assertEquals("++-++", result);
        assertEquals(5, game.getRemainingSteps());
        assertFalse(game.isWon());
        assertFalse(game.isLost());
    }

    @Test
    @DisplayName("Слово неверной длины не засчитывается как ход")
    void wordWithInvalidLengthShouldNotConsumeStep() {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT & ASSERT
        assertThrows(InvalidWordException.class, () -> game.makeMove("дом"));

        // ASSERT
        assertEquals(6, game.getRemainingSteps());
        assertTrue(game.getGuesses().isEmpty());
    }

    @Test
    @DisplayName("Слово с не-буквенными символами не засчитывается как ход")
    void wordWithNonLettersShouldNotConsumeStep() {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT & ASSERT
        assertThrows(InvalidWordException.class, () -> game.makeMove("12345"));

        // ASSERT
        assertEquals(6, game.getRemainingSteps());
        assertTrue(game.getGuesses().isEmpty());
    }

    @Test
    @DisplayName("Слово из английских букв не засчитывается как ход")
    void wordWithEnglishLettersShouldNotConsumeStep() {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT & ASSERT
        assertThrows(InvalidWordException.class, () -> game.makeMove("table"));

        // ASSERT
        assertEquals(6, game.getRemainingSteps());
        assertTrue(game.getGuesses().isEmpty());
    }

    @Test
    @DisplayName("Слово, отсутствующее в словаре, не засчитывается как ход")
    void wordAbsentFromDictionaryShouldNotConsumeStep() {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT & ASSERT
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeMove("шалаш"));

        // ASSERT
        assertEquals(6, game.getRemainingSteps());
        assertTrue(game.getGuesses().isEmpty());
    }

    @Test
    @DisplayName("Ввод нормализуется (регистр, пробелы, ё) перед ходом")
    void inputShouldBeNormalizedBeforeMove() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "бедра");

        // ACT
        String result = game.makeMove(" БЁДРА ");

        // ASSERT
        assertEquals("+++++", result);
        assertTrue(game.isWon());
        assertEquals(List.of("бедра"), game.getGuesses());
    }

    @Test
    @DisplayName("Шесть неверных попыток приводят к проигрышу")
    void sixIncorrectAttemptsShouldLoseGame() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        for (int i = 0; i < 6; i++) {
            game.makeMove("банка");
        }

        // ASSERT
        assertEquals(0, game.getRemainingSteps());
        assertFalse(game.isWon());
        assertTrue(game.isLost());
        assertTrue(game.isFinished());
    }

    @Test
    @DisplayName("Седьмой ход после проигрыша невозможен")
    void seventhMoveShouldBeImpossible() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        for (int i = 0; i < 6; i++) {
            game.makeMove("банка");
        }

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> game.makeMove("банка"));
    }

    @Test
    @DisplayName("Ход после победы невозможен")
    void moveAfterWinningShouldBeImpossible() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        game.makeMove("балка");

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> game.makeMove("банка"));
    }

    @Test
    @DisplayName("Подсказка до ходов возвращает слово из словаря")
    void getHintBeforeAnyMoveShouldReturnDictionaryWord() {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        String hint = game.getHint();

        // ASSERT
        assertTrue(dictionary.contains(hint));
    }

    @Test
    @DisplayName("Подсказка не повторяет уже угаданное слово")
    void hintShouldNotReturnAlreadyGuessedWord() throws Exception {
        // ARRANGE
        WordleDictionary local = new WordleDictionary(
                List.of(
                        "бабка",
                        "балка",
                        "банка"
                )
        );
        WordleGame game = new WordleGame(local, log, "балка");

        // ACT
        game.makeMove("бабка");
        String hint = game.getHint();

        // ASSERT
        assertNotEquals("бабка", hint);
        assertTrue(local.contains(hint));
        assertEquals("++-++", local.compare("бабка", hint));
    }

    @Test
    @DisplayName("Подсказка учитывает результат одного предыдущего хода")
    void hintShouldRespectPreviousResult() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(
                new WordleDictionary(
                        List.of(
                                "бабка",
                                "балка",
                                "волна"
                        )
                ),
                log,
                "балка"
        );

        // ACT
        game.makeMove("бабка");
        String hint = game.getHint();

        // ASSERT
        assertEquals("балка", hint);
    }

    @Test
    @DisplayName("Подсказка учитывает результаты нескольких предыдущих ходов")
    void hintShouldRespectSeveralPreviousResults() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(
                new WordleDictionary(
                        List.of(
                                "бабка",
                                "банка",
                                "балка",
                                "волна"
                        )
                ),
                log,
                "балка"
        );

        // ACT
        game.makeMove("бабка");
        game.makeMove("банка");
        String hint = game.getHint();

        // ASSERT
        assertEquals("балка", hint);
    }

    @Test
    @DisplayName("Повторные подсказки не дублируются и в итоге выдают ответ")
    void repeatedHintsShouldNotRepeatAndEventuallyReturnAnswer() {
        // ARRANGE
        WordleDictionary local = new WordleDictionary(
                List.of("банка", "бабка", "балка")
        );
        WordleGame game = new WordleGame(local, log, "балка");

        // ACT
        String first = game.getHint();
        String second = game.getHint();
        String third = game.getHint();

        // ASSERT
        assertNotEquals(first, second);
        assertNotEquals(second, third);
        assertNotEquals(first, third);
        assertTrue(local.contains(first));
        assertTrue(local.contains(second));
        assertTrue(local.contains(third));
        assertTrue(first.equals("балка") || second.equals("балка") || third.equals("балка"));
    }

    @Test
    @DisplayName("Подсказка после завершения игры выбрасывает исключение")
    void hintAfterGameFinishedShouldThrow() throws Exception {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, "балка");

        // ACT
        game.makeMove("балка");

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, game::getHint);
    }

    @Test
    @DisplayName("Ответ нормализуется в конструкторе")
    void answerShouldBeNormalizedInConstructor() {
        // ARRANGE
        WordleGame game = new WordleGame(dictionary, log, " БАЛКА ");

        // ACT
        String answer = game.getAnswer();

        // ASSERT
        assertEquals("балка", answer);
    }
}
