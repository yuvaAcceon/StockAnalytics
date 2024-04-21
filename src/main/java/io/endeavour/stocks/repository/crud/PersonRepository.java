package io.endeavour.stocks.repository.crud;

import io.endeavour.stocks.entity.crud.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
