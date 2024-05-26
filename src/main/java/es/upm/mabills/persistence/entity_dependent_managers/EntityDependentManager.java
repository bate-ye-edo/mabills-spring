package es.upm.mabills.persistence.entity_dependent_managers;

public interface EntityDependentManager {
    <T> void decouple(T id);
}
