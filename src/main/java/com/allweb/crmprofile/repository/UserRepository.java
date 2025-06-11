package com.allweb.crmprofile.repository;

import com.allweb.crmprofile.entity.UserEntity;
import java.util.List;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
  Mono<Boolean> existsUserEntityByEmail(String email);

  Flux<UserEntity> findAllByPrincipleIdIn(List<String> ids);

  Mono<UserEntity> findByPrincipleId(String principleId);
}
