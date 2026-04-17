package com.ieum.ansimdonghaeng.domain.notification.dto.response;

public record NotificationReadResponse(
        Long notificationId,
        Boolean isRead
) {
}
