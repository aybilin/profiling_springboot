package com.profiling.profiling_project.LPS;

public class Event {
    private String status;
    private String message;

    // Constructeur, getters et setters
    public Event(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

