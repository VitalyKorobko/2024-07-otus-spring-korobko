//package ru.otus.hw.repositories;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClient;
//import ru.otus.hw.config.TokenStorage;
//import ru.otus.hw.dto.RoleDto;
//import ru.otus.hw.models.Role;
//import ru.otus.hw.models.User;
//
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//@Component
//public class UserRepositoryImpl implements UserRepository {
//    private static final String AUTHORIZATION = "Authorization";
//
//    private static final String BEARER = "Bearer ";
//
//    private final RestClient authRestClient;
//
//    private final TokenStorage tokenStorage;
//
//    public UserRepositoryImpl(@Qualifier("authRestClient") RestClient authRestClient, TokenStorage tokenStorage) {
//        this.authRestClient = authRestClient;
//        this.tokenStorage = tokenStorage;
//    }
//
//    @Override
//    public User findByUsername(String username) {
//        return authRestClient.get()
//                .uri("api/v1/user/username/%s".formatted(username))
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
//                .retrieve()
//                .body(User.class);
//    }
//
//    @Override
//    public List<User> findAllByRoles(Role role) {
//        return authRestClient.post()
//                .uri("/api/v1/users")
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
//                .body(Set.of(new RoleDto(Role.SELLER.name())))
//                .retrieve()
//                .body(new ParameterizedTypeReference<List<User>>() {
//                    @Override
//                    public Type getType() {
//                        return super.getType();
//                    }
//                });
//    }
//
//    @Override
//    public User findByEmail(String email) {
//        return authRestClient.get()
//                .uri("/api/v1/user/email/%s".formatted(email))
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
//                .retrieve()
//                .body(User.class);
//    }
//
//    @Override
//    public Optional<User> findById(long id) {
//        var user = authRestClient.get()
//                .uri("/api/v1/user/id/%d".formatted(id))
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
//                .retrieve()
//                .body(User.class);
//        return Optional.ofNullable(user);
//    }
//
//    @Override
//    public User create(User user) {
//        //todo проверить маппинг
//        return authRestClient.post()
//                .uri("/api/v1/user")
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
//                .body(user)
//                .retrieve()
//                .body(User.class);
//    }
//
//    @Override
//    public User update(User user) {
//        //todo проверить маппинг
//        return authRestClient.patch()
//                .uri("/api/v1/user")
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
//                .body(user)
//                .retrieve()
//                .body(User.class);
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("\n===============loadUserByUsername====START================\n");
//
//        UserDetails userDetails = authRestClient.get()
//                .uri("/api/v1/userdetails/%s".formatted(username))
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
//                .retrieve()
//                .body(UserDetails.class);
//        System.out.println(userDetails);
//
//        System.out.println("\n===============loadUserByUsername====END================\n");
//
//        return userDetails;
//    }
//
//}
