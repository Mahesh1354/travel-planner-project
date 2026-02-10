package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.BudgetService;
import com.travelplanner.backend.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private JwtService jwtService;

    // Helper method to extract user email from JWT token
    private String getCurrentUserEmail(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        return jwtService.getEmailFromToken(token);
    }

    // Budget item CRUD operations
    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addBudgetItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @Valid @RequestBody BudgetItemRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BudgetItemResponse response = budgetService.addBudgetItem(tripId, userEmail, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget item added successfully", response));
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse> getTripBudgetItems(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<BudgetItemResponse> items = budgetService.getTripBudgetItems(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Budget items retrieved successfully", items));
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> getBudgetItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @PathVariable Long itemId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BudgetItemResponse item = budgetService.getBudgetItem(itemId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Budget item retrieved successfully", item));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> updateBudgetItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @PathVariable Long itemId,
            @Valid @RequestBody BudgetItemRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BudgetItemResponse response = budgetService.updateBudgetItem(itemId, userEmail, request);

        return ResponseEntity.ok(ApiResponse.success("Budget item updated successfully", response));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> deleteBudgetItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @PathVariable Long itemId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        budgetService.deleteBudgetItem(itemId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Budget item deleted successfully", null));
    }

    // Budget summary operations
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getBudgetSummary(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BudgetSummaryResponse summary = budgetService.getBudgetSummary(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Budget summary retrieved successfully", summary));
    }

    // Mark item as paid
    @PostMapping("/items/{itemId}/pay")
    public ResponseEntity<ApiResponse> markAsPaid(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @PathVariable Long itemId,
            @RequestBody MarkAsPaidRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BudgetItemResponse response = budgetService.markAsPaid(itemId, userEmail,
                request.getActualAmount(), request.getPaymentMethod());

        return ResponseEntity.ok(ApiResponse.success("Budget item marked as paid successfully", response));
    }

    // Update actual amount
    @PutMapping("/items/{itemId}/actual-amount")
    public ResponseEntity<ApiResponse> updateActualAmount(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @PathVariable Long itemId,
            @RequestBody UpdateAmountRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BudgetItemResponse response = budgetService.updateActualAmount(itemId, userEmail, request.getActualAmount());

        return ResponseEntity.ok(ApiResponse.success("Actual amount updated successfully", response));
    }

    // Get items by category
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getBudgetItemsByCategory(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @PathVariable String category) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<BudgetItemResponse> items = budgetService.getBudgetItemsByCategory(tripId, category, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Budget items retrieved by category successfully", items));
    }

    // Inner DTO classes for specific requests
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarkAsPaidRequest {
        private Double actualAmount;
        private String paymentMethod;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAmountRequest {
        private Double actualAmount;
    }
}
