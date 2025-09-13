package com.startup.campusmate.global.exceptions;

public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}