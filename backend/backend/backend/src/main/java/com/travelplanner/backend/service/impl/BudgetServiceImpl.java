package com.travelplanner.backend.service.impl;


import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.BudgetItem;
import com.travelplanner.backend.entity.Trip;
import com.travelplanner.backend.entity.User;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.exception.UnauthorizedAccessException;
import com.travelplanner.backend.repository.BudgetItemRepository;
import com.travelplanner.backend.repository.TripRepository;
import com.travelplanner.backend.repository.UserRepository;
import com.travelplanner.backend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public BudgetItemResponse addBudgetItem(Long tripId, String userEmail, BudgetItemRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to add budget items to this trip");
        }

        // Validate estimated amount
        if (request.getEstimatedAmount() <= 0) {
            throw new IllegalArgumentException("Estimated amount must be greater than 0");
        }

        BudgetItem item = new BudgetItem();
        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setEstimatedAmount(request.getEstimatedAmount());
        item.setActualAmount(request.getActualAmount());
        item.setPaid(request.isPaid());
        item.setPaymentMethod(request.getPaymentMethod());
        item.setNotes(request.getNotes());
        item.setTrip(trip);
        item.setUser(user);

        // Set payment date if marked as paid
        if (request.isPaid() && request.getActualAmount() != null) {
            item.setPaymentDate(LocalDateTime.now());
        }

        BudgetItem savedItem = budgetItemRepository.save(item);
        return new BudgetItemResponse(savedItem);
    }

    @Override
    @Transactional
    public BudgetItemResponse updateBudgetItem(Long itemId, String userEmail, BudgetItemRequest request) {
        BudgetItem item = budgetItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget item not found with id: " + itemId));

        // Check if user has access to the trip
        if (!hasAccessToTrip(item.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to update this budget item");
        }

        // Validate estimated amount
        if (request.getEstimatedAmount() <= 0) {
            throw new IllegalArgumentException("Estimated amount must be greater than 0");
        }

        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setEstimatedAmount(request.getEstimatedAmount());
        item.setActualAmount(request.getActualAmount());
        item.setPaid(request.isPaid());
        item.setPaymentMethod(request.getPaymentMethod());
        item.setNotes(request.getNotes());

        // Update payment date if marked as paid and has actual amount
        if (request.isPaid() && request.getActualAmount() != null && item.getPaymentDate() == null) {
            item.setPaymentDate(LocalDateTime.now());
        }

        BudgetItem updatedItem = budgetItemRepository.save(item);
        return new BudgetItemResponse(updatedItem);
    }

    @Override
    @Transactional
    public void deleteBudgetItem(Long itemId, String userEmail) {
        BudgetItem item = budgetItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget item not found with id: " + itemId));

        // Check if user has access to the trip
        if (!hasAccessToTrip(item.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this budget item");
        }

        budgetItemRepository.delete(item);
    }

    @Override
    public BudgetItemResponse getBudgetItem(Long itemId, String userEmail) {
        BudgetItem item = budgetItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget item not found with id: " + itemId));

        // Check if user has access to the trip
        if (!hasAccessToTrip(item.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view this budget item");
        }

        return new BudgetItemResponse(item);
    }

    @Override
    public List<BudgetItemResponse> getTripBudgetItems(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view budget items for this trip");
        }

        return budgetItemRepository.findByTrip(trip).stream()
                .map(BudgetItemResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetSummaryResponse getBudgetSummary(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view budget summary for this trip");
        }

        // Get all budget items for the trip
        List<BudgetItem> items = budgetItemRepository.findByTrip(trip);

        // Calculate totals
        double totalBudget = trip.getBudget() != null ? trip.getBudget() : 0.0;
        double totalEstimated = items.stream()
                .mapToDouble(BudgetItem::getEstimatedAmount)
                .sum();
        double totalActual = items.stream()
                .filter(item -> item.getActualAmount() != null)
                .mapToDouble(BudgetItem::getActualAmount)
                .sum();
        double totalVariance = totalActual - totalEstimated;
        double remainingBudget = totalBudget - totalActual;

        // Calculate percentages
        double percentageUsed = totalBudget > 0 ? (totalActual / totalBudget) * 100 : 0.0;

        // Count items
        int totalItems = items.size();
        int paidItems = (int) items.stream().filter(BudgetItem::isPaid).count();
        int unpaidItems = totalItems - paidItems;
        int itemsOverBudget = (int) items.stream()
                .filter(BudgetItem::isOverBudget)
                .count();

        // Calculate category summaries
        Map<String, List<BudgetItem>> itemsByCategory = items.stream()
                .collect(Collectors.groupingBy(BudgetItem::getCategory));

        List<BudgetSummaryResponse.CategorySummary> categorySummaries = new ArrayList<>();

        for (Map.Entry<String, List<BudgetItem>> entry : itemsByCategory.entrySet()) {
            String category = entry.getKey();
            List<BudgetItem> categoryItems = entry.getValue();

            double categoryEstimated = categoryItems.stream()
                    .mapToDouble(BudgetItem::getEstimatedAmount)
                    .sum();

            double categoryActual = categoryItems.stream()
                    .filter(item -> item.getActualAmount() != null)
                    .mapToDouble(BudgetItem::getActualAmount)
                    .sum();

            double categoryVariance = categoryActual - categoryEstimated;
            int categoryItemCount = categoryItems.size();

            BudgetSummaryResponse.CategorySummary summary = new BudgetSummaryResponse.CategorySummary();
            summary.setCategory(category);
            summary.setEstimatedAmount(categoryEstimated);
            summary.setActualAmount(categoryActual);
            summary.setVariance(categoryVariance);
            summary.setItemCount(categoryItemCount);

            categorySummaries.add(summary);
        }

        // Create response
        BudgetSummaryResponse response = new BudgetSummaryResponse();
        response.setTripId(tripId);
        response.setTripName(trip.getTripName());
        response.setTotalBudget(totalBudget);
        response.setTotalEstimated(totalEstimated);
        response.setTotalActual(totalActual);
        response.setTotalVariance(totalVariance);
        response.setRemainingBudget(remainingBudget);
        response.setPercentageUsed(percentageUsed);
        response.setTotalItems(totalItems);
        response.setPaidItems(paidItems);
        response.setUnpaidItems(unpaidItems);
        response.setItemsOverBudget(itemsOverBudget);
        response.setCategorySummaries(categorySummaries);

        return response;
    }

    @Override
    @Transactional
    public BudgetItemResponse markAsPaid(Long itemId, String userEmail, Double actualAmount, String paymentMethod) {
        BudgetItem item = budgetItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget item not found with id: " + itemId));

        // Check if user has access to the trip
        if (!hasAccessToTrip(item.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to update this budget item");
        }

        if (actualAmount != null && actualAmount <= 0) {
            throw new IllegalArgumentException("Actual amount must be greater than 0");
        }

        item.setActualAmount(actualAmount);
        item.setPaid(true);
        item.setPaymentMethod(paymentMethod);
        item.setPaymentDate(LocalDateTime.now());

        BudgetItem updatedItem = budgetItemRepository.save(item);
        return new BudgetItemResponse(updatedItem);
    }

    @Override
    @Transactional
    public BudgetItemResponse updateActualAmount(Long itemId, String userEmail, Double actualAmount) {
        BudgetItem item = budgetItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget item not found with id: " + itemId));

        // Check if user has access to the trip
        if (!hasAccessToTrip(item.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to update this budget item");
        }

        if (actualAmount != null && actualAmount <= 0) {
            throw new IllegalArgumentException("Actual amount must be greater than 0");
        }

        item.setActualAmount(actualAmount);

        // Auto-mark as paid if actual amount is set
        if (actualAmount != null && !item.isPaid()) {
            item.setPaid(true);
            item.setPaymentDate(LocalDateTime.now());
        }

        BudgetItem updatedItem = budgetItemRepository.save(item);
        return new BudgetItemResponse(updatedItem);
    }

    @Override
    public List<BudgetItemResponse> getBudgetItemsByCategory(Long tripId, String category, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view budget items for this trip");
        }

        return budgetItemRepository.findByTripAndCategory(trip, category).stream()
                .map(BudgetItemResponse::new)
                .collect(Collectors.toList());
    }

    // Helper method to check if user has access to trip
    private boolean hasAccessToTrip(Trip trip, String userEmail) {
        // Owner has access
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        // Check if user is a collaborator
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        // For budget, we'll allow any collaborator with VIEW permission or higher
        return trip.getCollaborators().stream()
                .anyMatch(c -> c.getUser().getEmail().equals(userEmail) && c.isAccepted());
    }
}
