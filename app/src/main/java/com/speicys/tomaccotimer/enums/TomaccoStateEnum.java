package com.speicys.tomaccotimer.enums;

import com.speicys.tomaccotimer.R;

/**
 * A Tomacco state is a vanity feature that allows the user a visual cue for the current session's progress.
 */
public enum TomaccoStateEnum {

    STATE_3(R.drawable.tomacco_state_3, null),
    STATE_2(R.drawable.tomacco_state_2, STATE_3),
    STATE_1(R.drawable.tomacco_state_1, STATE_2),
    VANILLA(R.drawable.tomato_vanilla_small, STATE_1);

    private int drawableId;
    private TomaccoStateEnum nextStage;

    TomaccoStateEnum(int drawableId, TomaccoStateEnum nextStage) {
        this.drawableId = drawableId;
        this.nextStage = nextStage;
    }

    public void nextStage() {
        if (this.nextStage == null)
            return;

        drawableId = this.nextStage.drawableId;
        nextStage = this.nextStage.nextStage;
    }

    public int getDrawableId() {
        return this.drawableId;
    }
}
