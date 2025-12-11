package org.inzight.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class GoalResult {
    public String name;
    public int monthsNeeded;
    public int yearsNeeded;
    public double totalWithInterest;
    public double interestEarned;
    public String targetDate;
    public List<Milestone> milestones;
    public int priority;
    
    public GoalResult(String name, int monthsNeeded) {
        this.name = name;
        this.monthsNeeded = monthsNeeded;
        this.yearsNeeded = monthsNeeded / 12;
    }
    
    public GoalResult(String name, int monthsNeeded, int yearsNeeded, double totalWithInterest,
                     double interestEarned, String targetDate, List<Milestone> milestones, int priority) {
        this.name = name;
        this.monthsNeeded = monthsNeeded;
        this.yearsNeeded = yearsNeeded;
        this.totalWithInterest = totalWithInterest;
        this.interestEarned = interestEarned;
        this.targetDate = targetDate;
        this.milestones = milestones;
        this.priority = priority;
    }
    
    @Data
    public static class Milestone {
        public int month;
        public double amount;
        public double progress;
        public String description;
        
        public Milestone(int month, double amount, double progress, String description) {
            this.month = month;
            this.amount = amount;
            this.progress = progress;
            this.description = description;
        }
    }
}
