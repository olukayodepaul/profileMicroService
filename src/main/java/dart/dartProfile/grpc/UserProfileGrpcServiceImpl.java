package dart.dartProfile.grpc;


import dart.dartProfile.darts_app.mapper.ModelMapper;
import dart.dartProfile.darts_app.service.profile.AddProfileGrpcService;
import dart.dartProfile.utilities.ResponseHandler;
import darts.grpc.server.UserProfileGrpc;
import darts.grpc.server.UserProfileOuterClass;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class UserProfileGrpcServiceImpl extends UserProfileGrpc.UserProfileImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileGrpcServiceImpl.class);

    private final AddProfileGrpcService profileService;
    private final ModelMapper profileMapper;

    public UserProfileGrpcServiceImpl(AddProfileGrpcService profileService, ModelMapper profileMapper) {
        this.profileService = profileService;
        this.profileMapper = profileMapper;
    }

    @Override
    public void addProfile(UserProfileOuterClass.Profile request, StreamObserver<UserProfileOuterClass.Response> responseObserver) {
        try {
            ResponseHandler implResponse = profileService.getUserProfile(profileMapper.toProfileModel(request));
            UserProfileOuterClass.Response response = buildResponse(implResponse);
            sendResponse(responseObserver, response);
        } catch (Exception e) {
            LOGGER.error("UserProfileGrpcServiceImpl::addProfile Error adding profile", e);
            sendErrorResponse(responseObserver);
        }
    }

    private void sendErrorResponse(StreamObserver<UserProfileOuterClass.Response> responseObserver) {
        UserProfileOuterClass.Response response = UserProfileOuterClass.Response.newBuilder()
                .setStatus(false)
                .setMessage("An error occurred")
                .build();
        sendResponse(responseObserver, response);
    }

    private void sendResponse(StreamObserver<UserProfileOuterClass.Response> responseObserver, UserProfileOuterClass.Response response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private UserProfileOuterClass.Response buildResponse(ResponseHandler implResponse) {
        return UserProfileOuterClass.Response.newBuilder()
                .setStatus(false)
                .setMessage(implResponse.getMessage())
                .build();
    }

}
