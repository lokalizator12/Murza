package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.profile.UserSubscribeDto;

import java.util.List;

public interface SubscriptionService {

    void subscribe(Long followerId, Long followedId);

    void unsubscribe(Long followerId, Long followedId);

    List<UserSubscribeDto> getFollowers(Long userId);

    List<UserSubscribeDto> getFollowing(Long userId);

    int getCountFollowers(Long userId);

    int getCountFollowing(Long userId);

    boolean isFollowing(Long id, Long followedId);
}
