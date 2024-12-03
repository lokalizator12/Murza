package com.work.rest.project.murza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresenceStatus {
    private String userId;
    private boolean online;
}
