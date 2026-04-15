package com.ieum.ansimdonghaeng.domain.freelancer.repository;

import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class FreelancerQueryRepositoryImpl implements FreelancerQueryRepository {

    private static final String FREELANCER_PROFILE_COLUMNS = """
            fp.FREELANCER_PROFILE_ID,
            fp.USER_ID,
            fp.CAREER_DESCRIPTION,
            fp.CAREGIVER_YN,
            fp.VERIFIED_YN,
            fp.AVERAGE_RATING,
            fp.ACTIVITY_COUNT,
            fp.PUBLIC_YN,
            fp.CREATED_AT,
            fp.UPDATED_AT
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<FreelancerProfile> findPublicFreelancers(Pageable pageable) {
        String contentQuery = """
                SELECT
                    FREELANCER_PROFILE_ID,
                    USER_ID,
                    CAREER_DESCRIPTION,
                    CAREGIVER_YN,
                    VERIFIED_YN,
                    AVERAGE_RATING,
                    ACTIVITY_COUNT,
                    PUBLIC_YN,
                    CREATED_AT,
                    UPDATED_AT
                FROM (
                    SELECT
                """ + FREELANCER_PROFILE_COLUMNS + """
                        ,
                        ROW_NUMBER() OVER (ORDER BY fp.CREATED_AT DESC) AS rn
                    FROM FREELANCER_PROFILE fp
                    JOIN APP_USER u
                      ON u.USER_ID = fp.USER_ID
                    WHERE fp.PUBLIC_YN = 'Y'
                      AND u.ACTIVE_YN = 'Y'
                      AND u.ROLE_CODE = 'ROLE_FREELANCER'
                )
                WHERE rn > :offsetRow
                  AND rn <= :endRow
                ORDER BY rn
                """;

        Query query = entityManager.createNativeQuery(contentQuery, FreelancerProfile.class);
        query.setParameter("offsetRow", pageable.getOffset());
        query.setParameter("endRow", pageable.getOffset() + pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<FreelancerProfile> content = query.getResultList();

        String countQuery = """
                SELECT COUNT(*)
                FROM FREELANCER_PROFILE fp
                JOIN APP_USER u
                  ON u.USER_ID = fp.USER_ID
                WHERE fp.PUBLIC_YN = 'Y'
                  AND u.ACTIVE_YN = 'Y'
                  AND u.ROLE_CODE = 'ROLE_FREELANCER'
                """;

        long total = ((Number) entityManager.createNativeQuery(countQuery).getSingleResult()).longValue();
        return new PageImpl<>(content, pageable, total);
    }
}
