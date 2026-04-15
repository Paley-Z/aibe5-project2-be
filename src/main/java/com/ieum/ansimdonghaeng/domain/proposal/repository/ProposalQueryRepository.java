package com.ieum.ansimdonghaeng.domain.proposal.repository;

import com.ieum.ansimdonghaeng.domain.proposal.entity.Proposal;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProposalQueryRepository {

    // 구형 Oracle 환경에서도 동작하도록 프리랜서 수신 제안 목록을 커스텀 페이지 쿼리로 조회한다.
    Page<Proposal> findFreelancerProposals(Long freelancerProfileId, ProposalStatus status, Pageable pageable);
}
