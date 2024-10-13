package dart.dartProfile.utilities;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.parameters.P;

@Configuration
public class AppConfig {



    public static final String KEY_ERROR = "Validation error";
    public static final String INVALID_EMAIL = "Invalid email";
    public static final String INVALID_UUID = "uuid error";

    //bruce force protection
    public static final String ADD_PROFILE_BRUTE_FORCE_PROTECTION = "add_profile_brute_force_protection";
    public static final String DELETE_PROFILE_BRUTE_FORCE_PROTECTION = "delete_profile_brute_force_protection";
    public static final String FETCH_PROFILE_BRUTE_FORCE_PROTECTION = "fetch_profile_brute_force_protection";
    public static final String UPDATE_PROFILE_BRUTE_FORCE_PROTECTION = "update_profile_brute_force_protection";
    public static final String ADD_ADDRESS_BRUTE_FORCE_PROTECTION = "add_address_brute_force_protection";
    public static final String UPDATE_ADDRESS_BRUTE_FORCE_PROTECTION = "update_address_brute_force_protection";

    //Request Response
    public static final String FETCH_PROFILE_RESPONSE = "Profile fetched successfully.";

    //language can be changed from here.... depend on the local language picked
    //Response Message PROFILE
    public static final String ADD_PROFILE_ERROR_TAG = "error checking";
    public static final String ADD_PROFILE_ERROR_RESPONSE = "User profile already exist on our system";
    public static final String ADD_PROFILE_CANT_BE_SAVE_ERROR = "User profile cant be save at this moment, please try again.";
    public static final String UPDATE_PROFILE_CANT_BE_SAVE_ERROR = "User profile cant be updated at this moment, please try again.";
    public static final String ADD_PROFILE_SAVE_RESPONSE = "User Profile created successfully";


    //Response Message ADDRESS
    public static final String ADD_ADDRESS_CANT_BE_SAVE_ERROR = "User address cant be save at this moment, please try again.";
    public static final String ADD_ADDRESS_SAVE_RESPONSE = "User address created successfully";
    public static final String DELETE_ADDRESS_BRUTE_FORCE_PROTECTION = "add_address_brute_force_protection";
    public static final String FETCH_ADDRESS_BRUTE_FORCE_PROTECTION = "add_address_brute_force_protection";
    public static final String DELETE_ADDRESS_ERROR_TAG = "error checking";
    public static final String DELETE_ADDRESS_ERROR_RESPONSE = "Invalid address id";


    //Response Message DELETE PROFILE.
    public static final String DELETE_PROFILE_ERROR_TAG = "error checking";
    public static final String DELETE_PROFILE_ERROR_RESPONSE = "Invalid Access token";
    public static final String DELETE_PROFILE_DELETED_ERROR_RESPONSE = "User Profile cant be deleted at the moment, kindly try again";
    public static final String DELETE_PROFILE_PREVIOUSLY_DELETE = "User Profile previously delete";
    public static final String DELETE_PROFILE_SUCCESSFUL = "Profile deleted successfully";
    public static final String UPDATE_PROFILE_SUCCESSFUL = "Profile updated successfully";

}
