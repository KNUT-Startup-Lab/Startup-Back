package com.startup.campusmate.global.exceptions;

public class DuplicateRoomException extends RuntimeException {
    public DuplicateRoomException(String message) {
        super(message);
    }
}
