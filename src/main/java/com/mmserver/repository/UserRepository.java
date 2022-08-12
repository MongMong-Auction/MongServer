package com.mmserver.repository;

import com.mmserver.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 데이터 관리 Repository
 */
public interface UserRepository extends JpaRepository<User, String> {
}
