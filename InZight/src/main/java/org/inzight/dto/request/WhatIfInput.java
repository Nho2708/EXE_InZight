package org.inzight.dto.request;

import lombok.Data;

@Data
public class WhatIfInput {
    public double income;
    public double expense;
    public double incomeChange;
}
