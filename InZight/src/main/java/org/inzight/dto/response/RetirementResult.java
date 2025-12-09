package org.inzight.dto.response;

import lombok.Data;

@Data
public class RetirementResult {

    public double futureValue;
    public int sustainableYears;
    public RetirementResult(double futureValue, int sustainableYears) {
        this.futureValue = futureValue;
        this.sustainableYears = sustainableYears;
    }
}
