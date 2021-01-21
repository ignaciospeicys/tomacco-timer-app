package com.speicys.tomaccotimer.enums;

public enum ClockStateEnum {

    NOT_STARTED(0),
    STARTED(1),
    PAUSED(2);

    private int state;

    private ClockStateEnum(int state) {
        this.state = state;
    }

    public ClockStateEnum getState() {
        ClockStateEnum result = null;
        for (ClockStateEnum st : ClockStateEnum.values()) {
            if (this.state == st.state) {
                result = st;
                break;
            }
        }
        if (result == null)
            throw new IllegalStateException("Invalid state value");
        return result;
    }
}
