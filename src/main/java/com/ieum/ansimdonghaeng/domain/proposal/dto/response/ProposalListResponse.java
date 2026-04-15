package com.ieum.ansimdonghaeng.domain.proposal.dto.response;

import com.ieum.ansimdonghaeng.domain.proposal.repository.ProposalSummaryView;
import java.util.List;
import org.springframework.data.domain.Page;

public record ProposalListResponse(
        List<ProposalSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {

    // 공통 페이지 응답 형식에 맞춰 프리랜서 제안 목록 결과를 감싼다.
    public static ProposalListResponse from(Page<ProposalSummaryView> proposalPage) {
        return new ProposalListResponse(
                proposalPage.getContent().stream()
                        .map(ProposalSummaryResponse::from)
                        .toList(),
                proposalPage.getNumber(),
                proposalPage.getSize(),
                proposalPage.getTotalElements(),
                proposalPage.getTotalPages(),
                proposalPage.hasNext()
        );
    }
}
