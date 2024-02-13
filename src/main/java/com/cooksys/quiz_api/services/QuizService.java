package com.cooksys.quiz_api.services;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;

import java.util.List;

public interface QuizService {

  List<QuizResponseDto> getAllQuizzes();

  QuizResponseDto createQuiz(QuizRequestDto quizRequestDto);

  QuizResponseDto deleteQuiz(Long id);

  QuizResponseDto updateQuiz(Long id, String newName);

  QuestionResponseDto getRandomQuestionFromQuiz(Long id);

  QuizResponseDto addQuestionToQuiz(Long id, QuestionRequestDto questionRequestDto);

  QuestionResponseDto deleteQuestionFromQuiz(Long id, Long questionID);
}
