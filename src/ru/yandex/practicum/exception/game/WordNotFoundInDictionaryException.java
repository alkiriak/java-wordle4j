package ru.yandex.practicum.exception.game;

import java.io.Serial;

public class WordNotFoundInDictionaryException extends BaseWordleGameException {

    @Serial
    private static final long serialVersionUID = 4084262715075428386L;

    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}
