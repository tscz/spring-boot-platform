package com.jalp.server;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jalp.server.model.Vocable;
import com.jalp.server.repository.VocableRepository;

@SpringBootTest
public class ApplicationTests {

	@Autowired
	private VocableRepository vocableRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	@Transactional
	void answersCanBeReadFromDb() {
		System.out.println("Test started");

		Vocable vocable = new Vocable();
		vocable.setValue("식당");
		vocable.setTranslation("restaurant");

		vocableRepository.save(vocable);

		List<Vocable> vocabulary = vocableRepository.findAll();
		System.out.println("Number of Vocables: " + vocabulary.size());
		vocabulary.stream().forEach(a -> System.out.println(a));

	}

}
