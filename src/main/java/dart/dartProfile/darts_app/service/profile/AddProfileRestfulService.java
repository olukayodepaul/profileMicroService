package dart.dartProfile.darts_app.service.profile;

import dart.dartProfile.darts_app.entity.AddProfileReqResModel;
import dart.dartProfile.darts_app.entity.ProfileDbModel;
import dart.dartProfile.darts_app.mapper.ModelMapper;
import dart.dartProfile.darts_app.repository.ProfileRepo;
import dart.dartProfile.security.FilterService;
import dart.dartProfile.utilities.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AddProfileRestfulService {

    private final ValidationUtils validationUtils;
    private final ProfileRepo profileRepo;
    private final UtilitiesManager utilitiesManager;
    private final ModelMapper profileMapper;
    private final SaveAndUpdateRecord saveAndUpdateProfile;
    private final FilterService filterService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AddProfileRestfulService.class);

    public AddProfileRestfulService(
            ValidationUtils validationUtils,
            ProfileRepo profileRepo,
            UtilitiesManager utilitiesManager,
            ModelMapper profileMapper,
            SaveAndUpdateRecord saveAndUpdateProfile,
            FilterService filterService
    )
    {
        this.validationUtils = validationUtils;
        this.profileRepo = profileRepo;
        this.utilitiesManager = utilitiesManager;
        this.profileMapper = profileMapper;
        this.saveAndUpdateProfile = saveAndUpdateProfile;
        this.filterService = filterService;
    }

    @Transactional
    public ResponseEntity<ResponseHandler> addProfile(AddProfileReqResModel requestBody, String jwt) {

        String token = filterService.extractTokenFromHeader(jwt);
        validateRequest(jwt, requestBody, filterService.extractUUID(token));

        Optional<ProfileDbModel> dbExistingProfile = profileRepo.findByUuid(utilitiesManager.convertStringToUUID(filterService.extractUUID(token)));

        if (dbExistingProfile.isPresent()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, AppConfig.ADD_PROFILE_ERROR_TAG, AppConfig.ADD_PROFILE_ERROR_RESPONSE),
                    HttpStatus.BAD_REQUEST
            );
        }

        requestBody.setOrganisation_id(filterService.extractOrganisationId(token));
        requestBody.setUuid(filterService.extractUUID(token));
        requestBody.setCreated_at(LocalDateTime.now());
        requestBody.setUpdated_at(LocalDateTime.now());
        SaveAndUpdateProfileResponse savedResult = saveProfile(requestBody);

        if(!savedResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, AppConfig.ADD_PROFILE_CANT_BE_SAVE_ERROR, savedResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        return buildResponse();
    }

    private void validateRequest(String token, AddProfileReqResModel requestBody, String uuid) {
        validationUtils.jwtValidateRequest(token);
        validationUtils.profileValidateRequest(requestBody);
        validationUtils.bruteForceProtection(AppConfig.ADD_PROFILE_BRUTE_FORCE_PROTECTION+uuid);
    }

    private ResponseEntity<ResponseHandler> buildResponse() {
        return new ResponseEntity<>(new ResponseHandler(true, AppConfig.ADD_PROFILE_SAVE_RESPONSE), HttpStatus.CREATED);
    }

    private SaveAndUpdateProfileResponse saveProfile(AddProfileReqResModel requestBody) {
        return saveAndUpdateProfile.saveUpdatedProfileRecord(profileMapper.toCreateNewProfile(requestBody));
    }

}