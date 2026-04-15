package com.ieum.ansimdonghaeng.domain.proposal.repository;

import com.ieum.ansimdonghaeng.domain.proposal.entity.Proposal;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProposalQueryRepositoryImpl implements ProposalQueryRepository {

    private static final String PROPOSAL_COLUMNS = """
            p.PROPOSAL_ID,
            p.PROJECT_ID,
            p.FREELANCER_PROFILE_ID,
            p.STATUS_CODE,
            p.MESSAGE,
            p.RESPONDED_AT,
            p.CREATED_AT,
            p.UPDATED_AT
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Proposal> findFreelancerProposals(Long freelancerProfileId, ProposalStatus status, Pageable pageable) {
        String whereClause = buildWhereClause(status);
        String contentQuery = """
                SELECT
                    PROPOSAL_ID,
                    PROJECT_ID,
                    FREELANCER_PROFILE_ID,
                    STATUS_CODE,
                    MESSAGE,
                    RESPONDED_AT,
                    CREATED_AT,
                    UPDATED_AT
                FROM (
                    SELECT
                """ + PROPOSAL_COLUMNS + """
                        ,
                        ROW_NUMBER() OVER (ORDER BY p.CREATED_AT DESC) AS rn
                    FROM PROPOSAL p
                """ + whereClause + """
                )
                WHERE rn > :offsetRow
                  AND rn <= :endRow
                ORDER BY rn
                """;

        Query query = entityManager.createNativeQuery(contentQuery, Proposal.class);
        bindParameters(query, freelancerProfileId, status);
        query.setParameter("offsetRow", pageable.getOffset());
        query.setParameter("endRow", pageable.getOffset() + pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Proposal> content = query.getResultList();

        String countQuery = "SELECT COUNT(*) FROM PROPOSAL p " + whereClause;
        Query totalQuery = entityManager.createNativeQuery(countQuery);
        bindParameters(totalQuery, freelancerProfileId, status);
        long total = ((Number) totalQuery.getSingleResult()).longValue();

        return new PageImpl<>(content, pageable, total);
    }

    private String buildWhereClause(ProposalStatus status) {
        if (status == null) {
            return " WHERE p.FREELANCER_PROFILE_ID = :freelancerProfileId ";
        }
        return " WHERE p.FREELANCER_PROFILE_ID = :freelancerProfileId AND p.STATUS_CODE = :status ";
    }

    private void bindParameters(Query query, Long freelancerProfileId, ProposalStatus status) {
        query.setParameter("freelancerProfileId", freelancerProfileId);
        if (status != null) {
            query.setParameter("status", status.name());
        }
    }
}
