package dart.dartProfile.darts_app.service.address;


import dart.dartProfile.darts_app.entity.AddProfileReqResModel;
import dart.dartProfile.darts_app.entity.AddressDbModel;
import dart.dartProfile.darts_app.entity.ProfileDbModel;
import dart.dartProfile.darts_app.entity.UpdateAddress;
import dart.dartProfile.darts_app.mapper.ModelMapper;
import dart.dartProfile.darts_app.repository.AddressRepo;
import dart.dartProfile.darts_app.repository.RedisCacheRepo;
import dart.dartProfile.security.FilterService;
import dart.dartProfile.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateAddressService {

    private final ValidationUtils validationUtils;
    private final ModelMapper profileMapper;
    private final FilterService filterService;
    private final AddressRepo addressRepo;
    private final RedisCacheRepo redisCacheRepo;
    private UtilitiesManager utilitiesManager;
    private final SaveAndUpdateRecord saveAndUpdateRecord;
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAddressService.class);

    public UpdateAddressService(
            ValidationUtils validationUtils,
            ModelMapper profileMapper,
            FilterService filterService,
            RedisCacheRepo redisCacheRepo,
            AddressRepo addressRepo,
            UtilitiesManager utilitiesManager,
            SaveAndUpdateRecord saveAndUpdateRecord
    )
    {
        this.validationUtils = validationUtils;
        this.profileMapper = profileMapper;
        this.filterService = filterService;
        this.addressRepo = addressRepo;
        this.redisCacheRepo = redisCacheRepo;
        this.utilitiesManager = utilitiesManager;
        this.saveAndUpdateRecord = saveAndUpdateRecord;
    }

    public ResponseEntity<ResponseHandler> updateAddress(Integer id, AddProfileReqResModel request, String jwt) {

        String token = filterService.extractTokenFromHeader(jwt);
        String uuid = filterService.extractUUID(token);
        validateRequest(jwt, request, uuid);

        AddressDbModel existingAddress = fetchAddress(id, utilitiesManager.convertStringToUUID(uuid));

        //checking status if particular address is block
        if(existingAddress.isStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false,AppConfig.DELETE_PROFILE_ERROR_TAG , AppConfig.DELETE_PROFILE_PREVIOUSLY_DELETE),
                    HttpStatus.BAD_REQUEST
            );
        }

        UpdateAddress updateResult = saveProfile(profileMapper.toUpdateAddress(existingAddress, false));


        if(!updateResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, AppConfig.DELETE_PROFILE_ERROR_TAG, AppConfig.DELETE_PROFILE_DELETED_ERROR_RESPONSE),
                    HttpStatus.BAD_REQUEST
            );
        }


        boolean updateCacheResult = deleteAddressFromCache(uuid);
        return buildResponse(updateCacheResult);
    }

    private boolean deleteAddressFromCache(String uuid) {
        return redisCacheRepo.deleteAllAddress(uuid);
    }


    private void validateRequest(String token, AddProfileReqResModel requestBody, String uuid) {
        validationUtils.jwtValidateRequest(token);
        validationUtils.profileValidateRequest(requestBody);
        validationUtils.bruteForceProtection(AppConfig.UPDATE_ADDRESS_BRUTE_FORCE_PROTECTION+uuid);
    }

    private AddressDbModel fetchAddress(Integer id, UUID uuid) {
        return addressRepo.findByIdAndUuid(id, uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.DELETE_ADDRESS_ERROR_TAG, AppConfig.DELETE_ADDRESS_ERROR_RESPONSE),
                        HttpStatus.NOT_FOUND
                ));
    }

    private UpdateAddress saveProfile(AddressDbModel addressModel) {
        return saveAndUpdateRecord.updateAddressRecord(addressModel);
    }

    private ResponseEntity<ResponseHandler> buildResponse(boolean cacheResult) {
        if (!cacheResult) {
            // todo: use kafka to retry and resave the record
        }
        return new ResponseEntity<>(new ResponseHandler(true, AppConfig.DELETE_PROFILE_SUCCESSFUL), HttpStatus.OK);
    }

}
