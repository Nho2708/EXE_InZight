package org.inzight.dto.request;

import lombok.Data;

@Data
public class GoalInput {

    public String name;
    public double targetAmount;
    public double monthlySaving;
}
