package io.endeavour.stocks.repository.crud;

import io.endeavour.stocks.entity.crud.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
