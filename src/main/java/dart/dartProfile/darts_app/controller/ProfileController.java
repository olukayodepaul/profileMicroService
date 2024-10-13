package dart.dartProfile.darts_app.controller;



import dart.dartProfile.darts_app.entity.AddProfileReqResModel;
import dart.dartProfile.darts_app.entity.FetchProfileResModel;
import dart.dartProfile.darts_app.service.profile.AddProfileRestfulService;
import dart.dartProfile.darts_app.service.profile.DeleteProfileService;
import dart.dartProfile.darts_app.service.profile.FetchProfileService;
import dart.dartProfile.darts_app.service.profile.UpdateProfileService;
import dart.dartProfile.utilities.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final AddProfileRestfulService addProfileRestfulService;
    private final DeleteProfileService deleteProfileService;
    private final FetchProfileService fetchProfileService;
    private final UpdateProfileService updateProfile;


    public ProfileController(
            AddProfileRestfulService addProfileRestfulService,
            DeleteProfileService deleteProfileService,
            FetchProfileService fetchProfileService,
            UpdateProfileService updateProfile
    ) {
        this.addProfileRestfulService = addProfileRestfulService;
        this.deleteProfileService = deleteProfileService;
        this.fetchProfileService = fetchProfileService;
        this.updateProfile = updateProfile;
    }

    @PostMapping("/profile")
    public ResponseEntity<ResponseHandler> addProfile(
            @RequestBody AddProfileReqResModel body,
            @RequestHeader("Authorization") String token
    ) {
        return addProfileRestfulService.addProfile(body, token);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ResponseHandler> deleteProfile(
            @RequestHeader("Authorization") String token

    ) {
        return deleteProfileService.deleteProfile( token);
    }

    @GetMapping("/profile")
    public ResponseEntity<FetchProfileResModel> fetchUserProfile(@RequestHeader("Authorization") String token){
        return fetchProfileService.fetchUserProfile(token);
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseHandler> getUpdate(
            @RequestBody AddProfileReqResModel request,
            @RequestHeader("Authorization") String token
    )
    {
        return updateProfile.updateProfile(request, token);
    }
}
