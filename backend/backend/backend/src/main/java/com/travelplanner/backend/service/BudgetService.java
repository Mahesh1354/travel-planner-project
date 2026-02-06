package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.BudgetItemResponse;
import com.travelplanner.backend.dto.BudgetItemRequest;
import com.travelplanner.backend.dto.BudgetSummaryResponse;
import java.util.List;

public interface BudgetService {

    // Budget item operations
    BudgetItemResponse addBudgetItem(Long tripId, String userEmail, BudgetItemRequest request);
    BudgetItemResponse updateBudgetItem(Long itemId, String userEmail, BudgetItemRequest request);
    void deleteBudgetItem(Long itemId, String userEmail);
    BudgetItemResponse getBudgetItem(Long itemId, String userEmail);
    List<BudgetItemResponse> getTripBudgetItems(Long tripId, String userEmail);

    // Budget summary operations
    BudgetSummaryResponse getBudgetSummary(Long tripId, String userEmail);

    // Mark item as paid
    BudgetItemResponse markAsPaid(Long itemId, String userEmail, Double actualAmount, String paymentMethod);

    // Update actual amount
    BudgetItemResponse updateActualAmount(Long itemId, String userEmail, Double actualAmount);

    // Get budget items by category
    List<BudgetItemResponse> getBudgetItemsByCategory(Long tripId, String category, String userEmail);
}
