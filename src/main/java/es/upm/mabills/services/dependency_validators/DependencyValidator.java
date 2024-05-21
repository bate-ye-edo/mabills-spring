package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.model.UserPrincipal;
import org.springframework.transaction.annotation.Transactional;

public interface DependencyValidator {
    @Transactional
    <T> void assertDependencies(UserPrincipal userPrincipal, T model);
}
