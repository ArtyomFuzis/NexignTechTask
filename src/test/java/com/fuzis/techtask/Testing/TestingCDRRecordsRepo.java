package com.fuzis.techtask.Testing;

import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Repositories.ICDRRecordRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TestingCDRRecordsRepo implements ICDRRecordRepository {
    HashMap<Integer, CDRRecord> fakeDatabase;
    private Integer lastId;

    public TestingCDRRecordsRepo() {
        fakeDatabase = new HashMap<>();
        lastId = 0;
    }

    private synchronized int nextId() {
        return ++lastId;
    }

    @NonNull
    @Override
    public <S extends CDRRecord> S save(S entity) {
        if (entity.getId() == null) {
            int id = nextId();
            entity.setId(id);
            fakeDatabase.put(id, entity);
        }
        fakeDatabase.put(entity.getId(), entity);
        return entity;
    }

    @NonNull
    @Override
    public <S extends CDRRecord> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    @NonNull
    @Override
    public Optional<CDRRecord> findById(@NonNull Integer integer) {
        if (fakeDatabase.containsKey(integer)) {
            return Optional.of(fakeDatabase.get(integer));
        }
        return Optional.empty();
    }

    @NonNull
    @Override
    public boolean existsById(@NonNull Integer integer) {
        return fakeDatabase.containsKey(integer);
    }

    @NonNull
    @Override
    public Iterable<CDRRecord> findAll() {
        return fakeDatabase.values();
    }

    @NonNull
    @Override
    public Iterable<CDRRecord> findAllById(Iterable<Integer> integers) {
        List<CDRRecord> CDRs = new LinkedList<>();
        for (Integer integer : integers) {
            Optional<CDRRecord> findResult = findById(integer);
            findResult.ifPresent(CDRs::add);
        }
        return CDRs;
    }

    @Override
    public long count() {
        return fakeDatabase.size();
    }

    @Override
    public void deleteById(@NonNull Integer integer) {
        fakeDatabase.remove(integer);
    }

    @Override
    public void delete(CDRRecord entity) {
        Integer id = entity.getId();
        if (id != null) {
            deleteById(id);
        }
        throw new IllegalArgumentException("Entity id is null. Test database does not provide search functionality.");
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {
        for (Integer integer : integers) {
            deleteById(integer);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends CDRRecord> entities) {
        for (CDRRecord entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        fakeDatabase.clear();
    }

    @Override
    public List<CDRRecord> getCDRRecordsByClientPhoneNumberAndTimeStartAfter(String fromPhoneNumber, LocalDateTime timeStartAfter) {
        List<CDRRecord> CDRs = new LinkedList<>();
        for(var val : fakeDatabase.values()) {
            if(val.getClientPhoneNumber().equals(fromPhoneNumber) && val.getTimeStart().isAfter(timeStartAfter)) {
                CDRs.add(val);
            }
        }
        return CDRs;
    }
}

