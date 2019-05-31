package com.jalp.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jalp.server.model.Vocable;

@Repository
public interface VocableRepository extends JpaRepository<Vocable, Long> {

	List<Vocable> findByValue(String value);

}
