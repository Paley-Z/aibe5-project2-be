package com.ieum.ansimdonghaeng.domain.verification.repository;

import com.ieum.ansimdonghaeng.domain.verification.entity.VerificationFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VerificationFileRepository extends JpaRepository<VerificationFile, Long> {

    boolean existsByVerification_Id(Long verificationId);

    @Query("select distinct verificationFile.verificationId from VerificationFile verificationFile where verificationFile.verificationId in :verificationIds")
    List<Long> findVerificationIdsWithFiles(@Param("verificationIds") List<Long> verificationIds);

    List<VerificationFile> findAllByVerification_IdOrderByUploadedAtAsc(Long verificationId);

    @Query("""
            select file
            from VerificationFile file
            join fetch file.verificationRequest request
            join fetch request.freelancerProfile profile
            join fetch profile.user
            where request.id = :verificationRequestId
              and profile.user.id = :userId
            order by file.uploadedAt desc
            """)
    List<VerificationFile> findAllByVerificationIdAndUserId(@Param("verificationRequestId") Long verificationRequestId,
                                                            @Param("userId") Long userId);

    @Query("""
            select file
            from VerificationFile file
            join fetch file.verificationRequest request
            join fetch request.freelancerProfile profile
            join fetch profile.user
            where file.id = :fileId
            """)
    Optional<VerificationFile> findDetailById(@Param("fileId") Long fileId);
}
