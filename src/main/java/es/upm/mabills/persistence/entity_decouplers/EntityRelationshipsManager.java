package es.upm.mabills.persistence.entity_decouplers;

public interface EntityRelationshipsManager {
    <T> void decouple(T id);
}
