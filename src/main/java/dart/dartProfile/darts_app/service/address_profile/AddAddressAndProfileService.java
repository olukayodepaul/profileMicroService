package dart.dartProfile.darts_app.service.address_profile;

public class AddAddressAndProfileService {
}


//package dart.profileMicroservice.profile_and_address.add_profile_and_address_restful.service;
//
//import dart.profileMicroservice.profile_and_address.add_profile_and_address_restful.entity.AddUserAddressProfileReqModel;
//import dart.profileMicroservice.profile_and_address.add_profile_and_address_restful.entity.AddUserAddressProfileResModel;
//import dart.profileMicroservice.profile_and_address.add_profile_and_address_restful.repository.AddUserAddressProfileDAO;
//import dart.profileMicroservice.common.repository.UserProfileDAO;
//import dart.profileMicroservice.common.entity.AddressDbModel;
//import dart.profileMicroservice.common.entity.ProfileDbModel;
//import dart.profileMicroservice.common.CacheManager;
//import dart.profileMicroservice.util.CustomException;
//import dart.profileMicroservice.util.RegErrorHandler;
//import dart.profileMicroservice.util.UUIDManager;
//import jakarta.transaction.Transactional;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class AddUserAddressProfileImpl {
//
//    private final AddUserAddressProfileDAO userAddressDAO;
//    private final UserProfileDAO userProfileDAO;
//    private final UUIDManager uuidManager;
//    private final CacheManager cacheManager;
//
//    public AddUserAddressProfileImpl(
//            AddUserAddressProfileDAO userAddressDAO,
//            UserProfileDAO userProfileDAO,
//            UUIDManager uuidManager,
//            CacheManager cacheManager
//    )
//    {
//        this.userAddressDAO = userAddressDAO;
//        this.userProfileDAO = userProfileDAO;
//        this.uuidManager = uuidManager;
//        this.cacheManager = cacheManager;
//    }
//
//    @Transactional
//    public ResponseEntity<AddUserAddressProfileResModel> addProfile(AddUserAddressProfileReqModel request) {
//
//        if (
//                request == null
//                        || request.getUserID() == null
//                        || request.getProfile().getFirstName() == null
//                        || request.getProfile().getLastName() == null
//                        || request.getProfile().getPhoneNumber() == null
//                        || request.getProfile().getDateOfBirth() == null
//                        || request.getProfile().getGender() == null
//                        || request.getProfile().getBio() == null
//                        || request.getProfile().getAddresses() == null
//        ) {
//            throw new CustomException(
//                    new RegErrorHandler(false, "Invalid profile request data. Please fill in all required fields."),
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//
//        Optional<ProfileDbModel> response = userProfileDAO.findByUseridAndStatus(request.getUserID(), true);
//
//        if (response.isPresent()) {
//            throw new CustomException(
//                    new RegErrorHandler(false, "Profile already exists for this user."),
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//
//        ProfileDbModel profile = ProfileDbModel.builder()
//                .userid(request.getUserID())
//                .firstname(request.getProfile().getFirstName())
//                .lastname(request.getProfile().getLastName())
//                .phonenumber(request.getProfile().getPhoneNumber())
//                .dateofbirth(uuidManager.getDate(request.getProfile().getDateOfBirth()))
//                .gender(request.getProfile().getGender())
//                .bio(request.getProfile().getBio())
//                .status(true)
//                .build();
//
//        userProfileDAO.save(profile);
//
//        List<AddressDbModel> addresses = request.getProfile().getAddresses().stream()
//                .map(address -> AddressDbModel.builder()
//                        .userid(request.getUserID())
//                        .type(address.getType())
//                        .addressline1(address.getAddressline1())
//                        .addressline2(address.getAddressline2())
//                        .street(address.getStreet())
//                        .city(address.getCity())
//                        .state(address.getState())
//                        .zip(address.getZip())
//                        .country(address.getCountry())
//                        .build())
//                .collect(Collectors.toList());
//
//        userAddressDAO.saveAll(addresses);
//        cacheManager.saveProfileIntoRedis(profile);
//        cacheManager.saveAddressIntoRedis(addresses);
//
//        return new ResponseEntity<>(new AddUserAddressProfileResModel(true, "Profile and addresses created successfully."), HttpStatus.CREATED);
//    }
//}