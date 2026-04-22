package com.ieum.ansimdonghaeng.domain.freelancer.repository;

import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
    public Page<FreelancerProfile> findPublicFreelancers(String keyword,
                                                         String projectType,
                                                         String region,
                                                         Pageable pageable) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(keyword, projectType, region, parameters);

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
                """ + whereClause + """
                )
                WHERE rn > :offsetRow
                  AND rn <= :endRow
                ORDER BY rn
                """;

        Query query = entityManager.createNativeQuery(contentQuery, FreelancerProfile.class);
        bindParameters(query, parameters);
        query.setParameter("offsetRow", pageable.getOffset());
        query.setParameter("endRow", pageable.getOffset() + pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<FreelancerProfile> content = query.getResultList();

        String countQuery = """
                SELECT COUNT(*)
                FROM FREELANCER_PROFILE fp
                JOIN APP_USER u
                  ON u.USER_ID = fp.USER_ID
                """ + whereClause;

        Query totalQuery = entityManager.createNativeQuery(countQuery);
        bindParameters(totalQuery, parameters);

        long total = ((Number) totalQuery.getSingleResult()).longValue();
        return new PageImpl<>(content, pageable, total);
    }

    private String buildWhereClause(String keyword,
                                    String projectType,
                                    String region,
                                    Map<String, Object> parameters) {
        StringBuilder whereBuilder = new StringBuilder("""
                WHERE fp.PUBLIC_YN = 'Y'
                  AND u.ACTIVE_YN = 'Y'
                  AND u.ROLE_CODE = 'ROLE_FREELANCER'
                """);

        if (StringUtils.hasText(keyword)) {
            whereBuilder.append("""
                  AND (
                        LOWER(u.NAME) LIKE :keyword
                     OR LOWER(u.INTRO) LIKE :keyword
                     OR EXISTS (
                            SELECT 1
                            FROM FREELANCER_ACTIVITY_REGION far_keyword
                            LEFT JOIN REGION_CODE rc_keyword
                              ON rc_keyword.REGION_CODE = far_keyword.REGION_CODE
                            WHERE far_keyword.FREELANCER_PROFILE_ID = fp.FREELANCER_PROFILE_ID
                              AND (
                                    LOWER(far_keyword.REGION_CODE) LIKE :keyword
                                 OR LOWER(rc_keyword.REGION_NAME) LIKE :keyword
                              )
                        )
                     OR EXISTS (
                            SELECT 1
                            FROM FREELANCER_PROJECT_TYPE fpt_keyword
                            LEFT JOIN PROJECT_TYPE_CODE ptc_keyword
                              ON ptc_keyword.PROJECT_TYPE_CODE = fpt_keyword.PROJECT_TYPE_CODE
                            WHERE fpt_keyword.FREELANCER_PROFILE_ID = fp.FREELANCER_PROFILE_ID
                              AND (
                                    LOWER(fpt_keyword.PROJECT_TYPE_CODE) LIKE :keyword
                                 OR LOWER(ptc_keyword.PROJECT_TYPE_NAME) LIKE :keyword
                              )
                        )
                  )
                """);
            parameters.put("keyword", "%" + keyword.toLowerCase(Locale.ROOT) + "%");
        }

        if (StringUtils.hasText(projectType)) {
            whereBuilder.append("""
                  AND EXISTS (
                        SELECT 1
                        FROM FREELANCER_PROJECT_TYPE fpt
                        WHERE fpt.FREELANCER_PROFILE_ID = fp.FREELANCER_PROFILE_ID
                          AND fpt.PROJECT_TYPE_CODE = :projectType
                  )
                """);
            parameters.put("projectType", projectType);
        }

        if (StringUtils.hasText(region)) {
            whereBuilder.append("""
                  AND EXISTS (
                        SELECT 1
                        FROM FREELANCER_ACTIVITY_REGION far
                        LEFT JOIN REGION_CODE rc
                          ON rc.REGION_CODE = far.REGION_CODE
                        WHERE far.FREELANCER_PROFILE_ID = fp.FREELANCER_PROFILE_ID
                          AND (
                                far.REGION_CODE = :region
                             OR rc.PARENT_REGION_CODE = :region
                          )
                  )
                """);
            parameters.put("region", region);
        }

        return whereBuilder.toString();
    }

    private void bindParameters(Query query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
    }
}
