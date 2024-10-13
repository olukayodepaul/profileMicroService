package dart.dartProfile.utilities;



import dart.dartProfile.darts_app.entity.AddProfileReqResModel;
import dart.dartProfile.darts_app.rate_limit.BruteForceRateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class ValidationUtils {

    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder encoder;
    private final BruteForceRateLimitService rateLimitService;

    public ValidationUtils(
            EmailValidator emailValidator,
            BruteForceRateLimitService rateLimitService
    ) {
        this.emailValidator = emailValidator;
        this.encoder = new BCryptPasswordEncoder(12);
        this.rateLimitService = rateLimitService;
    }


    public void sanitizeEmail(String email) {
        if (!emailValidator.isValid(email)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Invalid email format"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validatePasswordStrength(String password) {

        if (password.length() < 8) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must be at least 8 characters long."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one uppercase letter."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one lowercase letter."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*\\d.*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one digit."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one special character (@#$%^&+=!)."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void profileValidateRequest(AddProfileReqResModel request) {
        validateField(request.getFirst_name(), "First Name");
        validateField(request.getLast_name(), "Last Name");
        validateField(request.getPhone_number(), "Phone Number");
        validateField(request.getDate_of_birth(), "Date of Birth");
        validateField(request.getGender(), "Gender");
        validateField(request.getBio(), "Bio");
    }

    public void profileIdValidateRequest(String token) {
        validateField(token, "Access Token cant be null" );
    }

    public void jwtValidateRequest(String token) {
        validateField(token, "Access Token cant be null" );
    }


    public void addressIdValidateRequest(int id) {
        validateField(id, "Address id can't be null" );
    }

    public static void validateField(Object field, String fieldName) {
        if (field == null) {
            throw new CustomRuntimeException(new ErrorHandler(false, "validation error", fieldName + " cannot be null"), HttpStatus.BAD_REQUEST);
        }
    }


    public void bruteForceProtection(String uuid) {
        if (rateLimitService.isRateLimited(uuid)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "Rate limit exceeded", "You have exceeded the maximum number of requests per minute."),
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }
    }


}