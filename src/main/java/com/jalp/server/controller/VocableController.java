package com.jalp.server.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jalp.server.exception.ResourceNotFoundException;
import com.jalp.server.model.Vocable;
import com.jalp.server.repository.VocableRepository;

@RestController
public class VocableController {

	@Autowired
	private VocableRepository vocableRepository;

	@GetMapping("/vocabulary")
	public List<Vocable> getVocabulary() {
		return vocableRepository.findAll();
	}

	@PostMapping("/vocabulary/{value}")
	public Vocable addVocable(@PathVariable String value, @Valid @RequestBody Vocable vocable) {
		return vocableRepository.save(vocable);
	}

	@PutMapping("/vocabulary/{value}")
	public Vocable updateVocable(@PathVariable String value, @Valid @RequestBody Vocable vocable) {
		if (!vocableRepository.findByValue(value).isEmpty()) {
			throw new ResourceNotFoundException("Vocable not found with id " + value);
		}

		return addVocable(value, vocable);
	}

	@DeleteMapping("/vocabulary/{value}")
	public ResponseEntity<?> deleteVocable(@PathVariable String value) {
		List<Vocable> vocable = vocableRepository.findByValue(value);

		if (vocable.isEmpty()) {
			throw new ResourceNotFoundException("Vocable not found with id " + value);
		}

		vocableRepository.delete(vocable.get(0));
		return ResponseEntity.ok().build();

	}
}
