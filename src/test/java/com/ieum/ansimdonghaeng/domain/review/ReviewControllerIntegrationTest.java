package com.ieum.ansimdonghaeng.domain.review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import com.ieum.ansimdonghaeng.domain.project.entity.Project;
import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.report.entity.ReportReasonType;
import com.ieum.ansimdonghaeng.domain.user.entity.User;
import com.ieum.ansimdonghaeng.domain.user.entity.UserRole;
import com.ieum.ansimdonghaeng.support.AdminIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerIntegrationTest extends AdminIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("완료된 프로젝트에 대해 리뷰를 작성하고 수정 삭제할 수 있다")
    void createUpdateDeleteReviewSuccess() throws Exception {
        User owner = saveUser("owner-review@test.com", "owner", UserRole.USER);
        User freelancerUser = saveUser("freelancer-review@test.com", "freelancer", UserRole.FREELANCER);
        FreelancerProfile freelancerProfile = saveFreelancerProfile(freelancerUser, true, true);
        Project project = saveProject(owner, ProjectStatus.COMPLETED);
        saveAcceptedProposal(project, freelancerProfile);

        mockMvc.perform(post("/api/v1/projects/{projectId}/reviews", project.getId())
                        .with(userPrincipal(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "rating", 5,
                                "content", "great support",
                                "tag", "kind"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.projectId").value(project.getId()))
                .andExpect(jsonPath("$.data.reviewerUserId").value(owner.getId()))
                .andExpect(jsonPath("$.data.rating").value(5));

        Long reviewId = reviewRepository.findByProject_Id(project.getId()).orElseThrow().getId();

        mockMvc.perform(patch("/api/v1/reviews/{reviewId}", reviewId)
                        .with(userPrincipal(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "rating", 4,
                                "content", "updated review"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rating").value(4))
                .andExpect(jsonPath("$.data.content").value("updated review"));

        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId)
                        .with(userPrincipal(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true));
    }

    @Test
    @DisplayName("리뷰 목록과 작성 가능 여부를 조회할 수 있다")
    void listReviewsAndEligibilitySuccess() throws Exception {
        User owner = saveUser("owner-list-review@test.com", "owner", UserRole.USER);
        User freelancerUser = saveUser("freelancer-list-review@test.com", "freelancer", UserRole.FREELANCER);
        FreelancerProfile freelancerProfile = saveFreelancerProfile(freelancerUser, true, true);
        Project project = saveProject(owner, ProjectStatus.COMPLETED);
        saveAcceptedProposal(project, freelancerProfile);
        saveReview(project, 5, false);

        mockMvc.perform(get("/api/v1/freelancers/{freelancerProfileId}/reviews", freelancerProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.aggregate.totalReviews").value(1))
                .andExpect(jsonPath("$.data.aggregate.averageRating").value(5.00));

        mockMvc.perform(get("/api/v1/projects/{projectId}/reviews/eligibility", project.getId())
                        .with(userPrincipal(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.canWrite").value(false))
                .andExpect(jsonPath("$.data.reason").value("REVIEW_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("리뷰 신고는 중복 없이 생성된다")
    void createReportSuccess() throws Exception {
        User owner = saveUser("owner-report@test.com", "owner", UserRole.USER);
        User freelancerUser = saveUser("freelancer-report@test.com", "freelancer", UserRole.FREELANCER);
        User reporter = saveUser("reporter@test.com", "reporter", UserRole.USER);
        FreelancerProfile freelancerProfile = saveFreelancerProfile(freelancerUser, true, true);
        Project project = saveProject(owner, ProjectStatus.COMPLETED);
        saveAcceptedProposal(project, freelancerProfile);
        Long reviewId = saveReview(project, 2, false).getId();

        mockMvc.perform(post("/api/v1/reviews/{reviewId}/reports", reviewId)
                        .with(userPrincipal(reporter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "reasonType", ReportReasonType.SPAM.name(),
                                "reasonDetail", "spam review"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.reviewId").value(reviewId))
                .andExpect(jsonPath("$.data.reasonType").value("SPAM"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        mockMvc.perform(post("/api/v1/reviews/{reviewId}/reports", reviewId)
                        .with(userPrincipal(reporter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "reasonType", ReportReasonType.SPAM.name()
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("REPORT_409_2"));
    }
}
