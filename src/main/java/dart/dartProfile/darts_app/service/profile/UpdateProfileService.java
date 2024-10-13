package dart.dartProfile.darts_app.service.profile;

import dart.dartProfile.darts_app.entity.AddProfileReqResModel;
import dart.dartProfile.darts_app.entity.ProfileDbModel;
import dart.dartProfile.darts_app.repository.ProfileRepo;
import dart.dartProfile.darts_app.repository.RedisCacheRepo;
import dart.dartProfile.security.FilterService;
import dart.dartProfile.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UpdateProfileService {

    private final ValidationUtils validationUtils;
    private final ProfileRepo profileRepo;
    private final UtilitiesManager utilitiesManager;
    private final RedisCacheRepo redisCacheRepo;
    private final FilterService filterService;
    private final SaveAndUpdateRecord saveAndUpdateProfile;
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProfileService.class);

    public UpdateProfileService(
            ValidationUtils validationUtils,
            ProfileRepo profileRepo,
            UtilitiesManager utilitiesManager,
            RedisCacheRepo redisCacheRepo,
            FilterService filterService,
            SaveAndUpdateRecord saveAndUpdateProfile
    ) {
        this.validationUtils = validationUtils;
        this.profileRepo = profileRepo;
        this.utilitiesManager = utilitiesManager;
        this.redisCacheRepo = redisCacheRepo;
        this.filterService = filterService;
        this.saveAndUpdateProfile = saveAndUpdateProfile;
    }

    public ResponseEntity<ResponseHandler> updateProfile(AddProfileReqResModel request, String jwt) {

        String token = filterService.extractUUID(filterService.extractTokenFromHeader(jwt));
        validateRequest(jwt, token, request);

        ProfileDbModel existingProfile = fetchProfile(token);
        SaveAndUpdateProfileResponse updatedResult = updateProfile(request, existingProfile);

        if(!updatedResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, AppConfig.UPDATE_PROFILE_CANT_BE_SAVE_ERROR, updatedResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        boolean deletedCacheResult = deleteProfileFromCache(existingProfile.getUuid().toString());
        return buildResponse(deletedCacheResult);
    }

    private ProfileDbModel fetchProfile(String token) {
        return profileRepo
                .findByUuid(utilitiesManager.convertStringToUUID(token))
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.DELETE_PROFILE_ERROR_TAG, AppConfig.DELETE_PROFILE_ERROR_RESPONSE),
                        HttpStatus.NOT_FOUND
                ));
    }

    private void validateRequest(String token, String uuid, AddProfileReqResModel request) {
        validationUtils.jwtValidateRequest(token);
        validationUtils.profileIdValidateRequest(uuid);
        validationUtils.profileValidateRequest(request);
        validationUtils.bruteForceProtection(AppConfig.UPDATE_PROFILE_BRUTE_FORCE_PROTECTION+uuid);
    }

    private SaveAndUpdateProfileResponse updateProfile(AddProfileReqResModel requestBody, ProfileDbModel existingProfile) {
        ProfileDbModel updateProfileRecord = ProfileDbModel.builder()
                .id(existingProfile.getId())
                .uuid(existingProfile.getUuid())
                .first_name(requestBody.getFirst_name())
                .last_name(requestBody.getLast_name())
                .phone_number(requestBody.getPhone_number())
                .organisation_id("1")
                .date_of_birth(utilitiesManager.convertDateOfBirth(requestBody.getDate_of_birth()))
                .gender(requestBody.getGender())
                .bio(requestBody.getBio())
                .status(existingProfile.isStatus())
                .updated_at(LocalDateTime.now())
                .created_at(existingProfile.getCreated_at())
                .build();
        return saveAndUpdateProfile.saveUpdatedProfileRecord(updateProfileRecord);
    }

    private boolean deleteProfileFromCache(String uuid) {
        return redisCacheRepo.deleteProfile(uuid);
    }

    private ResponseEntity<ResponseHandler> buildResponse(boolean cacheResult) {
        if (!cacheResult) {
            // todo: use kafka to retry and resave the record
        }
        return new ResponseEntity<>(new ResponseHandler(true, AppConfig.UPDATE_PROFILE_SUCCESSFUL), HttpStatus.OK);
    }
}
