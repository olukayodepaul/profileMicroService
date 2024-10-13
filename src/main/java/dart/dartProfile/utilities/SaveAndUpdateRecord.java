package dart.dartProfile.utilities;

import dart.dartProfile.darts_app.entity.AddressDbModel;
import dart.dartProfile.darts_app.entity.ProfileDbModel;
import dart.dartProfile.darts_app.entity.SaveAddress;
import dart.dartProfile.darts_app.entity.UpdateAddress;
import dart.dartProfile.darts_app.repository.AddressRepo;
import dart.dartProfile.darts_app.repository.ProfileRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SaveAndUpdateRecord {

    private final ProfileRepo databaseRep;
    private final AddressRepo addressRepo;
    private static final Logger logger = LoggerFactory.getLogger(SaveAndUpdateRecord.class);

    public SaveAndUpdateRecord(ProfileRepo databaseRep, AddressRepo addressRepo) {
        this.databaseRep = databaseRep;
        this.addressRepo = addressRepo;
    }

    public SaveAndUpdateProfileResponse saveUpdatedProfileRecord(ProfileDbModel regDetails) {
        try {
            return new SaveAndUpdateProfileResponse(true, "", databaseRep.save(regDetails)) ;
        } catch (Exception e) {
            logger.error("DbSaveUpdatedService::saveUpdatedUserDetails: {}", e.getMessage());
            return new SaveAndUpdateProfileResponse(false, e.getMessage(), ProfileDbModel.builder().build());
        }
    }

    public SaveAddress saveAddressRecord(List<AddressDbModel> regDetails) {
        try {
            return new SaveAddress(true, "", addressRepo.saveAll(regDetails));
        } catch (Exception e) {
            logger.error("DbSaveUpdatedService::saveAddressRecord: {}", e.getMessage());
            return new SaveAddress(false, e.getMessage(), new ArrayList<>());
        }
    }

    public UpdateAddress updateAddressRecord(AddressDbModel regDetails) {
        try {
            return new UpdateAddress(true, "", addressRepo.save(regDetails));
        } catch (Exception e) {
            logger.error("DbSaveUpdatedService::saveUpdatedAddressRecord: {}", e.getMessage());
            return new UpdateAddress(false, e.getMessage(), AddressDbModel.builder().build());
        }
    }

}
