package es.upm.mabills.persistence.entity_decouplers;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface EntityDependentManager {
    @Modifying
    @Transactional
    <T> void decouple(T id);
}
