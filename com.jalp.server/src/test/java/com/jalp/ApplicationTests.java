package com.jalp;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.jalp.model.Answer;
import com.jalp.model.Question;
import com.jalp.repository.AnswerRepository;
import com.jalp.repository.QuestionRepository;

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
