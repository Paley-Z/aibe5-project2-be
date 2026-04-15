package com.ieum.ansimdonghaeng.domain.user.entity;

import com.ieum.ansimdonghaeng.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APP_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "INTRO", length = 500)
    private String intro;

    @Column(name = "ROLE_CODE", nullable = false, length = 50)
    private String roleCode;

    @Column(name = "ACTIVE_YN", nullable = false)
    private Boolean activeYn;

    public void updateProfile(String name, String phone, String intro) {
        this.name = name;
        this.phone = phone;
        this.intro = intro;
    }

    public void deactivate() {
        this.activeYn = false;
    }

    public UserRole getRole() {
        return UserRole.fromCode(roleCode);
    }
}
