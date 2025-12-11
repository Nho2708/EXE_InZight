package org.inzight.dto.request;

import lombok.Data;

@Data
public class ScenarioInput {
    public double income;
    public double expense;
    public String preset;
    public int projectionYears;  // Number of years to project
    
    public ScenarioInput() {
        this.projectionYears = 10; // Default 10 years
    }
}
