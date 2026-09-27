package ru.yandex.practicum.exception.game;

import java.io.Serial;

public class InvalidWordException extends BaseWordleGameException {

    @Serial
    private static final long serialVersionUID = -162738461511476790L;

    public InvalidWordException(String message) {
        super(message);
    }
}
