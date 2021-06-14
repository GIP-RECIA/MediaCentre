package org.esco.portlet.mediacentre.model.apiresponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.tomcat.jni.Time;

public class ApiResponse {
    // Attributs
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final long timestamp;
    private String message;
    private Object payload;

    // Constructeurs
    private ApiResponse() {
        this.timestamp = Time.now();
    }

    public ApiResponse(String message, Object payload) {
        this();
        this.message = message;
        this.payload = payload;
    }

    // Getteurs
    public long getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPayloadClass() {
        return this.payload.getClass().getSimpleName();
    }

    public Object getPayload() {
        return this.payload;
    }
}
