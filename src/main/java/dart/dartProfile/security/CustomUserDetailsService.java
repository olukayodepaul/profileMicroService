package dart.dartProfile.security;

import dart.dartProfile.darts_app.repository.RedisCacheRepo;
import dart.dartProfile.utilities.CustomRuntimeException;
import dart.dartProfile.utilities.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService  implements UserDetailsService {

    private final RedisCacheRepo redisTemplate;
    private final FilterService filterService;
    private final BCryptPasswordEncoder encoder;

    public CustomUserDetailsService(RedisCacheRepo redisTemplate, FilterService filterService) {
        this.redisTemplate = redisTemplate;
        this.filterService = filterService;
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public UserDetails loadUserByUsername(String token) {

        if(redisTemplate.isTokenBlacklisted(token)) {
            return new User(
                    filterService.extractEmail(token),
                    encoder.encode(filterService.extractUUID(token)),
                    Collections.emptyList()
            );
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false, "Token BlackListed", "The token use is blacklisted."),
                HttpStatus.BAD_REQUEST
        );

    }
}