package dart.dartProfile.configuration;

import dart.dartProfile.grpc.UserProfileGrpcServiceImpl;
import dart.dartProfile.security.GrpcFilter;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class GrpcInterceptor {

    private final UserProfileGrpcServiceImpl userProfileGrpcService;
    private final GrpcFilter grpcFilter;

    public GrpcInterceptor(UserProfileGrpcServiceImpl userProfileGrpcService, GrpcFilter grpcFilter) {
        this.userProfileGrpcService = userProfileGrpcService;
        this.grpcFilter = grpcFilter;
    }

    @Bean
    public Server grpcServer() throws IOException {

        Server server = ServerBuilder.forPort(9096)
                .intercept(grpcFilter) // Adds the interceptor
                .addService(userProfileGrpcService) // Adds the gRPC service implementation
                .build();

        // Start the server asynchronously
        new Thread(() -> {
            try {
                server.start();
                System.out.println("gRPC server started on port 9096");
                server.awaitTermination(); // Keeps the server running
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return server;
    }
}