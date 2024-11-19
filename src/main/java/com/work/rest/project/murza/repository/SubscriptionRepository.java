package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Subscription;
import com.work.rest.project.murza.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByFollower(User follower);

    List<Subscription> findByFollowed(User followed);

    Optional<Subscription> findByFollowedAndFollower(User followed, User follower);

    boolean existsByFollowerAndFollowed(User follower, User followed);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.followed.id = :userId")
    int countFollowers(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.follower.id = :userId")
    int countFollowing(@Param("userId") Long userId);

    boolean existsByFollowerIdAndFollowedId(Long currentUserId, Long followedId);
}
