package dart.dartProfile.darts_app.service.profile;


import dart.dartProfile.darts_app.entity.ProfileDbModel;
import dart.dartProfile.darts_app.mapper.ModelMapper;
import dart.dartProfile.darts_app.repository.ProfileRepo;
import dart.dartProfile.darts_app.repository.RedisCacheRepo;
import dart.dartProfile.security.FilterService;
import dart.dartProfile.utilities.*;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class DeleteProfileService {

    private final ValidationUtils validationUtils;
    private final ProfileRepo profileRepo;
    private final UtilitiesManager utilitiesManager;
    private final SaveAndUpdateRecord saveAndUpdateProfile;
    private final RedisCacheRepo redisCacheRepo;
    private final FilterService filterService;
    private ModelMapper profileMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteProfileService.class);

    public DeleteProfileService(
            ValidationUtils validationUtils,
            ProfileRepo profileRepo,
            UtilitiesManager utilitiesManager,
            SaveAndUpdateRecord saveAndUpdateProfile,
            RedisCacheRepo redisCacheRepo,
            FilterService filterService,
            ModelMapper profileMapper
    )
    {
        this.validationUtils = validationUtils;
        this.profileRepo = profileRepo;
        this.utilitiesManager = utilitiesManager;
        this.saveAndUpdateProfile = saveAndUpdateProfile;
        this.redisCacheRepo = redisCacheRepo;
        this.filterService = filterService;
        this.profileMapper = profileMapper;
    }

    public ResponseEntity<ResponseHandler> deleteProfile(@NonNull String jwt) {

        String token = filterService.extractUUID(filterService.extractTokenFromHeader(jwt));
        validateRequest(jwt, token);

        ProfileDbModel existingProfile = profileRepo
                .findByUuid(utilitiesManager.convertStringToUUID(token))
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.DELETE_PROFILE_ERROR_TAG, AppConfig.DELETE_PROFILE_ERROR_RESPONSE),
                        HttpStatus.NOT_FOUND
                ));

        //checking status if already change
        if(existingProfile.isStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false,AppConfig.DELETE_PROFILE_ERROR_TAG , AppConfig.DELETE_PROFILE_PREVIOUSLY_DELETE),
                    HttpStatus.BAD_REQUEST
            );
        }

        SaveAndUpdateProfileResponse savedResult = saveProfile(profileMapper.profileBuilder(existingProfile));

        if(!savedResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, AppConfig.DELETE_PROFILE_ERROR_TAG, AppConfig.DELETE_PROFILE_DELETED_ERROR_RESPONSE),
                    HttpStatus.BAD_REQUEST
            );
        }

        boolean deletedCacheResult = deleteProfileFromCache(existingProfile.getUuid().toString());
        return buildResponse(deletedCacheResult);
    }

    private ResponseEntity<ResponseHandler> buildResponse(boolean cacheResult) {
        if (!cacheResult) {
            // todo: use kafka to retry and resave the record
        }
        return new ResponseEntity<>(new ResponseHandler(true, AppConfig.DELETE_PROFILE_SUCCESSFUL), HttpStatus.OK);
    }

    private void validateRequest(String token, String uuid) {
        validationUtils.jwtValidateRequest(token);
        validationUtils.profileIdValidateRequest(uuid);
        validationUtils.bruteForceProtection(AppConfig.DELETE_PROFILE_BRUTE_FORCE_PROTECTION+uuid);
    }

    private SaveAndUpdateProfileResponse saveProfile(ProfileDbModel profileDbModel) {
        return saveAndUpdateProfile.saveUpdatedProfileRecord(profileDbModel);
    }

    private boolean deleteProfileFromCache(String uuid) {
        return redisCacheRepo.deleteProfile(uuid);
    }
}