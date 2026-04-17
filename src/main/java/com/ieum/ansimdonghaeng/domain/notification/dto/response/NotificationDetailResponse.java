package com.ieum.ansimdonghaeng.domain.notification.dto.response;

import com.ieum.ansimdonghaeng.domain.notification.entity.Notification;
import java.time.LocalDateTime;

public record NotificationDetailResponse(
        Long notificationId,
        String notificationType,
        String title,
        String content,
        Boolean isRead,
        Long relatedProjectId,
        Long relatedProposalId,
        Long relatedReviewId,
        Long relatedNoticeId,
        Long relatedVerificationId,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {

    public static NotificationDetailResponse from(Notification notification) {
        return new NotificationDetailResponse(
                notification.getId(),
                notification.getNotificationType().name(),
                notification.getTitle(),
                notification.getContent(),
                notification.getReadYn(),
                notification.getRelatedProjectId(),
                notification.getRelatedProposalId(),
                notification.getRelatedReviewId(),
                notification.getRelatedNoticeId(),
                notification.getRelatedVerificationId(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}
