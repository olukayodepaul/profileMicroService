package dart.dartProfile.darts_app.repository;

import dart.dartProfile.darts_app.entity.AddressDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface AddressRepo extends JpaRepository<AddressDbModel, Long> {
    Optional<AddressDbModel> findByIdAndUuid(Integer id, UUID uuid);
    Optional<List<AddressDbModel>> findByUuidAndStatus(UUID uuid, boolean status);
}
