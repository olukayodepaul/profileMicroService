package dart.dartProfile.darts_app.service.address_profile;

public class FetchAddressAndProfileService {
}


//package dart.profileMicroservice.profile_and_address.fetch_profile_and_address.service;
//
//
//import dart.profileMicroservice.address.fetch_address.entity.FetchAddressRedisResModel;
//import dart.profileMicroservice.common.CacheManager;
//import dart.profileMicroservice.common.entity.AddressDbModel;
//import dart.profileMicroservice.common.entity.ProfileDbModel;
//import dart.profileMicroservice.common.entity.MapProfileResModel;
//import dart.profileMicroservice.profile_and_address.fetch_profile_and_address.entity.FetchAddressModel;
//import dart.profileMicroservice.profile_and_address.fetch_profile_and_address.entity.FetchProfileAddressResModel;
//import dart.profileMicroservice.profile_and_address.fetch_profile_and_address.entity.FetchProfileModel;
//import dart.profileMicroservice.profile_and_address.fetch_profile_and_address.entity.ProfileAddressBuilder;
//import dart.profileMicroservice.profile_and_address.fetch_profile_and_address.repository.FetchAddressOnProfileAddressUpdateDAO;
//import dart.profileMicroservice.profile_and_address.fetch_profile_and_address.repository.FetchProfileOnProfileAddressUpdateDAO;
//import dart.profileMicroservice.util.CustomException;
//import dart.profileMicroservice.util.RegErrorHandler;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//
//@Service
//public class FetchProfileAddressImpl {
//
//    private final CacheManager cacheManager;
//    private final FetchProfileOnProfileAddressUpdateDAO fetchProfileDAO;
//    private final ProfileAddressBuilder profileAddressBuilder;
//    private final FetchAddressOnProfileAddressUpdateDAO fetchAddressDAO;
//    private static final int userId = 1;
//
//    public FetchProfileAddressImpl(
//            CacheManager cacheManager,
//            FetchProfileOnProfileAddressUpdateDAO fetchProfileDAO,
//            ProfileAddressBuilder profileAddressBuilder,
//            FetchAddressOnProfileAddressUpdateDAO fetchAddressDAO)
//    {
//        this.cacheManager = cacheManager;
//        this.fetchProfileDAO = fetchProfileDAO;
//        this.profileAddressBuilder = profileAddressBuilder;
//        this.fetchAddressDAO = fetchAddressDAO;
//    }
//
//    public ResponseEntity<FetchProfileAddressResModel> fetchProfile(Integer profileId) {
//
//        if (profileId == null) {
//            throw new CustomException(
//                    new RegErrorHandler(false, "Invalid profile request data. Please fill in all required fields."),
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//
//        MapProfileResModel cachedProfile = cacheManager.findProfile(userId, profileId);
//        FetchAddressRedisResModel cachedAddress = cacheManager.findAllAddresses(userId);
//
//        FetchProfileModel profileReAllocation;
//        List<FetchAddressModel> addressReAllocation;
//
//
//        if (cachedProfile.isFoundInCache()) {
//            profileReAllocation = profileAddressBuilder.createProfileFromCache(cachedProfile);
//        } else {
//
//            Optional<ProfileDbModel> dbProfile = fetchProfileDAO.findByUseridAndIdAndStatus(userId, profileId, true);
//
//            if (dbProfile.isPresent()) {
//                profileReAllocation = profileAddressBuilder.fetchProfileFromDatabase(dbProfile.get());
//            }else{
//                profileReAllocation =  FetchProfileModel.builder().build();
//            }
//        }
//
//        if (cachedAddress.getIsOnlineOffline()) {
//            addressReAllocation = profileAddressBuilder.fetchAddressFromCache(cachedAddress.getRedisRes());
//        }else{
//
//            Optional<List<AddressDbModel>> dbAddress = fetchAddressDAO.findByUseridAndStatus(userId, true);
//
//            if(dbAddress.isPresent()) {
//                addressReAllocation = profileAddressBuilder.fetchAddressFromDatabase(dbAddress.get());
//            }else{
//                addressReAllocation = Collections.singletonList(FetchAddressModel.builder().build());
//            }
//
//        }
//
//        return new ResponseEntity<>(new FetchProfileAddressResModel(
//                true, "Successful", profileReAllocation, addressReAllocation
//        ), HttpStatus.OK);
//    }
//}