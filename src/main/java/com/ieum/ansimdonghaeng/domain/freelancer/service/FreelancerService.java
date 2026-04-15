package com.ieum.ansimdonghaeng.domain.freelancer.service;

import com.ieum.ansimdonghaeng.common.exception.CustomException;
import com.ieum.ansimdonghaeng.common.exception.ErrorCode;
import com.ieum.ansimdonghaeng.domain.freelancer.dto.response.FreelancerDetailResponse;
import com.ieum.ansimdonghaeng.domain.freelancer.dto.response.FreelancerListResponse;
import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import com.ieum.ansimdonghaeng.domain.freelancer.repository.FreelancerProfileRepository;
import com.ieum.ansimdonghaeng.domain.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreelancerService {

    private final FreelancerProfileRepository freelancerProfileRepository;

    // 공개 가능한 프리랜서만 페이지 단위로 조회한다.
    public FreelancerListResponse getFreelancers(int page, int size) {
        Page<FreelancerProfile> freelancerPage =
                freelancerProfileRepository.findPublicFreelancers(PageRequest.of(page, size));
        return FreelancerListResponse.from(freelancerPage);
    }

    // 비공개 프로필이나 비활성 프리랜서는 상세 조회에서 숨긴다.
    public FreelancerDetailResponse getFreelancer(Long freelancerProfileId) {
        FreelancerProfile profile = freelancerProfileRepository.findDetailById(freelancerProfileId)
                .orElseThrow(() -> new CustomException(ErrorCode.FREELANCER_NOT_FOUND));

        validatePublicFreelancer(profile);
        return FreelancerDetailResponse.from(profile);
    }

    private void validatePublicFreelancer(FreelancerProfile profile) {
        if (!profile.isPublicProfile()
                || Boolean.FALSE.equals(profile.getUser().getActiveYn())
                || !UserRole.FREELANCER.getCode().equals(profile.getUser().getRoleCode())) {
            throw new CustomException(ErrorCode.FREELANCER_NOT_FOUND);
        }
    }
}
