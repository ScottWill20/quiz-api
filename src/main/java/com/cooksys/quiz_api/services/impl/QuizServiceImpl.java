package com.cooksys.quiz_api.services.impl;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final QuizMapper quizMapper;
  private final QuestionMapper questionMapper;


  private Quiz getQuiz(Long id) {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    return optionalQuiz.get();
  }

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAll());
  }

  @Override
  public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {
    Quiz quiz = quizMapper.requestDtoToEntity(quizRequestDto);
    quizRepository.saveAndFlush(quiz);

    List<Question> questionsToAdd = quiz.getQuestions();
    questionRepository.saveAllAndFlush(questionsToAdd);

    for (Question q : questionsToAdd) {
      q.setQuiz(quiz);
      for (Answer a : q.getAnswers()) {
        a.setQuestion(q);
      }
      answerRepository.saveAllAndFlush(q.getAnswers());
    }
    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuizResponseDto deleteQuiz(Long id) {

    Quiz quizToDelete = quizRepository.getById(id);

    List<Question> questionsToDelete = quizToDelete.getQuestions();

    for (Question q : questionsToDelete) {
      for (Answer a : q.getAnswers()) {
        answerRepository.delete(a);
      }
      questionRepository.delete(q);
    }
    quizRepository.delete(quizToDelete);

    return quizMapper.entityToDto(quizToDelete);
  }

  @Override
  public QuizResponseDto updateQuiz(Long id, String newName) {

    Quiz quizToUpdate = getQuiz(id);
    quizToUpdate.setName(newName);

    quizRepository.saveAndFlush(quizToUpdate);

    return quizMapper.entityToDto(quizToUpdate);
  }

  @Override
  public QuestionResponseDto getRandomQuestionFromQuiz(Long id) {
    Random rand = new Random();
    Quiz quiz = getQuiz(id);
    List<Question> randQuestions = quiz.getQuestions();

    Question randQ = randQuestions.get(rand.nextInt(randQuestions.size()));

    return questionMapper.entityToDto(randQ);

  }

  @Override
  public QuizResponseDto addQuestionToQuiz(Long id, QuestionRequestDto questionRequestDto) {
    Quiz quiz = getQuiz(id);
    quizRepository.saveAndFlush(quiz);
    List<Question> quizQuestions = quiz.getQuestions();
    Question newQuestion = questionMapper.requestDtoToEntity(questionRequestDto);
    quizQuestions.add(newQuestion);

    questionRepository.saveAllAndFlush(quizQuestions);

    for (Question q : quizQuestions) {
      q.setQuiz(quiz);
      for (Answer a : q.getAnswers()) {
        a.setQuestion(q);
      }
      answerRepository.saveAllAndFlush(q.getAnswers());
    }

    return quizMapper.entityToDto(quizRepository.saveAndFlush(quiz));
  }

  @Override
  public QuestionResponseDto deleteQuestionFromQuiz(Long id, Long questionID) { // DONE
    Quiz quiz = getQuiz(id);
    List<Question> quizQuestions = quiz.getQuestions();

    Question instance = new Question();

    for (Question q : quizQuestions) {
      if (questionID == q.getId()) {
        instance = q;
        for (Answer a : q.getAnswers()) {
          answerRepository.delete(a);
        }
        questionRepository.delete(q);
      }
    }
    return questionMapper.entityToDto(instance);

  }
}
