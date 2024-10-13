package dart.dartProfile.security;

import io.grpc.*;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class GrpcFilter implements ServerInterceptor {

    private final FilterService filterService;
    private final ApplicationContext context;


    public GrpcFilter( FilterService filterService, ApplicationContext context) {
        this.filterService = filterService;
        this.context = context;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        try{
            String authHeader = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));

            if (authHeader == null || authHeader.isEmpty()) {
                call.close(Status.UNAUTHENTICATED.withDescription("Missing JWT token"), new Metadata());
                return new ServerCall.Listener<ReqT>() {};
            }

            String token = filterService.extractTokenFromHeader(authHeader);

            if (token == null) {
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid JWT token format"), new Metadata());
                return new ServerCall.Listener<ReqT>() {};
            }

            if (!filterService.verifyTokenSignature(token)) {
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid token signature"), new Metadata());
                return new ServerCall.Listener<ReqT>() {};
            }

            UserDetails userDetails = loadUserDetails(token);

            if (userDetails == null) {
                call.close(Status.UNAUTHENTICATED.withDescription("User not found"), new Metadata());
                return new ServerCall.Listener<ReqT>() {};
            }

            if (!validateToken(token, userDetails)) {
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid JWT token"), new Metadata());
                return new ServerCall.Listener<ReqT>() {};
            }

            if (filterService.isTokenExpired(token)) {
                call.close(Status.UNAUTHENTICATED.withDescription("Token expired"), new Metadata());
                return new ServerCall.Listener<ReqT>() {};
            }

//            if (isTokenMalformed(token)) {
//                call.close(Status.UNAUTHENTICATED.withDescription("Token malformed"), new Metadata());
//                return new ServerCall.Listener<ReqT>() {};
//            }

            return next.startCall(call, headers);

        } catch (Exception e) {
            call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
    }

    private boolean isTokenMalformed(String token) {
        try {
            filterService.validateToken(token, loadUserDetails(filterService.extractUsername(token)));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean validateToken(String token, UserDetails userDetails) {
        return filterService.validateToken(token, userDetails);
    }

    private UserDetails loadUserDetails(String username) {
        return context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);
    }

}
