package com.ieum.ansimdonghaeng.domain.report.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieum.ansimdonghaeng.domain.notification.entity.NotificationType;
import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.report.entity.ReportReasonType;
import com.ieum.ansimdonghaeng.domain.report.entity.ReportStatus;
import com.ieum.ansimdonghaeng.domain.review.entity.Review;
import com.ieum.ansimdonghaeng.domain.user.entity.User;
import com.ieum.ansimdonghaeng.domain.user.entity.UserRole;
import com.ieum.ansimdonghaeng.support.AdminIntegrationTestSupport;
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
class ReportControllerIntegrationTest extends AdminIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMyReportsReturnsOnlyCurrentUserReports() throws Exception {
        User owner = saveUser("owner@test.com", "owner", UserRole.USER);
        User reporter = saveUser("reporter@test.com", "reporter", UserRole.USER);
        User otherReporter = saveUser("other@test.com", "other", UserRole.USER);
        User freelancerUser = saveUser("freelancer@test.com", "freelancer", UserRole.FREELANCER);
        var freelancerProfile = saveFreelancerProfile(freelancerUser, true, true);
        var project = saveProject(owner, ProjectStatus.COMPLETED);
        saveAcceptedProposal(project, freelancerProfile);
        Review review = saveReview(project, 4, false);
        saveReport(review, reporter, ReportReasonType.SPAM, ReportStatus.PENDING, null);
        saveReport(review, otherReporter, ReportReasonType.ABUSE, ReportStatus.PENDING, null);

        mockMvc.perform(get("/api/v1/reports/me").with(userPrincipal(reporter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].reasonType").value("SPAM"))
                .andExpect(jsonPath("$.data.content[0].review.projectTitle").value(project.getTitle()));
    }

    @Test
    void createReportNotifiesActiveAdmins() throws Exception {
        User owner = saveUser("owner-create-report@test.com", "owner", UserRole.USER);
        User reporter = saveUser("reporter-create-report@test.com", "reporter", UserRole.USER);
        User admin = saveUser("admin-create-report@test.com", "admin", UserRole.ADMIN);
        User inactiveAdmin = saveUser("inactive-admin-create-report@test.com", "inactive-admin", UserRole.ADMIN, false);
        User freelancerUser = saveUser("freelancer-create-report@test.com", "freelancer", UserRole.FREELANCER);
        var freelancerProfile = saveFreelancerProfile(freelancerUser, true, true);
        var project = saveProject(owner, ProjectStatus.COMPLETED);
        saveAcceptedProposal(project, freelancerProfile);
        Review review = saveReview(project, 4, false);

        mockMvc.perform(post("/api/v1/reviews/{reviewId}/reports", review.getId())
                        .with(userPrincipal(reporter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "reasonType", "SPAM",
                                "reasonDetail", "report detail"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.reviewId").value(review.getId()));

        java.util.List<com.ieum.ansimdonghaeng.domain.notification.entity.Notification> notifications =
                notificationRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(notifications)
                .filteredOn(notification -> notification.getUser().getId().equals(admin.getId()))
                .singleElement()
                .satisfies(notification -> {
                    org.assertj.core.api.Assertions.assertThat(notification.getNotificationType())
                            .isEqualTo(NotificationType.REVIEW_REPORTED);
                    org.assertj.core.api.Assertions.assertThat(notification.getRelatedReviewId())
                            .isEqualTo(review.getId());
                    org.assertj.core.api.Assertions.assertThat(notification.getRelatedProjectId())
                            .isEqualTo(project.getId());
                    org.assertj.core.api.Assertions.assertThat(notification.getReadYn()).isFalse();
                });
        org.assertj.core.api.Assertions.assertThat(notifications)
                .noneMatch(notification -> notification.getUser().getId().equals(inactiveAdmin.getId()));
    }
}
