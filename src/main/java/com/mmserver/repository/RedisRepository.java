package com.mmserver.repository;

import com.mmserver.domain.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Refresh Token을 관리하기위한 Redis Repository
 */
@Repository
public interface RedisRepository extends CrudRepository<Token, String> {
}
