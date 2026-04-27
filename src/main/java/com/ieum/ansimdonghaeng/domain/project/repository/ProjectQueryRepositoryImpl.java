package com.ieum.ansimdonghaeng.domain.project.repository;

import static com.ieum.ansimdonghaeng.domain.project.entity.QProject.project;
import static com.ieum.ansimdonghaeng.domain.proposal.entity.QProposal.proposal;
import static com.ieum.ansimdonghaeng.domain.user.entity.QUser.user;

import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepositoryImpl implements ProjectQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProjectSummaryView> findMyProjects(Long ownerUserId, ProjectStatus status, Pageable pageable) {
        BooleanExpression statusCondition = status == null ? null : project.status.eq(status);

        List<ProjectSummaryView> content = queryFactory
                .select(Projections.constructor(
                        ProjectSummaryView.class,
                        project.id,
                        project.title,
                        user.name,
                        project.projectTypeCode,
                        project.serviceRegionCode,
                        project.requestedStartAt,
                        project.requestedEndAt,
                        project.status,
                        project.createdAt,
                        project.updatedAt
                ))
                .from(project)
                .leftJoin(user).on(user.id.eq(project.ownerUserId))
                .where(project.ownerUserId.eq(ownerUserId), statusCondition)
                .orderBy(project.createdAt.desc(), project.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.count())
                .from(project)
                .where(project.ownerUserId.eq(ownerUserId), statusCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<ProjectSummaryView> findFreelancerVisibleProjects(Long freelancerUserId,
                                                                  ProjectStatus status,
                                                                  Pageable pageable) {
        BooleanExpression visibilityCondition = freelancerVisibilityCondition(freelancerUserId, status);

        List<ProjectSummaryView> content = queryFactory
                .select(Projections.constructor(
                        ProjectSummaryView.class,
                        project.id,
                        project.title,
                        user.name,
                        project.projectTypeCode,
                        project.serviceRegionCode,
                        project.requestedStartAt,
                        project.requestedEndAt,
                        project.status,
                        project.createdAt,
                        project.updatedAt
                ))
                .from(project)
                .leftJoin(user).on(user.id.eq(project.ownerUserId))
                .where(visibilityCondition)
                .orderBy(project.createdAt.desc(), project.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.count())
                .from(project)
                .where(visibilityCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression freelancerVisibilityCondition(Long freelancerUserId, ProjectStatus status) {
        BooleanExpression acceptedByFreelancer = JPAExpressions
                .selectOne()
                .from(proposal)
                .where(
                        proposal.project.id.eq(project.id),
                        proposal.freelancerProfile.user.id.eq(freelancerUserId),
                        proposal.status.eq(ProposalStatus.ACCEPTED)
                )
                .exists();

        if (status == null) {
            return project.status.eq(ProjectStatus.REQUESTED).or(acceptedByFreelancer);
        }

        if (status == ProjectStatus.REQUESTED) {
            return project.status.eq(ProjectStatus.REQUESTED);
        }

        return project.status.eq(status).and(acceptedByFreelancer);
    }
}
