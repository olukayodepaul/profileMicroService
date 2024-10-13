package dart.dartProfile.darts_app.controller;


import dart.dartProfile.darts_app.service.address_profile.AddAddressAndProfileService;
import dart.dartProfile.darts_app.service.address_profile.FetchAddressAndProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class AddressAndProfileService {

    private final AddAddressAndProfileService addAddressAndProfileService;
    private final FetchAddressAndProfileService fetchAddressAndProfileService;

    public AddressAndProfileService(
            AddAddressAndProfileService addAddressAndProfileService,
            FetchAddressAndProfileService fetchAddressAndProfileService
    ) {
        this.addAddressAndProfileService = addAddressAndProfileService;
        this.fetchAddressAndProfileService = fetchAddressAndProfileService;
    }

//    @PostMapping("/profile/address")
//    public ResponseEntity<AddUserAddressProfileResModel> addProfile(@RequestBody AddUserAddressProfileReqModel request) {
//        return  userProfileImpl.addProfile(request);
//    }

//    @GetMapping("/profile/address")
//    public ResponseEntity<FetchProfileAddressResModel> fetchProfile(@PathVariable("profileId") Integer profileId){
//        return fetchProfileAddressImpl.fetchProfile(profileId);
//    }

}
