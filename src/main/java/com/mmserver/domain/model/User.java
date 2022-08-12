package com.mmserver.domain.model;

import com.mmserver.config.security.oauth.OAuthUserProfile;
import com.mmserver.domain.EnumType.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 관리 Entity
 */
@Getter
@NoArgsConstructor
@ToString
@Entity
@DynamicInsert
public class User {

    /**
     * 사용자 아이디(Primary Key)
     */
    @Id
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
    private long point;

    /**
     * 도로명
     */
    private String roadAddress;

    /**
     *  우편번호
     */
    private String zoneNo;

    /**
     * 상세 주소
     */
    private String addressDetail;

    /**
     * 테마 설정 값
     */
    private int theme;

    /**
     * 로그인 실패 횟수
     */
    private int failCnt;

    /**
     * 잠금 여부
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
     * 로컬 회원가입 시, 객체 생성을 위한 생성
     *
     * @param email         : 사용자 아이디
     * @param password      : 비밀번호
     * @param userName      : 사용자 이름
     * @param roadAddress   : 도로명
     * @param zoneNo        : 우편번호
     * @param addressDetail : 상세주소
     */
    @Builder
    public User(String email, String oauth, String password, String userName, String roadAddress, String zoneNo, String addressDetail) {
        this.email         = email;
        this.oauth         = oauth;
        this.password      = password;
        this.userName      = userName;
        this.roadAddress   = roadAddress;
        this.zoneNo        = zoneNo;
        this.addressDetail = addressDetail;
    }

    /**
     * 소셜 로그인
     * User DB에 저장하기위한 인스턴스 반환
     *
     * @param userProfile : OAuth2 제공 기관으로 부터 제공받은 데이터로 가공한 객체
     */
    public void mainInfoUpdate(OAuthUserProfile userProfile) {
        email    = userProfile.getEmail();
        oauth    = userProfile.getOauth();
        userName = userProfile.getUserName();

        if(lastLogin == null){
            // 최초 로그인의 경우 10point 추가
            point = 100;
        }else if(!lastLogin.isEqual(LocalDate.now())){
            // 해당 날짜에 처음 로그인의 경우 10point 추가
            point += 10;
        }
    }

    /**
     * 소셜 로그인 추가 정보 저장
     *
     * @param  roadAddress   : 도로명
     * @param  zoneNo        : 우편번호
     * @param  addressDetail : 상세 주소
     * @return User          : User Entity 객체
     */
    public User subInfoUpdate(String roadAddress, String zoneNo, String addressDetail){
        this.roadAddress   = roadAddress;
        this.zoneNo        = zoneNo;
        this.addressDetail = addressDetail;

        return this;
    }

    /**
     * 마지막 로그인 날짜 업데이트
     */
    public void lastLoginUpdate(){
        // 최초 로그인이거나 오늘 로그인한 적이 없는 경우
        // 마지막 로그인 날짜 업데이트
        if(lastLogin == null || !lastLogin.isEqual(LocalDate.now())){
            lastLogin = LocalDate.now();
        }
    }
}
