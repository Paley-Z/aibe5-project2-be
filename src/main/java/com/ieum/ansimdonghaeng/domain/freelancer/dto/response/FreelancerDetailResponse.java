package com.ieum.ansimdonghaeng.domain.freelancer.dto.response;

import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record FreelancerDetailResponse(
        Long freelancerProfileId,
        Long userId,
        String name,
        String intro,
        String roleCode,
        String careerDescription,
        Boolean caregiverYn,
        Boolean verifiedYn,
        BigDecimal averageRating,
        Long activityCount,
        Boolean publicYn,
        List<String> activityRegionCodes,
        List<String> availableTimeSlotCodes,
        List<String> projectTypeCodes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // 상세 응답에서는 경력 설명과 활동 코드까지 함께 내려준다.
    public static FreelancerDetailResponse from(FreelancerProfile profile) {
        return new FreelancerDetailResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getName(),
                profile.getUser().getIntro(),
                profile.getUser().getRoleCode(),
                profile.getCareerDescription(),
                profile.getCaregiverYn(),
                profile.getVerifiedYn(),
                profile.getAverageRating(),
                profile.getActivityCount(),
                profile.getPublicYn(),
                profile.getActivityRegionCodes().stream().sorted(Comparator.naturalOrder()).toList(),
                profile.getAvailableTimeSlotCodes().stream().sorted(Comparator.naturalOrder()).toList(),
                profile.getProjectTypeCodes().stream().sorted(Comparator.naturalOrder()).toList(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
