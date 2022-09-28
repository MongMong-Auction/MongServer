package com.mmserver.domain.model;

import com.mmserver.config.security.oauth.OAuth2UserInfo;
import com.mmserver.domain.EnumType.RoleType;
import com.mmserver.domain.UserInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 관리 Entity
 */
@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name        = "EMAIL_UNIQUE",
                columnNames = "email"
        )
})
public class User {

    /**
     * 식별 값(PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 아이디
     */
    private String email;

    /**
     * OAuth 서비스 이름
     */
    private String oauth;

    /**
     * 비밀번호
     */
    private String password;

    /**
     * 사용자 이름
     */
    private String userName;

    /**
     * 사용자 권한
     */
    @Enumerated(EnumType.STRING)
    private RoleType role;

    /**
     * 포인트
     */
    private int point;

    /**
     * 테마 설정 값
     */
    private int theme;

    /**
     * 로그인 실패 횟수
     */
    private int failCnt;

    /**
     * 계정 잠금 여부
     */
    private String lockYn;

    /**
     * 최근 로그인 시간
     */
    private LocalDate lastLogin;

    /**
     * 생성 시간
     */
    @CreationTimestamp
    private LocalDateTime createDate;

    /**
     * 수정 시간
     */
    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    /**
     * insert 되기전 실행
     * */
    @PrePersist
    public void prePersist() {
        // role 값이 없을 경우
        // default => USER
        role   = (role == null) ? RoleType.USER : role;

        // 계정 잠금 여부 N
        lockYn = "N";
    }

    /**
     * 로컬 회원가입 시, 객체 생성을 위한 생성
     *
     * @param email         : 사용자 아이디
     * @param password      : 비밀번호
     * @param userName      : 사용자 이름
     */
    @Builder
    public User(String email, String password, String userName) {
        this.email    = email;
        this.password = password;
        this.userName = userName;
    }

    /**
     * 소셜 로그인
     * User DB에 저장하기위한 인스턴스 반환
     *
     * @param userInfo : OAuth2 제공 기관으로 부터 제공받은 데이터로 가공한 객체
     */
    public User oauthInfoUpdate(OAuth2UserInfo userInfo) {
        email    = userInfo.getEmail();
        oauth    = userInfo.getOauth();
        userName = userInfo.getUserName();

        return this;
    }

    /**
     * 인증 성공 시,
     * Authentication에 저장된 User 데이터 세팅
     *
     * @param user : 사용자 정보
     */
    public void mainInfoUpdate(User user){
        email    = user.getEmail();
        oauth    = user.getOauth();
        userName = user.getUserName();

        lastLoginUpdate();
    }

    /**
     * 마지막 로그인 날짜 업데이트
     *
     * 최초 로그인의 경우     - 100point
     * 오늘 처음 로그인의 경우 - 10point 추가
     */
    public void lastLoginUpdate(){
        if(lastLogin == null){
            point = 100;
            lastLogin = LocalDate.now();
        }else if(!lastLogin.isEqual(LocalDate.now())){
            point += 10;
            lastLogin = LocalDate.now();
        }
    }

    /**
     * 비밀번호 실패 횟수 증가
     */
    public void addFailCnt() {
        failCnt++;
    }

    /**
     * 비밀번호 실패 횟수 초기화
     */
    public void initFailCnt() {
        failCnt = 0;
    }

    /**
     * User 객체 UserInfoDTO 객체로 변환
     *
     * @return UserInfoDTO : 세팅된 객체
     */
    public UserInfoDto toUserInfo() {
        return UserInfoDto.builder()
                .id(this.id)
                .email(this.email)
                .userName(this.userName)
                .role(this.role)
                .point(this.point)
                .theme(this.theme)
                .build();
    }

    @Override
    public String toString() {
        // 비밀번호 제외
        return "User{" +
                "email='" + email + '\'' +
                ", oauth='" + oauth + '\'' +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", point=" + point +
                ", theme=" + theme +
                ", failCnt=" + failCnt +
                ", lockYn='" + lockYn + '\'' +
                ", lastLogin=" + lastLogin +
                ", createDate=" + createDate +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}
