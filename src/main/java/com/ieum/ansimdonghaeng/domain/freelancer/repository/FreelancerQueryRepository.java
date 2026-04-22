package com.ieum.ansimdonghaeng.domain.freelancer.repository;

import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FreelancerQueryRepository {

    // 구형 Oracle 환경에서도 동작하도록 공개 프리랜서 목록을 커스텀 페이지 쿼리로 조회한다.
    Page<FreelancerProfile> findPublicFreelancers(String keyword,
                                                  String projectType,
                                                  String region,
                                                  Pageable pageable);
}
