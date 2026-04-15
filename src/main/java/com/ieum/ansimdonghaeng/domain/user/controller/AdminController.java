package com.ieum.ansimdonghaeng.domain.user.controller;

import com.ieum.ansimdonghaeng.common.response.ApiResponse;
import com.ieum.ansimdonghaeng.domain.user.dto.response.AccessPolicyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/access-check")
    public ResponseEntity<ApiResponse<AccessPolicyResponse>> accessCheck(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(new AccessPolicyResponse(
                "admin",
                authentication.getAuthorities().iterator().next().getAuthority(),
                "Admin-only endpoint is accessible."
        )));
    }
}
