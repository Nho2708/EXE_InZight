package org.inzight.controller;


import org.inzight.dto.request.GoalInput;
import org.inzight.dto.request.RetirementInput;
import org.inzight.dto.request.ScenarioInput;
import org.inzight.dto.request.WhatIfInput;
import org.inzight.dto.response.GoalResult;
import org.inzight.dto.response.RetirementResult;
import org.inzight.dto.response.ScenarioResult;
import org.inzight.dto.response.WhatIfResult;
import org.inzight.dto.storage.MemoryDB;
import org.inzight.service.FinanceService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planfinance")
public class FinancePlannerController {
    FinanceService service = new FinanceService();


    @PostMapping("/retirement")
    public RetirementResult retirement(@RequestBody RetirementInput req) {
        RetirementResult result = service.calcRetirement(req);
        MemoryDB.retirementHistory.add(result);
        return result;
    }


    @GetMapping("/retirement")
    public List<RetirementResult> getRetirementHistory() {
        return MemoryDB.retirementHistory;
    }


    @PostMapping("/scenario")
    public ScenarioResult scenario(@RequestBody ScenarioInput req) {
        ScenarioResult res = service.calcScenario(req);
        MemoryDB.scenarioHistory.add(res);
        return res;
    }


    @GetMapping("/scenario")
    public List<ScenarioResult> getScenarioHistory() {
        return MemoryDB.scenarioHistory;
    }


    @PostMapping("/whatif")
    public WhatIfResult whatIf(@RequestBody WhatIfInput req) {
        WhatIfResult r = service.calcWhatIf(req);
        MemoryDB.whatIfHistory.add(r);
        return r;
    }


    @GetMapping("/whatif")
    public List<WhatIfResult> getWhatIfHistory() {
        return MemoryDB.whatIfHistory;
    }


    @PostMapping("/goals")
    public GoalResult goals(@RequestBody GoalInput req) {
        GoalResult g = service.calcGoal(req);
        MemoryDB.goalHistory.add(g);
        return g;
    }


    @GetMapping("/goals")
    public List<GoalResult> getGoalHistory() {
        return MemoryDB.goalHistory;
    }
}
