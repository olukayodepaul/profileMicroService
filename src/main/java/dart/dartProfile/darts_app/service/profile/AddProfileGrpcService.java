package dart.dartProfile.darts_app.service.profile;


import dart.dartProfile.darts_app.entity.ProfileDbModel;
import dart.dartProfile.darts_app.repository.ProfileRepo;
import dart.dartProfile.utilities.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AddProfileGrpcService {

    private final ProfileRepo profileRepo;
    private final UtilitiesManager utilitiesManager;

    public AddProfileGrpcService(ProfileRepo profileRepo, UtilitiesManager utilitiesManager) {
        this.profileRepo = profileRepo;
        this.utilitiesManager = utilitiesManager;
    }

    @Transactional
    public ResponseHandler getUserProfile(ProfileDbModel profileModel) {
        try{

            if (profileRepo.findByUuid(profileModel.getUuid()).isPresent()) {
                return new ResponseHandler(true, "");
            }

            profileRepo.save(profileModel);
            return new ResponseHandler(true, "");

        } catch (Exception e) {
            profileRepo.save(profileModel);
            return new ResponseHandler(false, e.getMessage());
        }
    }

}
