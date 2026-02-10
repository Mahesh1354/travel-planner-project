package com.travelplanner.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetItemResponse {
    private Long id;
    private String itemName;
    private String description;
    private String category;
    private Double estimatedAmount;
    private Double actualAmount;
    private Double variance;
    private boolean isPaid;
    private boolean isOverBudget;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long tripId;
    private UserResponse user;

    // Constructor from BudgetItem entity
    public BudgetItemResponse(com.travelplanner.backend.entity.BudgetItem item) {
        this.id = item.getId();
        this.itemName = item.getItemName();
        this.description = item.getDescription();
        this.category = item.getCategory();
        this.estimatedAmount = item.getEstimatedAmount();
        this.actualAmount = item.getActualAmount();
        this.variance = item.getVariance();
        this.isPaid = item.isPaid();
        this.isOverBudget = item.isOverBudget();
        this.paymentDate = item.getPaymentDate();
        this.paymentMethod = item.getPaymentMethod();
        this.notes = item.getNotes();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        this.tripId = item.getTrip().getId();
        this.user = new UserResponse(item.getUser());
    }
}