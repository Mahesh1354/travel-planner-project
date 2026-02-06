package com.travelplanner.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetItemRequest {

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Estimated amount is required")
    private Double estimatedAmount;

    private Double actualAmount;

    private boolean isPaid = false;

    private String paymentMethod;

    private String notes;
}
