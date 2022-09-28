package com.mmserver.service;

import com.mmserver.config.security.UserInfo;
import com.mmserver.domain.UserInfoDto;
import com.mmserver.domain.model.User;
import com.mmserver.exception.NotFoundEmailException;
import com.mmserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * 사용자 관리 Service
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    /**
     * 사용자 데이터 관리 Repository
     */
    private final UserRepository userRepository;


    /**
     * 사용자 이메일을 통해 UserDetails 인스턴스 반환
     *
     * @param  username    : 사용자 식별 값
     * @return UserDetails : Authentication에 저장될 객체
     * @throws NotFoundEmailException : 이메일을 통해 조회된 사용자가 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws NotFoundEmailException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    throw new NotFoundEmailException();
                });

        return new UserInfo(user);
    }

    /**
     * 사용자 정보 조회
     *
     * @param  id          : 사용자 식별 값
     * @return UserInfoDTO : 사용자 정보
     */
    public UserInfoDto findUserByEmail(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundEmailException();
        }).toUserInfo();
    }
}
