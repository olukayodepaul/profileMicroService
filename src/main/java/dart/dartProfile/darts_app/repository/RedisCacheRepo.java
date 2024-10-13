package dart.dartProfile.darts_app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import dart.dartProfile.darts_app.entity.*;
import dart.dartProfile.security.FilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class RedisCacheRepo {


    private static final Logger logger = LoggerFactory.getLogger(RedisCacheRepo.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final FilterService filterService;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY_PATTERN_PROFILE = "users_profile";
    private static final String REDIS_KEY_PATTERN_ADDRESS = "users_address";
    private static final boolean SAVE_UPDATE_SUCCESS = true;
    private static final boolean SAVE_UPDATE_FAILED = false;

    public RedisCacheRepo(RedisTemplate<String, Object> redisTemplate, FilterService filterService, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.filterService = filterService;
        this.objectMapper = objectMapper;
    }

    //this is being update by the kafka service to blacklist a token
    public void saveJWTBlackListedToken(String uuid, String token) {
        String subKey = "jwt_black_service/"+uuid;
        redisTemplate.opsForList().leftPush(subKey, token);
    }

    //the filter is using token blacklisted. no need to implement it within your application
    public boolean isTokenBlacklisted(String token) {
        try {
            String subKey = "jwt_black_service/" + filterService.extractUUID(token);
            List<Object> tokens = redisTemplate.opsForList().range(subKey, 0, -1);
            System.out.println(tokens);
            List<String> tokenList = tokens.stream().map(Object::toString).collect(Collectors.toList());

            if(tokenList.contains(token)) {
                return false; //mean it is available and reject login
            } else {
                return true;
            }
        }catch (Exception e) {
            logger.warn("RedisCacheRepo::isTokenBlacklisted kindly urgently fix, token black list not working");
            return false;
        }
    }

    public Boolean saveUpdateProfile(ProfileCacheModel profile) {
        try {

            // Sub-key for identifying the user by their email
            String subKey = profile.getUuid().toString();

            // Save or update user details in Redis hash
            redisTemplate.opsForHash().put(REDIS_KEY_PATTERN_PROFILE, subKey, profile);

            // Return success
            return SAVE_UPDATE_SUCCESS;
        } catch (Exception e) {
            // Log the error and return failure response
            logger.error("RedisCacheRepo::saveUpdateUserDetails - Error occurred while saving/updating user with email {}: {}", profile.getUuid(), e.getMessage());
            return SAVE_UPDATE_FAILED;
        }
    }

    public FetchProfileFromCacheModel fetchUserProfile(String uuid) {
        try {

            Object cachedObject = redisTemplate.opsForHash().get(REDIS_KEY_PATTERN_PROFILE, uuid);

            if (cachedObject == null) {
                return new FetchProfileFromCacheModel(false, 1, "No profile found", null);
            }

            // Convert the cached object to CacheModel
            ProfileCacheModel cacheModel = objectMapper.convertValue(cachedObject, ProfileCacheModel.class);
            return new FetchProfileFromCacheModel(true, 0, "", cacheModel);

        } catch (Exception e) {
            logger.error("RedisCacheService::fetchUserDetails - Error occurred while trying to fetch profile details ID {}: {}", uuid, e.getMessage());
            return new FetchProfileFromCacheModel(false, 3, e.getMessage(), new ProfileCacheModel());
        }
    }

    public boolean deleteProfile(String uuid) {
        try {

            Long result = redisTemplate.opsForHash().delete(REDIS_KEY_PATTERN_PROFILE, uuid);

            return result > 0;
        } catch (Exception e) {
            logger.error("RedisCacheRepo::deleteProfile - Error occurred while saving/updating user with email {}: {}", uuid, e.getMessage());
            return false;
        }
    }

    //address
    public boolean deleteSingleAddress(String uuid, String id) {
        try {
            Long result = redisTemplate.opsForHash().delete(REDIS_KEY_PATTERN_ADDRESS+uuid, id);
            return result > 0;
        } catch (Exception e) {
            logger.error("RedisCacheRepo::deleteAddress - Error occurred while saving/updating user with email {}: {}", uuid, e.getMessage());
            return false;
        }
    }

    public boolean deleteAllAddress(String uuid) {
        try {
            System.out.println(REDIS_KEY_PATTERN_ADDRESS+uuid);
            Boolean result = redisTemplate.delete(REDIS_KEY_PATTERN_ADDRESS+uuid);
            return result != null && result;
        } catch (Exception e) {
            logger.error("RedisCacheRepo::deleteAllAddress - Error occurred while saving/updating user with email {}: {}", uuid, e.getMessage());
            return false;
        }
    }

    public Boolean saveAddress(List<AddressCacheModel> addressModels,String uuid) {
        try {
            for (AddressCacheModel address : addressModels) {
                String subKey = String.valueOf(address.getId());
                redisTemplate.opsForHash().put(REDIS_KEY_PATTERN_ADDRESS+uuid, subKey, address);
            }
            return SAVE_UPDATE_SUCCESS;
        }catch (Exception e){
            logger.error("RedisCacheRepo::saveAddressIntoRedis - Error occurred while saving/updating user with email {}: {}", addressModels.getFirst(), e.getMessage());
            return SAVE_UPDATE_FAILED;
        }
    }

    public FetchAddressFromCacheModel findAllAddresses(String uuid) {
        try {
            String key = REDIS_KEY_PATTERN_ADDRESS + uuid;
            Map<Object, Object> addressMap = redisTemplate.opsForHash().entries(key);

            if (!addressMap.isEmpty()) {
                List<AddressCacheModel> addressList = addressMap.values().stream()
                        .map(value -> objectMapper.convertValue(value, AddressCacheModel.class))
                        .sorted()
                        .collect(Collectors.toList());
                return new FetchAddressFromCacheModel(true, "Addresses fetched successfully", addressList);
            }

            return new FetchAddressFromCacheModel(false, "No addresses found", Collections.emptyList());

        } catch (Exception e) {
            logger.error("Error fetching addresses for uuid {}: {}", uuid, e.getMessage());
            return new FetchAddressFromCacheModel(false, e.getMessage(), Collections.emptyList());
        }
    }

}
