package com.travelplanner.backend.repository;
import com.travelplanner.backend.entity.OfflineCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfflineCacheRepository extends JpaRepository<OfflineCache, Long> {

    // Find cache by key and user
    Optional<OfflineCache> findByCacheKeyAndUserId(String cacheKey, Long userId);

    // Find all cache entries for a user
    List<OfflineCache> findByUserId(Long userId);

    // Find cache by data type and user
    List<OfflineCache> findByDataTypeAndUserId(String dataType, Long userId);

    // Find cache that needs sync (dirty or expired)
    @Query("SELECT oc FROM OfflineCache oc WHERE oc.userId = :userId AND " +
            "(oc.isDirty = true OR oc.expiresAt < :now)")
    List<OfflineCache> findCacheNeedingSync(@Param("userId") Long userId,
                                            @Param("now") LocalDateTime now);

    // Find cache by device
    List<OfflineCache> findByUserIdAndDeviceId(Long userId, String deviceId);

    // Mark cache as clean (synced)
    @Modifying
    @Transactional
    @Query("UPDATE OfflineCache oc SET oc.isDirty = false, oc.lastSynced = :now " +
            "WHERE oc.id = :cacheId")
    void markAsSynced(@Param("cacheId") Long cacheId, @Param("now") LocalDateTime now);

    // Delete expired cache entries
    @Modifying
    @Transactional
    @Query("DELETE FROM OfflineCache oc WHERE oc.expiresAt < :now")
    void deleteExpiredCache(@Param("now") LocalDateTime now);

    // Delete cache by user and device
    @Modifying
    @Transactional
    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
}