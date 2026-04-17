package com.ieum.ansimdonghaeng.domain.report.service;

import com.ieum.ansimdonghaeng.domain.report.dto.request.ReportCreateRequest;
import com.ieum.ansimdonghaeng.domain.report.dto.response.ReportCreateResponse;

public interface ReportService {

    ReportCreateResponse createReport(Long currentUserId, Long reviewId, ReportCreateRequest request);
}
