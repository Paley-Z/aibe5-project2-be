package com.ieum.ansimdonghaeng.domain.freelancer.dto.response;

import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public record FreelancerSummaryResponse(
        Long freelancerProfileId,
        Long userId,
        String name,
        String intro,
        Boolean caregiverYn,
        Boolean verifiedYn,
        BigDecimal averageRating,
        Long activityCount,
        List<String> activityRegionCodes,
        List<String> projectTypeCodes
) {

    // 목록 응답에서는 프리랜서 검색에 필요한 공개 정보만 요약해서 내려준다.
    public static FreelancerSummaryResponse from(FreelancerProfile profile) {
        return new FreelancerSummaryResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getName(),
                profile.getUser().getIntro(),
                profile.getCaregiverYn(),
                profile.getVerifiedYn(),
                profile.getAverageRating(),
                profile.getActivityCount(),
                profile.getActivityRegionCodes().stream().sorted(Comparator.naturalOrder()).toList(),
                profile.getProjectTypeCodes().stream().sorted(Comparator.naturalOrder()).toList()
        );
    }
}
