package com.mmserver.repository;

import com.mmserver.domain.mapper.UserInfoMapping;
import com.mmserver.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 데이터 관리 Repository
 */
public interface UserRepository extends JpaRepository<User, String> {

    Optional<UserInfoMapping> findByEmail(String email);

    Optional<User> findByUserName(String userName);
}
