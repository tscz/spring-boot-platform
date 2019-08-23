package com.jalp.server.vocable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocableRepository extends JpaRepository<Vocable, Long> {

	List<Vocable> findByValue(String value);

}
