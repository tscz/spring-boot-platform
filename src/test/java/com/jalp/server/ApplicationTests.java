package com.jalp.server;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jalp.server.model.Answer;
import com.jalp.server.model.Question;
import com.jalp.server.repository.AnswerRepository;
import com.jalp.server.repository.QuestionRepository;

@SpringBootTest()
public class ApplicationTests {
	
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;
    

	@Test
	public void contextLoads() {
	}


	@Test
	@Transactional
	void answersCanBeReadFromDb() {
		System.out.println("Test started");
		
		Question question = new Question();
		question.setTitle("Frage");
		
		
		Answer answer = new Answer();
		answer.setQuestion(question);
		
		
		questionRepository.save(question);
		answerRepository.save(answer);

		
		List<Answer> answers = answerRepository.findAll();
		System.out.println("Anzahl Antworten: " + answers.size());
		answers.stream().forEach(a -> System.out.println(a));
		
	}
	
	
}
