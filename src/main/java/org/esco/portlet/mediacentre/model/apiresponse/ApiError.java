package org.esco.portlet.mediacentre.model.apiresponse;

public class ApiError {
    // Attributs
    private final Throwable exception;

    // Constructeurs
    public ApiError(Throwable exception) {
        this.exception = exception;
    }

    // Getteurs
    public String getExceptionName() {
        return this.exception.getClass().getSimpleName();
    }

    public String getExceptionMessage() {
        return this.exception.getMessage();
    }

    public String getExceptionLocalizedMessage() {
        return this.exception.getLocalizedMessage();
    }
}
