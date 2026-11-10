package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.repository.AccessRecordRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryAccessRecordRepository extends InMemoryRepository<AccessRecord>
        implements AccessRecordRepository {

    public InMemoryAccessRecordRepository() {
        super();
    }

    public InMemoryAccessRecordRepository(Map<UUID, AccessRecord> store, Runnable onChange) {
        super(store, onChange);
    }

    @Override
    public AccessRecord save(AccessRecord record) {
        return put(record.id(), record);
    }

    @Override
    public Optional<AccessRecord> findById(UUID id) {
        return get(id);
    }

    @Override
    public List<AccessRecord> findAll() {
        return values();
    }
}
