package com.fuzis.techtask.Transfer;

import java.io.Serializable;
import java.time.Duration;

public record CallTime(String totalTime) implements Serializable {
    public CallTime(Duration totalTime){
        this(String.format("%d:%02d:%02d",
                totalTime.toHours(),
                totalTime.toMinutesPart(),
                totalTime.toSecondsPart())
        );
    }
}
