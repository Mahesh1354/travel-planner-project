package com.travelplanner.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Category is required")
    @Column(nullable = false)
    private String category; // TRANSPORTATION, ACCOMMODATION, FOOD, ACTIVITIES, SHOPPING, OTHER

    @NotNull(message = "Estimated amount is required")
    @Column(name = "estimated_amount", nullable = false)
    private Double estimatedAmount;

    @Column(name = "actual_amount")
    private Double actualAmount;

    @Column(name = "is_paid")
    private boolean isPaid = false;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to calculate variance
    public Double getVariance() {
        if (actualAmount == null) {
            return null;
        }
        return actualAmount - estimatedAmount;
    }

    // Helper method to check if over budget
    public boolean isOverBudget() {
        if (actualAmount == null) {
            return false;
        }
        return actualAmount > estimatedAmount;
    }
}