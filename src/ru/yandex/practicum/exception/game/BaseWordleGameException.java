package ru.yandex.practicum.exception.game;

import java.io.Serial;

public class BaseWordleGameException extends Exception {

    @Serial
    private static final long serialVersionUID = 2582001493186690797L;

    public BaseWordleGameException(String message) {
        super(message);
    }
}
