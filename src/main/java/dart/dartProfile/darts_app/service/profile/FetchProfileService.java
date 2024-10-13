package dart.dartProfile.darts_app.service.profile;

import dart.dartProfile.darts_app.entity.FetchProfileFromCacheModel;
import dart.dartProfile.darts_app.entity.FetchProfileResModel;
import dart.dartProfile.darts_app.entity.ProfileDbModel;
import dart.dartProfile.darts_app.mapper.ModelMapper;
import dart.dartProfile.darts_app.repository.ProfileRepo;
import dart.dartProfile.darts_app.repository.RedisCacheRepo;
import dart.dartProfile.security.FilterService;
import dart.dartProfile.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FetchProfileService {

    private final ValidationUtils validationUtils;
    private final ProfileRepo profileRepo;
    private final UtilitiesManager utilitiesManager;
    private final RedisCacheRepo redisCacheRepo;
    private final FilterService filterService;
    private final ModelMapper profileMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchProfileService.class);

    public FetchProfileService(
            ValidationUtils validationUtils,
            ProfileRepo profileRepo,
            UtilitiesManager utilitiesManager,
            RedisCacheRepo redisCacheRepo,
            FilterService filterService,
            ModelMapper profileMapper
    )
    {
        this.validationUtils = validationUtils;
        this.profileRepo = profileRepo;
        this.utilitiesManager = utilitiesManager;
        this.redisCacheRepo = redisCacheRepo;
        this.filterService = filterService;
        this.profileMapper = profileMapper;
    }


    public ResponseEntity<FetchProfileResModel> fetchUserProfile(String jwt) {

        String token = filterService.extractUUID(filterService.extractTokenFromHeader(jwt));
        validateRequest(jwt, token);

        FetchProfileFromCacheModel fetchProfileFromCache =  redisCacheRepo
                .fetchUserProfile(token);

        if(fetchProfileFromCache.getStatus()){
            return buildResponse(profileMapper.toProfileFromCache(fetchProfileFromCache.getProfile()));
        }else {
            ProfileDbModel existingProfile = fetchProfile(token);
            saveProfileToCache(existingProfile);
            return buildResponse(existingProfile);
        }
    }

    private ProfileDbModel fetchProfile(String token) {
        return profileRepo
                .findByUuid(utilitiesManager.convertStringToUUID(token))
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.DELETE_PROFILE_ERROR_TAG, AppConfig.DELETE_PROFILE_ERROR_RESPONSE),
                        HttpStatus.NOT_FOUND
                ));
    }

    private void validateRequest(String token, String uuid) {
        validationUtils.jwtValidateRequest(token);
        validationUtils.bruteForceProtection(AppConfig.FETCH_PROFILE_BRUTE_FORCE_PROTECTION+uuid);
    }

    private ResponseEntity<FetchProfileResModel> buildResponse(ProfileDbModel profile) {
        return new ResponseEntity<>(new FetchProfileResModel(
                true,
                AppConfig.FETCH_PROFILE_RESPONSE,
                toResponseModel(profile)
        ), HttpStatus.OK);
    }

    public FetchProfileResModel.Profile toResponseModel(ProfileDbModel profile) {
        return FetchProfileResModel.Profile.builder()
                .first_name(profile.getFirst_name())
                .last_name(profile.getLast_name())
                .phone_number(profile.getPhone_number())
                .date_of_birth(profile.getDate_of_birth())
                .gender(profile.getGender())
                .bio(profile.getBio())
                .status(profile.isStatus())
                .updated_at(profile.getUpdated_at())
                .created_at(profile.getCreated_at())
                .build();
    }

    private boolean saveProfileToCache(ProfileDbModel profile) {
        return redisCacheRepo.saveUpdateProfile(profileMapper.toCacheFromProfile(profile));
    }

}
