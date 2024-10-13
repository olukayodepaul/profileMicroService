package dart.dartProfile.darts_app.service.address;


import dart.dartProfile.darts_app.entity.*;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class FetchAddressService {

    private final ValidationUtils validationUtils;
    private final ModelMapper profileMapper;
    private final FilterService filterService;
    private final AddressRepo addressRepo;
    private final RedisCacheRepo redisCacheRepo;
    private UtilitiesManager utilitiesManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchAddressService.class);

    public FetchAddressService(
            ValidationUtils validationUtils,
            ModelMapper profileMapper,
            FilterService filterService,
            RedisCacheRepo redisCacheRepo,
            AddressRepo addressRepo,
            UtilitiesManager utilitiesManager
    )
    {
        this.validationUtils = validationUtils;
        this.profileMapper = profileMapper;
        this.filterService = filterService;
        this.addressRepo = addressRepo;
        this.redisCacheRepo = redisCacheRepo;
        this.utilitiesManager = utilitiesManager;
    }


    public ResponseEntity<FetchAddressResModel> getAddress(String jwt) {

        String token = filterService.extractTokenFromHeader(jwt);
        String uuid = filterService.extractUUID(token);
        validateRequest(jwt, uuid);

        FetchAddressFromCacheModel existingCacheAddress = redisCacheRepo.findAllAddresses(uuid);

        if(existingCacheAddress.getStatus()) {
            return new ResponseEntity<>(new FetchAddressResModel(
                    true,
                    AppConfig.ADD_PROFILE_SAVE_RESPONSE,
                    existingCacheAddress.getAddress().stream()
                            .sorted()
                            .map(userAddress -> FetchAddressResModel.Address
                                    .builder()
                                    .id(userAddress.getId())
                                    .type(userAddress.getType())
                                    .address_line2(userAddress.getAddress_line2())
                                    .address_line1(userAddress.getAddress_line1())
                                    .street(userAddress.getStreet())
                                    .city(userAddress.getCity())
                                    .zip(userAddress.getZip())
                                    .state(userAddress.getState())
                                    .country(userAddress.getCountry())
                                    .status(false)
                                    .created_at(userAddress.getCreated_at())
                                    .updated_at(userAddress.getUpdated_at())
                                    .build()
                            ).collect(Collectors.toList())), HttpStatus.OK);
        }else{
            List<AddressDbModel> existingDbAddress = fetchAddress(utilitiesManager.convertStringToUUID(uuid));
            redisCacheRepo.saveAddress(profileMapper.toAddAddressCache(existingDbAddress), uuid);
            return new ResponseEntity<>(new FetchAddressResModel(
                    true,
                    AppConfig.ADD_PROFILE_SAVE_RESPONSE,
                    existingDbAddress.stream()
                            .map(userAddress -> FetchAddressResModel.Address
                                    .builder()
                                    .id(userAddress.getId())
                                    .type(userAddress.getType())
                                    .address_line2(userAddress.getAddress_line2())
                                    .address_line1(userAddress.getAddress_line1())
                                    .street(userAddress.getStreet())
                                    .city(userAddress.getCity())
                                    .zip(userAddress.getZip())
                                    .state(userAddress.getState())
                                    .country(userAddress.getCountry())
                                    .status(false)
                                    .created_at(userAddress.getCreated_at())
                                    .updated_at(userAddress.getUpdated_at())
                                    .build()
                            ).collect(Collectors.toList())), HttpStatus.OK);
        }
    }

    private void validateRequest(String token, String uuid) {
        validationUtils.jwtValidateRequest(token);
        validationUtils.bruteForceProtection(AppConfig.FETCH_ADDRESS_BRUTE_FORCE_PROTECTION+uuid);
    }

    private List<AddressDbModel> fetchAddress(UUID uuid) {
        return addressRepo.findByUuidAndStatus(uuid,false)
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.DELETE_ADDRESS_ERROR_TAG, AppConfig.DELETE_ADDRESS_ERROR_RESPONSE),
                        HttpStatus.NOT_FOUND
                ));
    }

}
