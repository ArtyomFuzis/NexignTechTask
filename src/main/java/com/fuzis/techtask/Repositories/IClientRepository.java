package com.fuzis.techtask.Repositories;

import com.fuzis.techtask.Entities.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClientRepository extends CrudRepository<Client, Integer>
{
    Optional<Client> findByPhoneNumber(String number);
}
