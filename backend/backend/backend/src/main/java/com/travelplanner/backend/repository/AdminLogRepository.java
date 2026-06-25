package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    // Find logs by admin with pagination
    Page<AdminLog> findByAdminId(Long adminId, Pageable pageable);

    // Find logs by action type with pagination
    Page<AdminLog> findByActionType(String actionType, Pageable pageable);

    // Find logs by affected user with pagination
    Page<AdminLog> findByAffectedUserId(Long affectedUserId, Pageable pageable);

    // Find logs by status with pagination
    Page<AdminLog> findByStatus(String status, Pageable pageable);

    // Find logs by date range with pagination
    Page<AdminLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Find logs by multiple criteria
    @Query("SELECT al FROM AdminLog al WHERE " +
            "(:actionType IS NULL OR al.actionType = :actionType) AND " +
            "(:status IS NULL OR al.status = :status) AND " +
            "(:adminId IS NULL OR al.adminId = :adminId) AND " +
            "(:affectedUserId IS NULL OR al.affectedUserId = :affectedUserId)")
    Page<AdminLog> findLogsByCriteria(
            @Param("actionType") String actionType,
            @Param("status") String status,
            @Param("adminId") Long adminId,
            @Param("affectedUserId") Long affectedUserId,
            Pageable pageable);

    // Get recent logs
    List<AdminLog> findTop100ByOrderByCreatedAtDesc();

    // Get statistics
    @Query("SELECT COUNT(al) FROM AdminLog al WHERE al.createdAt >= :startDate")
    Long countLogsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT al.actionType, COUNT(al) FROM AdminLog al WHERE al.createdAt >= :startDate GROUP BY al.actionType")
    List<Object[]> getActionTypeCounts(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT DATE(al.createdAt), COUNT(al) FROM AdminLog al " +
            "WHERE al.createdAt >= :startDate GROUP BY DATE(al.createdAt)")
    List<Object[]> getLogsPerDay(@Param("startDate") LocalDateTime startDate);

    // Delete old logs
    @Query("DELETE FROM AdminLog al WHERE al.createdAt < :cutoffDate")
    void deleteOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
}