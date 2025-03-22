package com.fuzis.techtask.Testing;

import com.fuzis.techtask.Entities.Client;
import com.fuzis.techtask.Repositories.IClientRepository;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для тестирования методов с использованием БД с Entity Client. Не создает реальную базу данных,
 * все данные сохраняются в {@code HashMap} внутри объекта данного репозитория. Некоторые методы могут иметь асимптотику
 * вплоть до O(n), где n - количество записей в {@code HashMap}.
 */
public class TestingClientRepo implements IClientRepository {
    HashMap<Integer, Client> fakeDatabase;
    private Integer lastId;

    public TestingClientRepo() {
        fakeDatabase = new HashMap<>();
        lastId = 0;
    }

    private synchronized int nextId() {
        return ++lastId;
    }

    @NonNull
    @Override
    public <S extends Client> S save(S entity) {
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
    public <S extends Client> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    @NonNull
    @Override
    public Optional<Client> findById(@NonNull Integer integer) {
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
    public Iterable<Client> findAll() {
        return fakeDatabase.values();
    }

    @NonNull
    @Override
    public Iterable<Client> findAllById(Iterable<Integer> integers) {
        List<Client> clients = new LinkedList<>();
        for (Integer integer : integers) {
            Optional<Client> findResult = findById(integer);
            findResult.ifPresent(clients::add);
        }
        return clients;
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
    public void delete(Client entity) {
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
    public void deleteAll(Iterable<? extends Client> entities) {
        for (Client entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        fakeDatabase.clear();
    }

    @NonNull
    @Override
    public Optional<Client> findByPhoneNumber(String phoneNumber) {
        for (Client client : fakeDatabase.values()) {
            if (client.getPhoneNumber().equals(phoneNumber)) {
                return Optional.of(client);
            }
        }
        return Optional.empty();
    }
}
