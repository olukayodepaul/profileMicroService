package dart.dartProfile.darts_app.service.address;

import dart.dartProfile.darts_app.entity.AddAddressReqModel;
import dart.dartProfile.darts_app.entity.AddressDbModel;
import dart.dartProfile.darts_app.entity.SaveAddress;
import dart.dartProfile.darts_app.mapper.ModelMapper;
import dart.dartProfile.security.FilterService;
import dart.dartProfile.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddAddressService {

    private final ValidationUtils validationUtils;
    private final UtilitiesManager utilitiesManager;
    private final ModelMapper profileMapper;
    private final SaveAndUpdateRecord saveAndUpdateRecord;
    private final FilterService filterService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AddAddressService.class);

    public AddAddressService(
            ValidationUtils validationUtils,
            UtilitiesManager utilitiesManager,
            ModelMapper profileMapper,
            SaveAndUpdateRecord saveAndUpdateRecord,
            FilterService filterService
    )
    {
        this.validationUtils = validationUtils;
        this.utilitiesManager = utilitiesManager;
        this.profileMapper = profileMapper;
        this.saveAndUpdateRecord = saveAndUpdateRecord;
        this.filterService = filterService;
    }

    public ResponseEntity<ResponseHandler> addUserAddress(List<AddAddressReqModel> request, String jwt){

        String token = filterService.extractTokenFromHeader(jwt);
        String uuid = filterService.extractUUID(token);
        String organisationId = filterService.extractOrganisationId(token);
        validateRequest(jwt,uuid);

        List<AddressDbModel> saveAddress = profileMapper.toAddAddress(request, utilitiesManager.convertStringToUUID(uuid), organisationId);

        SaveAddress saveResult = saveAddress(saveAddress);

        if(!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, AppConfig.ADD_ADDRESS_CANT_BE_SAVE_ERROR, saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(new ResponseHandler(true, AppConfig.ADD_ADDRESS_SAVE_RESPONSE), HttpStatus.CREATED);
    }

    private void validateRequest(String token, String uuid) {
        validationUtils.jwtValidateRequest(token);
        validationUtils.bruteForceProtection(AppConfig.ADD_ADDRESS_BRUTE_FORCE_PROTECTION+uuid);
    }

    private SaveAddress saveAddress(List<AddressDbModel> addressRecord) {
        return saveAndUpdateRecord.saveAddressRecord(addressRecord);
    }
}
