package com.travelplanner.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSummaryResponse {
    private Long tripId;
    private String tripName;
    private Double totalBudget;
    private Double totalEstimated;
    private Double totalActual;
    private Double totalVariance;
    private Double remainingBudget;
    private Double percentageUsed;
    private Integer totalItems;
    private Integer paidItems;
    private Integer unpaidItems;
    private Integer itemsOverBudget;
    private List<CategorySummary> categorySummaries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummary {
        private String category;
        private Double estimatedAmount;
        private Double actualAmount;
        private Double variance;
        private Integer itemCount;
    }
}
