package ru.yandex.practicum.domain;

import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.game.WordNotFoundInDictionaryException;
import ru.yandex.practicum.exception.game.BaseWordleGameException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WordleGame {

    private static final int MAX_STEPS = 6;

    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final String answer;

    private final List<Attempt> attempts = new ArrayList<>();
    private final Set<String> guessedWords = new HashSet<>();
    private final Set<String> hintedWords = new HashSet<>();

    private int remainingSteps;
    private boolean won;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this(dictionary, log, pickAnswer(dictionary));
    }

    private static String pickAnswer(WordleDictionary dictionary) {
        return Objects.requireNonNull(dictionary, "Словарь не должен быть null").getRandomWord();
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        Objects.requireNonNull(dictionary, "Словарь не должен быть null");
        Objects.requireNonNull(log, "Лог не должен быть null");

        final String normalizedAnswer = WordleDictionary.normalize(answer);
        if (!dictionary.contains(normalizedAnswer)) {
            throw new IllegalArgumentException("Ответа нет в словаре");
        }

        this.dictionary = dictionary;
        this.log = log;
        this.answer = normalizedAnswer;
        this.remainingSteps = MAX_STEPS;

        log.println("Игра начата");
        log.println("Осталось шагов: " + remainingSteps);
    }

    public String makeMove(String input) throws BaseWordleGameException {
        ensureGameIsActive();

        final String word = WordleDictionary.normalize(input);
        validateWord(word);

        final String result = dictionary.compare(word, answer);
        attempts.add(new Attempt(word, result));
        guessedWords.add(word);

        remainingSteps--;

        if (word.equals(answer)) {
            won = true;
        }

        log.println("Ход: " + word);
        log.println("Результат: " + result);
        log.println("Осталось шагов: " + remainingSteps);

        return result;
    }

    private void ensureGameIsActive() {
        if (isFinished()) {
            throw new IllegalStateException("Игра уже завершена");
        }
    }

    private void validateWord(String word) throws BaseWordleGameException {
        if (!WordleDictionary.isValidWord(word)) {
            throw new InvalidWordException("Слово должно состоять из 5 русских букв");
        }
        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionaryException("Слова нет в словаре: " + word);
        }
    }

    public String getHint() {
        ensureGameIsActive();

        final String hint = dictionary.getWords().stream()
                .filter(candidate -> !guessedWords.contains(candidate) && !hintedWords.contains(candidate))
                .filter(this::isPossibleAnswer)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Не найдено подходящее слово для подсказки"));

        hintedWords.add(hint);
        log.println("Подсказка: " + hint);
        return hint;
    }

    private boolean isPossibleAnswer(String candidate) {
        return attempts.stream()
                .allMatch(attempt ->
                        dictionary
                                .compare(attempt.word(), candidate)
                                .equals(attempt.result())
                );
    }

    public boolean isWon() {
        return won;
    }

    public boolean isLost() {
        return !won && remainingSteps == 0;
    }

    public boolean isFinished() {
        return won || remainingSteps == 0;
    }

    public int getRemainingSteps() {
        return remainingSteps;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getGuesses() {
        return attempts.stream()
                .map(Attempt::word)
                .toList();
    }

    private record Attempt(String word, String result) {
    }
}
