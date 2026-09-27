package ru.yandex.practicum.domain;

import ru.yandex.practicum.exception.general.DictionaryLoadException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) throws DictionaryLoadException {
        final Set<String> words = new HashSet<>();

        try (final Stream<String> lines = Files.lines(Path.of(fileName))) {
            lines
                    .map(WordleDictionary::normalize)
                    .filter(WordleDictionary::isValidWord)
                    .forEach(words::add);
        } catch (IOException e) {
            throw new DictionaryLoadException("Не удалось загрузить словарь: " + fileName, e);
        }

        if (words.isEmpty()) {
            throw new DictionaryLoadException("В словаре нет подходящих слов");
        }

        log.println("Загружен словарь: " + words.size() + " слов");

        return new WordleDictionary(words);
    }
}
