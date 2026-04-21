package com.ieum.ansimdonghaeng.domain.freelancer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ieum.ansimdonghaeng.domain.freelancer.entity.FreelancerProfile;
import com.ieum.ansimdonghaeng.domain.freelancer.repository.FreelancerProfileRepository;
import com.ieum.ansimdonghaeng.domain.project.repository.ProjectRepository;
import com.ieum.ansimdonghaeng.domain.proposal.repository.ProposalRepository;
import com.ieum.ansimdonghaeng.domain.user.entity.User;
import com.ieum.ansimdonghaeng.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FreelancerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FreelancerProfileRepository freelancerProfileRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        proposalRepository.deleteAll();
        projectRepository.deleteAll();
        freelancerProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("public freelancer list is available without authentication")
    void getFreelancersWithoutAuthentication() throws Exception {
        User publicFreelancer = userRepository.save(createUser("freelancer1@test.com", "Public Freelancer", "ROLE_FREELANCER"));
        User privateFreelancer = userRepository.save(createUser("freelancer2@test.com", "Private Freelancer", "ROLE_FREELANCER"));

        FreelancerProfile publicProfile = freelancerProfileRepository.save(createProfile(publicFreelancer, true));
        freelancerProfileRepository.save(createProfile(privateFreelancer, false));

        mockMvc.perform(get("/api/v1/freelancers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].freelancerProfileId").value(publicProfile.getId()))
                .andExpect(jsonPath("$.data.content[0].name").value("Public Freelancer"))
                .andExpect(jsonPath("$.data.content[0].userId").doesNotExist());
    }

    @Test
    @DisplayName("public freelancer list searches keyword before pagination")
    void getFreelancersSearchesKeywordBeforePagination() throws Exception {
        User otherFreelancer = userRepository.save(createUser(
                "other@test.com",
                "Other Freelancer",
                "ROLE_FREELANCER",
                "ordinary intro"
        ));
        User matchingFreelancer = userRepository.save(createUser(
                "matching@test.com",
                "Matching Freelancer",
                "ROLE_FREELANCER",
                "elder companion specialist"
        ));

        freelancerProfileRepository.save(createProfile(
                otherFreelancer,
                true,
                Set.of("BUSAN_HAEUNDAE"),
                Set.of("HOUSEKEEPING")
        ));
        FreelancerProfile matchingProfile = freelancerProfileRepository.save(createProfile(matchingFreelancer, true));

        mockMvc.perform(get("/api/v1/freelancers")
                        .param("keyword", "elder")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].freelancerProfileId").value(matchingProfile.getId()))
                .andExpect(jsonPath("$.data.content[0].name").value("Matching Freelancer"));
    }

    @Test
    @DisplayName("public freelancer list filters by project type and region")
    void getFreelancersFiltersByProjectTypeAndRegion() throws Exception {
        User matchingFreelancer = userRepository.save(createUser("matching-filter@test.com", "Matching Filter", "ROLE_FREELANCER"));
        User typeOnlyFreelancer = userRepository.save(createUser("type-only@test.com", "Type Only", "ROLE_FREELANCER"));
        User regionOnlyFreelancer = userRepository.save(createUser("region-only@test.com", "Region Only", "ROLE_FREELANCER"));

        FreelancerProfile matchingProfile = freelancerProfileRepository.save(createProfile(
                matchingFreelancer,
                true,
                Set.of("SEOUL_GANGNAM"),
                Set.of("HOSPITAL_COMPANION")
        ));
        freelancerProfileRepository.save(createProfile(
                typeOnlyFreelancer,
                true,
                Set.of("BUSAN_HAEUNDAE"),
                Set.of("HOSPITAL_COMPANION")
        ));
        freelancerProfileRepository.save(createProfile(
                regionOnlyFreelancer,
                true,
                Set.of("SEOUL_GANGNAM"),
                Set.of("HOUSEKEEPING")
        ));

        mockMvc.perform(get("/api/v1/freelancers")
                        .param("projectType", "HOSPITAL_COMPANION")
                        .param("region", "SEOUL_GANGNAM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].freelancerProfileId").value(matchingProfile.getId()));
    }

    @Test
    @DisplayName("public freelancer detail is available without authentication")
    void getFreelancerDetailWithoutAuthentication() throws Exception {
        User freelancer = userRepository.save(createUser("detail@test.com", "Detail Freelancer", "ROLE_FREELANCER"));
        FreelancerProfile profile = freelancerProfileRepository.save(createProfile(freelancer, true));

        mockMvc.perform(get("/api/v1/freelancers/{freelancerProfileId}", profile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.freelancerProfileId").value(profile.getId()))
                .andExpect(jsonPath("$.data.name").value("Detail Freelancer"))
                .andExpect(jsonPath("$.data.userId").doesNotExist())
                .andExpect(jsonPath("$.data.publicYn").doesNotExist())
                .andExpect(jsonPath("$.data.roleCode").doesNotExist())
                .andExpect(jsonPath("$.data.createdAt").doesNotExist())
                .andExpect(jsonPath("$.data.updatedAt").doesNotExist());
    }

    @Test
    @DisplayName("private freelancer detail is not available publicly")
    void getFreelancerDetailFailsForPrivateProfile() throws Exception {
        User freelancer = userRepository.save(createUser("private@test.com", "Private Freelancer", "ROLE_FREELANCER"));
        FreelancerProfile profile = freelancerProfileRepository.save(createProfile(freelancer, false));

        mockMvc.perform(get("/api/v1/freelancers/{freelancerProfileId}", profile.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FREELANCER_404_1"));
    }

    private User createUser(String email, String name, String roleCode) {
        return createUser(email, name, roleCode, "intro");
    }

    private User createUser(String email, String name, String roleCode, String intro) {
        return User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("1234"))
                .name(name)
                .phone("010-0000-0000")
                .intro(intro)
                .roleCode(roleCode)
                .activeYn(true)
                .build();
    }

    private FreelancerProfile createProfile(User user, boolean publicYn) {
        return createProfile(
                user,
                publicYn,
                Set.of("SEOUL_GANGNAM"),
                Set.of("HOSPITAL_COMPANION")
        );
    }

    private FreelancerProfile createProfile(User user,
                                            boolean publicYn,
                                            Set<String> activityRegionCodes,
                                            Set<String> projectTypeCodes) {
        return FreelancerProfile.create(
                user,
                "career description",
                true,
                true,
                new BigDecimal("4.50"),
                12L,
                publicYn,
                activityRegionCodes,
                Set.of("MORNING"),
                projectTypeCodes
        );
    }
}
