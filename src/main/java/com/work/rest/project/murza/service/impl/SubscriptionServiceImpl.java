package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.dto.profile.UserSubscribeDto;
import com.work.rest.project.murza.entity.Subscription;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.SubscribeException;
import com.work.rest.project.murza.mapper.UserMapper;
import com.work.rest.project.murza.repository.SubscriptionRepository;
import com.work.rest.project.murza.service.SubscriptionService;
import com.work.rest.project.murza.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    @Override
    @Transactional
    @CacheEvict(value = {"followersCount", "followingCount"}, allEntries = true)
    public void subscribe(Long followerId, Long followedId) {
        log.info("User {} is subscribing to user {}", followerId, followedId);

        User follower = userService.getUserById(followerId);
        User followed = userService.getUserById(followedId);

        if (subscriptionRepository.existsByFollowerAndFollowed(follower, followed)) {
            throw new SubscribeException("Already subscribed: " + follower.getId() + " and " + followed.getId());
        }

        Subscription subscription = new Subscription();
        subscription.setFollower(follower);
        subscription.setFollowed(followed);

        subscriptionRepository.save(subscription);
        log.info("Subscription created successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"followersCount", "followingCount"}, key = "#followerId")
    public void unsubscribe(Long followerId, Long followedId) {
        log.info("User {} is unsubscribing from user {}", followerId, followedId);

        User follower = userService.getUserById(followerId);
        User followed = userService.getUserById(followedId);

        subscriptionRepository.findByFollowedAndFollower(followed, follower)
                .ifPresentOrElse(subscriptionRepository::delete,
                        () -> {
                            throw new SubscribeException("Subscription not found");
                        });

        log.info("Subscription removed successfully");
    }

    @Override
    public List<UserSubscribeDto> getFollowers(Long userId) {
        User user = userService.getUserById(userId);
        return subscriptionRepository.findByFollowed(user)
                .stream()
                .map(subscription -> UserMapper.toUserSubscribeDto(subscription.getFollower()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSubscribeDto> getFollowing(Long userId) {
        User user = userService.getUserById(userId);
        return subscriptionRepository.findByFollower(user)
                .stream()
                .map(subscription -> UserMapper.toUserSubscribeDto(subscription.getFollowed()))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("followersCount")
    public int getCountFollowers(Long userId) {
        userService.getUserById(userId);
        return subscriptionRepository.countFollowers(userId);
    }

    @Override
    @Cacheable("followingCount")
    public int getCountFollowing(Long userId) {
        log.info("Count following");
        userService.getUserById(userId);
        return subscriptionRepository.countFollowing(userId);
    }

    @Override
    public boolean isFollowing(Long currentUserId, Long followedId) {
        return subscriptionRepository.existsByFollowerIdAndFollowedId(currentUserId, followedId);
    }
}
