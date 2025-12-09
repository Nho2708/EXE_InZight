package org.inzight.dto.response;

import lombok.Data;

@Data
public class GoalResult {
    public String name;
    public int monthsNeeded;
    public GoalResult(String name, int monthsNeeded) {
        this.name = name;
        this.monthsNeeded = monthsNeeded;
    }
}
