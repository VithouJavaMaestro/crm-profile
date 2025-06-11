package com.allweb.crmprofile.service;

import static com.allweb.crmprofile.criteria.CriteriaUtils.*;

import com.allweb.crmprofile.client.OAuth2Client;
import com.allweb.crmprofile.constants.UserEntityColumns;
import com.allweb.crmprofile.entity.UserEntity;
import com.allweb.crmprofile.io.StorageConfigurationProperties;
import com.allweb.crmprofile.payload.OidcUser;
import com.allweb.crmprofile.payload.User;
import com.allweb.crmprofile.repository.UserRepository;
import com.crm.commons.specification.exception.BadRequestException;
import com.crm.commons.specification.exception.NotFoundException;
import com.crm.commons.specification.exception.UnAuthorizedException;
import com.crm.commons.specification.utils.IOUtils;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
  private final UserRepository userRepository;

  private final OAuth2Client oauth2Client;

  private final Path profileDir;

  public static final Log log = LogFactory.getLog(UserService.class);

  private final R2dbcEntityTemplate r2dbcEntityOperations;

  private final UserIdentityService userIdentityService;

  public UserService(
      UserRepository userRepository,
      OAuth2Client oauth2Client,
      StorageConfigurationProperties properties,
      R2dbcEntityTemplate r2dbcEntityOperations,
      UserIdentityService userIdentityService) {
    this.userRepository = userRepository;
    this.oauth2Client = oauth2Client;
    this.profileDir = properties.resolve("profile");
    this.r2dbcEntityOperations = r2dbcEntityOperations;
    this.userIdentityService = userIdentityService;
    if (this.profileDir.toFile().mkdir()) {
      log.info("directory profile does not exists create a new one");
    }
  }

  @Transactional
  public Flux<User> getUsers(String filter, List<Long> ids) {

    Criteria criteria =
        Criteria.from(like(UserEntityColumns.FIRSTNAME, filter, MatchMode.CONTAINS))
            .or(like(UserEntityColumns.LASTNAME, filter, MatchMode.CONTAINS))
            .or(
                like(UserEntityColumns.EMAIL, filter, MatchMode.CONTAINS)
                    .or(
                        like(UserEntityColumns.PROFILE, filter, MatchMode.CONTAINS)
                            .or(like(UserEntityColumns.PHONE, filter, MatchMode.CONTAINS))))
            .and(in(UserEntityColumns.ID, ids));

    return r2dbcEntityOperations
        .select(Query.query(criteria), UserEntity.class)
        .map(UserService::entityToUser);
  }

  public Mono<User> getUser(Long id) {
    return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
            .map(UserService::entityToUser);
  }

  @Transactional
  public Mono<User> me() {
    return userIdentityService
        .getUserIdentity()
        .switchIfEmpty(Mono.error(new UnAuthorizedException("User not logged in")))
        .flatMap(userIdentity -> findByPrincipalId(userIdentity.getId()));
  }

  public Mono<User> findByPrincipalId(String principalId) {
    return userRepository
        .findByPrincipleId(principalId)
        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
        .map(UserService::entityToUser);
  }

  @Transactional
  public Mono<User> getUser(String id) {
    return userRepository.findByPrincipleId(id).map(UserService::entityToUser);
  }

  public Flux<DataBuffer> readProfile(String filename) {
    return DataBufferUtils.read(profileDir.resolve(filename), new DefaultDataBufferFactory(), 4096);
  }

  @Transactional
  public Mono<User> createUser(FilePart profile, User request) {
    return Mono.defer(
            () ->
                profile
                    .content()
                    .<FilePart>handle(
                        (dataBuffer, sink) -> {
                          InputStream inputStream = dataBuffer.asInputStream();
                          if (!IOUtils.isImage(inputStream)) {
                            sink.error(new BadRequestException("Invalid image"));
                          } else {
                            sink.next(profile);
                          }
                        })
                    .flatMap(
                        content -> {
                          String extension = FilenameUtils.getExtension(content.filename());
                          String profileId = UUID.randomUUID().toString();
                          String filename = IOUtils.appendExtension(profileId, extension);
                          Path store = profileDir.resolve(filename);
                          return content
                              .transferTo(store)
                              .thenReturn(Pair.of(profileId, store.toString()));
                        })
                    .single())
        .flatMap(
            pair ->
                userRepository
                    .existsUserEntityByEmail(request.getEmail())
                    .<UserEntity>handle(
                        (existing, sink) -> {
                          if (existing == Boolean.TRUE) {
                            sink.error(
                                new BadRequestException(
                                    "user with email " + request.getEmail() + " already exists"));
                          } else {
                            sink.next(buildUserEntity(request, pair));
                          }
                        })
                    .flatMap(
                        user -> {
                          OidcUser oidcUser = userToOidcUser(request);
                          return createOidcUser(user, oidcUser).flatMap(saveUser(request));
                        }));
  }

  private Function<UserEntity, Mono<? extends User>> saveUser(User request) {
    return userEntity ->
        userRepository
            .save(userEntity)
            .map(
                savedEntity -> {
                  request.setId(savedEntity.getId());
                  return request;
                });
  }

  private Mono<UserEntity> createOidcUser(UserEntity user, OidcUser oidcUser) {
    return oauth2Client
        .createOidcUser(oidcUser)
        .map(
            (response) -> {
              user.setPrincipleId(response.getId());
              return user;
            });
  }

  private static User entityToUser(UserEntity userEntity) {
    User user = new User();
    user.setId(userEntity.getId());
    user.setFirstname(userEntity.getFirstname());
    user.setLastname(userEntity.getLastname());
    user.setEmail(userEntity.getEmail());
    user.setPhone(userEntity.getPhone());
    user.setProfileId(userEntity.getProfile());
    user.setPrincipleId(userEntity.getPrincipleId());
    return user;
  }

  private static UserEntity buildUserEntity(User request, Pair<String, String> pair) {
    UserEntity userEntity = new UserEntity();
    userEntity.setFirstname(request.getFirstname());
    userEntity.setLastname(request.getLastname());
    userEntity.setEmail(request.getEmail());
    userEntity.setProfile(pair.getFirst());
    userEntity.setProfilePath(pair.getSecond());
    userEntity.setPhone(request.getPhone());
    return userEntity;
  }

  private static OidcUser userToOidcUser(User request) {
    OidcUser oidcUser = new OidcUser();
    oidcUser.setUsername(request.getEmail());
    oidcUser.setPassword(request.getPassword());
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("firstname", request.getFirstname());
    attributes.put("lastname", request.getLastname());
    attributes.put("email", request.getEmail());
    attributes.put("phone", request.getPhone());
    oidcUser.setAttributes(attributes);
    return oidcUser;
  }
}
