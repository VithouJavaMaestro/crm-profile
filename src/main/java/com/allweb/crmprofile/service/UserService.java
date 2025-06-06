package com.allweb.crmprofile.service;

import com.allweb.crmprofile.entity.UserEntity;
import com.allweb.crmprofile.payload.User;
import com.allweb.crmprofile.repository.UserRepository;
import com.crm.commons.specification.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public Mono<User> createUser(User request) {
    return userRepository
        .existsUserEntityByEmail(request.getEmail())
        .<User>handle(
            (existing, sink) -> {
              if (existing == Boolean.TRUE) {
                sink.error(new BadRequestException("user with email " + request.getEmail() + " already exists"));
              } else {
                sink.next(request);
              }
            })
        .flatMap(
            user -> {
              UserEntity userEntity = new UserEntity();
              userEntity.setFirstname(request.getFirstname());
              userEntity.setLastname(request.getLastname());
              userEntity.setEmail(request.getEmail());
              userEntity.setProfile(request.getProfile());
              return userRepository.save(userEntity);
            })
        .map(
            userEntity -> {
              request.setId(userEntity.getId());
              return request;
            });
  }
}
