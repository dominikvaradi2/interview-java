package hu.bca.library.exceptions;

import org.springframework.http.HttpStatusCode;

public class OpenLibraryException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;

    public OpenLibraryException(String message, HttpStatusCode httpStatusCode) {
        super(message);

        this.httpStatusCode = httpStatusCode;
    }
}
