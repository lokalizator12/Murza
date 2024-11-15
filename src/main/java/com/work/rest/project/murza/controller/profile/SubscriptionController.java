package com.work.rest.project.murza.controller.profile;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{followedId}")
    public ResponseEntity<Void> subscribe(@PathVariable Long followedId, @AuthenticationPrincipal User currentUser) {
        subscriptionService.subscribe(currentUser.getId(), followedId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{followedId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long followedId, @AuthenticationPrincipal User currentUser) {
        subscriptionService.unsubscribe(currentUser.getId(), followedId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getFollowing(userId));
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable Long userId) {
        int followersCount = subscriptionService.getCountFollowers(userId);
        return ResponseEntity.ok(followersCount);
    }

    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Integer> getFollowingCount(@PathVariable Long userId) {
        int followingCount = subscriptionService.getCountFollowing(userId);
        return ResponseEntity.ok(followingCount);
    }
}
