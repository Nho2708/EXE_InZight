package org.inzight.dto.request;

import lombok.Data;

@Data
public class RetirementInput {
    public double currentSavings;
    public double monthlyExpense;
    public String investmentType;
    public int ageNow;       // tuổi hiện tại
    public int retireAge;   // muốn nghỉ hưu lúc bao nhiêu tuổi

}

