package com.github.tscz.spring.platform.testentity;

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

import com.github.tscz.spring.platform.exception.ResourceNotFoundException;

@RestController
public class TestEntityController {

	@Autowired
	private TestEntityRepository testEntityRepository;

	@GetMapping("/testEntities")
	public List<TestEntity> getTestEntities() {
		return testEntityRepository.findAll();
	}

	@PostMapping("/testEntities")
	public TestEntity createTestEntity(@Valid @RequestBody TestEntity testEntity) {
		return testEntityRepository.save(testEntity);
	}

	@PutMapping("/testEntities/{testEntityId}")
	public TestEntity updateTestEntity(@PathVariable Long testEntityId,
			@Valid @RequestBody TestEntity testEntityRequest) {
		return testEntityRepository.findById(testEntityId).map(testEntity -> {
			testEntity.setTitle(testEntityRequest.getTitle());
			testEntity.setDescription(testEntityRequest.getDescription());
			return testEntityRepository.save(testEntity);
		}).orElseThrow(() -> new ResourceNotFoundException("TestEnity not found with id " + testEntityId));
	}

	@DeleteMapping("/testEntity/{testEntityId}")
	public ResponseEntity<?> deleteTestEntity(@PathVariable Long testEntityId) {
		return testEntityRepository.findById(testEntityId).map(testEntity -> {
			testEntityRepository.delete(testEntity);
			return ResponseEntity.ok().build();
		}).orElseThrow(() -> new ResourceNotFoundException("TestEnity not found with id " + testEntityId));
	}
}
