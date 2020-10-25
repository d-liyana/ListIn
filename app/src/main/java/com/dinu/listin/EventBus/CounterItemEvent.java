package com.dinu.listin.EventBus;

public class CounterItemEvent {
    private boolean success;

    public CounterItemEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
