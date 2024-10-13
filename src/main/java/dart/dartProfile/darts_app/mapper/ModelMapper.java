package dart.dartProfile.darts_app.mapper;

import dart.dartProfile.darts_app.entity.*;
import dart.dartProfile.utilities.UtilitiesManager;
import darts.grpc.server.UserProfileOuterClass;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class ModelMapper {

    private final UtilitiesManager utilitiesManager;

    public ModelMapper(UtilitiesManager utilitiesManager) {
        this.utilitiesManager = utilitiesManager;
    }

    public ProfileDbModel toProfileModel(UserProfileOuterClass.Profile profile) {
        return ProfileDbModel.builder()
                .uuid(UUID.fromString(profile.getUuid()))
                .first_name(profile.getFirstName())
                .last_name(profile.getLastName())
                .phone_number(profile.getPhoneNumber())
                .organisation_id(profile.getOrganisationId())
                .date_of_birth(utilitiesManager.convertStringToDate(profile.getDateOfBirth()))
                .gender(profile.getGender())
                .bio(profile.getBio())
                .build();
    }

    public UserProfileOuterClass.Profile toGrpcProfile(ProfileDbModel profileModel) {
        return UserProfileOuterClass.Profile.newBuilder()
                .setUuid(profileModel.getUuid().toString())
                .setFirstName(profileModel.getFirst_name())
                .setLastName(profileModel.getLast_name())
                .setPhoneNumber(profileModel.getPhone_number())
                .setOrganisationId(profileModel.getOrganisation_id())
                .setDateOfBirth(profileModel.getDate_of_birth().toString())
                .setGender(profileModel.getGender())
                .setBio(profileModel.getBio())
                .build();
    }

    public ProfileDbModel toCreateNewProfile(AddProfileReqResModel profile) {
        return ProfileDbModel.builder()
                .uuid(utilitiesManager.convertStringToUUID(profile.getUuid()))
                .first_name(profile.getFirst_name())
                .last_name(profile.getLast_name())
                .phone_number(profile.getPhone_number())
                .organisation_id(profile.getOrganisation_id())
                .date_of_birth(utilitiesManager.convertDateOfBirth(profile.getDate_of_birth()))
                .gender(profile.getGender())
                .bio(profile.getBio())
                .created_at(profile.getCreated_at())
                .updated_at(profile.getUpdated_at())
                .build();
    }

    public ProfileCacheModel toCacheFromProfile(ProfileDbModel profile) {
        return ProfileCacheModel.builder()
                .id(profile.getId())
                .uuid(profile.getUuid())
                .first_name(profile.getFirst_name())
                .last_name(profile.getLast_name())
                .phone_number(profile.getPhone_number())
                .organisation_id(profile.getOrganisation_id())
                .date_of_birth(profile.getDate_of_birth())
                .gender(profile.getGender())
                .bio(profile.getBio())
                .status(profile.isStatus())
                .updated_at(profile.getUpdated_at())
                .created_at(profile.getCreated_at())
                .build();
    }

    public ProfileDbModel toProfileFromCache(ProfileCacheModel profile) {
        return ProfileDbModel.builder()
                .uuid(profile.getUuid())
                .first_name(profile.getFirst_name())
                .last_name(profile.getLast_name())
                .phone_number(profile.getPhone_number())
                .organisation_id(profile.getOrganisation_id())
                .date_of_birth(profile.getDate_of_birth())
                .gender(profile.getGender())
                .bio(profile.getBio())
                .status(profile.isStatus())
                .updated_at(profile.getUpdated_at())
                .created_at(profile.getCreated_at())
                .build();
    }

    public ProfileDbModel profileBuilder(ProfileDbModel existingUser) {
        return ProfileDbModel
                .builder()
                .id(existingUser.getId())
                .uuid(existingUser.getUuid())
                .first_name(existingUser.getFirst_name())
                .last_name(existingUser.getLast_name())
                .phone_number(existingUser.getPhone_number())
                .organisation_id(existingUser.getOrganisation_id())
                .date_of_birth(existingUser.getDate_of_birth())
                .gender(existingUser.getGender())
                .bio(existingUser.getBio())
                .status(true)
                .updated_at(LocalDateTime.now())
                .created_at(existingUser.getCreated_at())
                .build();
    }

    public List<AddressDbModel> toAddAddress(List<AddAddressReqModel> reqModels, UUID uuid, String organisationId) {
        return reqModels.stream()
                .map(reqModel -> AddressDbModel.builder()
                        .uuid(uuid)
                        .organisation_id(organisationId)
                        .address_line1(reqModel.getAddress_line1())
                        .address_line2(reqModel.getAddress_line2())
                        .street(reqModel.getStreet())
                        .city(reqModel.getCity())
                        .state(reqModel.getState())
                        .zip(reqModel.getZip())
                        .country(reqModel.getCountry())
                        .type(reqModel.getType())
                        .status(false)
                        .created_at(LocalDateTime.now())
                        .updated_at(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    public List<AddressCacheModel> toAddAddressCache(List<AddressDbModel> reqModels) {
        return reqModels.stream()
                .map(reqModel -> AddressCacheModel.builder()
                        .id(reqModel.getId())
                        .organisation_id(reqModel.getOrganisation_id())
                        .address_line1(reqModel.getAddress_line1())
                        .address_line2(reqModel.getAddress_line2())
                        .street(reqModel.getStreet())
                        .city(reqModel.getCity())
                        .state(reqModel.getState())
                        .zip(reqModel.getZip())
                        .country(reqModel.getCountry())
                        .status(String.valueOf(reqModel.isStatus()))
                        .type(reqModel.getType())
                        .build())
                .collect(Collectors.toList());
    }

    public AddressDbModel toUpdateAddress(AddressDbModel address, boolean status) {

        return AddressDbModel.builder()
                .id(address.getId())
                .uuid(address.getUuid())
                .organisation_id(address.getOrganisation_id())
                .address_line1(address.getAddress_line1())
                .address_line2(address.getAddress_line2())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zip(address.getZip())
                .country(address.getCountry())
                .type(address.getType())
                .status(status)
                .created_at(address.getCreated_at())
                .updated_at(LocalDateTime.now())
                .build();
    }

}
