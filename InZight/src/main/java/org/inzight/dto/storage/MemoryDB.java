package org.inzight.dto.storage;

import org.inzight.dto.response.GoalResult;
import org.inzight.dto.response.RetirementResult;
import org.inzight.dto.response.ScenarioResult;
import org.inzight.dto.response.WhatIfResult;

import java.util.ArrayList;
import java.util.List;

public class MemoryDB {
    public static List<RetirementResult> retirementHistory = new ArrayList<>();
    public static List<ScenarioResult> scenarioHistory = new ArrayList<>();
    public static List<WhatIfResult> whatIfHistory = new ArrayList<>();
    public static List<GoalResult> goalHistory = new ArrayList<>();
}
