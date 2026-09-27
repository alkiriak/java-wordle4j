package ru.yandex.practicum.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {

    private static final int WORD_LENGTH = 5;

    private static final char CORRECT_LETTER = '+';
    private static final char MISPLACED_LETTER = '^';
    private static final char ABSENT_LETTER = '-';

    private final Set<String> words;
    private final Random random;

    public WordleDictionary(Collection<String> words) {
        this(words, new Random());
    }

    public WordleDictionary(Collection<String> words, Random random) {
        Objects.requireNonNull(words, "Словарь не должен быть null");
        if (words.isEmpty()) {
            throw new IllegalArgumentException("Словарь не должен быть пустым");
        }
        Objects.requireNonNull(random, "Генератор случайных чисел не должен быть null");

        this.words = Set.copyOf(words);
        this.random = random;
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        final int index = random.nextInt(words.size());
        final Iterator<String> it = words.iterator();
        String word = null;
        for (int i = 0; i <= index; i++) {
            word = it.next();
        }
        return word;
    }

    public Collection<String> getWords() {
        return words;
    }

    public int size() {
        return words.size();
    }

    public String compare(String guess, String target) {
        validateWord(guess);
        validateWord(target);

        final boolean[] usedTargetLetters = new boolean[WORD_LENGTH];
        final char[] result = new char[WORD_LENGTH];
        Arrays.fill(result, ABSENT_LETTER);

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = CORRECT_LETTER;
                usedTargetLetters[i] = true;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == CORRECT_LETTER) {
                continue;
            }

            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!usedTargetLetters[j] && guess.charAt(i) == target.charAt(j)) {
                    result[i] = MISPLACED_LETTER;
                    usedTargetLetters[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }

    public static String normalize(String word) {
        if (word == null) {
            return null;
        }

        return word
                .trim()
                .toLowerCase()
                .replace('ё', 'е');
    }

    public static boolean isValidWord(String word) {
        return word != null &&
                word.length() == WORD_LENGTH &&
                word.chars().allMatch(WordleDictionary::isRussianLetter);
    }

    private static boolean isRussianLetter(int ch) {
        return (ch >= 'А' && ch <= 'я') || ch == 'Ё' || ch == 'ё';
    }

    private static void validateWord(String word) {
        if (!isValidWord(word)) {
            throw new IllegalArgumentException("Слово должно состоять из 5 русских букв");
        }
    }
}
