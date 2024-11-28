package com.profiling.profiling_project.LPS;

public class LPS {
    private String timestamp;
    private User user;

    // Getters et setters
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private Action action;
    private Event event;



    public static class Builder {
        private String timestamp;
        private User user;
        private Action action;
        private Event event;

        public Builder withTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withAction(Action action) {
            this.action = action;
            return this;
        }

        public Builder withEvent(Event event) {
            this.event = event;
            return this;
        }

        public LPS build() {
            LPS lps = new LPS();
            lps.timestamp = this.timestamp;
            lps.user = this.user;
            lps.action = this.action;
            lps.event = this.event;
            return lps;
        }
    }
}

