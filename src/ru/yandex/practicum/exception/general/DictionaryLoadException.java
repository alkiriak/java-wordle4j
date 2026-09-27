package ru.yandex.practicum.exception.general;

import java.io.Serial;

public class DictionaryLoadException extends Exception {

    @Serial
    private static final long serialVersionUID = 556766679657122395L;

    public DictionaryLoadException(String message) {
        super(message);
    }

    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
