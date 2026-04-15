package com.ieum.ansimdonghaeng.domain.freelancer.controller;

import com.ieum.ansimdonghaeng.common.response.ApiResponse;
import com.ieum.ansimdonghaeng.domain.user.dto.response.AccessPolicyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/freelancers/me")
public class FreelancerWorkspaceController {

    @GetMapping("/workspace")
    public ResponseEntity<ApiResponse<AccessPolicyResponse>> workspace(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(new AccessPolicyResponse(
                "freelancer",
                authentication.getAuthorities().iterator().next().getAuthority(),
                "Freelancer workspace is accessible."
        )));
    }
}
