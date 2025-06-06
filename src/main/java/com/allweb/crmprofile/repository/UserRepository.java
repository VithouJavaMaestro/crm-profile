package com.allweb.crmprofile.repository;

import com.allweb.crmprofile.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {
  Mono<Boolean> existsUserEntityByEmail(String email);
}
