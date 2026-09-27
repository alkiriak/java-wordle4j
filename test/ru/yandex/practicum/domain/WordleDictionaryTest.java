package ru.yandex.practicum.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleDictionaryTest {

    private final WordleDictionary dictionary = new WordleDictionary(
            List.of("балка", "банка", "бабка"),
            new Random(0)
    );

    @Test
    @DisplayName("Конструктор словаря отклоняет пустой список")
    void constructorShouldRejectEmptyList() {
        // ARRANGE
        List<String> empty = List.of();

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> new WordleDictionary(empty));
    }

    @Test
    @DisplayName("Нормализация приводит слово к игровой форме")
    void normalizeShouldConvertWordToGameForm() {
        // ACT
        String normalized = WordleDictionary.normalize("  БЁДРА  ");

        // ASSERT
        assertEquals("бедра", normalized);
    }

    @Test
    @DisplayName("Нормализация возвращает null для null")
    void normalizeShouldReturnNullForNull() {
        // ACT
        String normalized = WordleDictionary.normalize(null);

        // ASSERT
        assertNull(normalized);
    }

    @Test
    @DisplayName("Словарь находит существующее слово и не находит отсутствующее")
    void containsShouldFindExistingWord() {
        // ACT
        boolean found = dictionary.contains("балка");
        boolean missing = dictionary.contains("волна");

        // ASSERT
        assertTrue(found);
        assertFalse(missing);
    }

    @Test
    @DisplayName("Случайное слово всегда берётся из словаря")
    void getRandomWordShouldReturnWordFromDictionary() {
        // ACT
        String word = dictionary.getRandomWord();

        // ASSERT
        assertTrue(dictionary.contains(word));
    }

    @Test
    @DisplayName("Совпадающие по позициям буквы отмечаются символом +")
    void compareShouldMarkCorrectLettersWithPlus() {
        // ACT
        String result = dictionary.compare("балка", "балка");

        // ASSERT
        assertEquals("+++++", result);
    }

    @Test
    @DisplayName("Отсутствующие буквы отмечаются символом -")
    void compareShouldMarkMissingLettersWithMinus() {
        // ACT
        String result = dictionary.compare("сосна", "балка");

        // ASSERT
        assertEquals("----+", result);
    }

    @Test
    @DisplayName("Буквы не на своей позиции отмечаются символом ^")
    void compareShouldMarkMisplacedLettersWithCaret() {
        // ACT
        String result = dictionary.compare("аклаб", "балка");

        // ASSERT
        assertEquals("^^+^^", result);
    }

    @Test
    @DisplayName("Повторяющиеся буквы учитываются корректно")
    void compareShouldHandleDuplicateLettersCorrectly() {
        // ACT
        String result = dictionary.compare("бабка", "балка");

        // ASSERT
        assertEquals("++-++", result);
    }

    @Test
    @DisplayName("Одна и та же буква ответа не учитывается дважды")
    void compareShouldNotUseSameTargetLetterTwice() {
        // ACT
        String result = dictionary.compare("еероо", "герой");

        // ASSERT
        assertEquals("-+++-", result);
    }
}
