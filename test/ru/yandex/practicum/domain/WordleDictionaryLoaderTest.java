package ru.yandex.practicum.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleDictionaryLoaderTest {

    private Path file;

    @BeforeEach
    void setUp() throws Exception {
        file = Files.createTempFile("words", ".txt");
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(file);
    }

    @Test
    @DisplayName("Загрузчик нормализует слова и отбрасывает неподходящие")
    void loadShouldNormalizeAndFilterWords() throws Exception {
        // ARRANGE
        Files.writeString(
                file,
                """
                БЕДРА
                бёдра
                бабка
                аба
                абажур
                а-ля фуршет
                """
        );
        StringWriter output = new StringWriter();
        PrintWriter log = new PrintWriter(output);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);

        // ACT
        WordleDictionary dictionary = loader.load(file.toString());

        // ASSERT
        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("бедра"));
        assertTrue(dictionary.contains("бабка"));
    }

    @Test
    @DisplayName("Загрузчик убирает дубликаты после нормализации")
    void loadShouldRemoveDuplicatesAfterNormalization() throws Exception {
        // ARRANGE
        Files.writeString(
                file,
                """
                БЕДРА
                бёдра
                БЕДРА
                """
        );
        WordleDictionaryLoader loader = new WordleDictionaryLoader(new PrintWriter(new StringWriter()));

        // ACT
        WordleDictionary dictionary = loader.load(file.toString());

        // ASSERT
        assertEquals(1, dictionary.size());
        assertTrue(dictionary.contains("бедра"));
    }

    @Test
    @DisplayName("Загрузчик отсеивает слова из английских букв")
    void loadShouldRejectEnglishWords() throws Exception {
        // ARRANGE
        Files.writeString(
                file,
                """
                балка
                table
                гонка
                house
                """
        );
        WordleDictionaryLoader loader = new WordleDictionaryLoader(new PrintWriter(new StringWriter()));

        // ACT
        WordleDictionary dictionary = loader.load(file.toString());

        // ASSERT
        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("балка"));
        assertTrue(dictionary.contains("гонка"));
        assertFalse(dictionary.contains("table"));
        assertFalse(dictionary.contains("house"));
    }

    @Test
    @DisplayName("Загрузчик выбрасывает исключение при отсутствии файла")
    void loadShouldThrowExceptionForMissingFile() {
        // ARRANGE
        WordleDictionaryLoader loader = new WordleDictionaryLoader(new PrintWriter(new StringWriter()));

        // ACT & ASSERT
        assertThrows(Exception.class, () -> loader.load("file-that-does-not-exist.txt"));
    }

    @Test
    @DisplayName("Загрузчик выбрасывает исключение, если в словаре нет подходящих слов")
    void loadShouldThrowExceptionForEmptyDictionary() throws Exception {
        // ARRANGE
        Files.writeString(
                file,
                """
                аба
                абажур
                а-ля фуршет
                """
        );
        WordleDictionaryLoader loader = new WordleDictionaryLoader(new PrintWriter(new StringWriter()));

        // ACT & ASSERT
        assertThrows(Exception.class, () -> loader.load(file.toString()));
    }
}
