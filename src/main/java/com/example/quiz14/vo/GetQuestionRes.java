package com.example.quiz14.vo;

import java.util.List;

import com.example.quiz14.entity.Question;
import com.example.quiz14.entity.Quiz;

public class GetQuestionRes extends BasicRes {

	private List<QuestionVo> questionList;
	
	private Quiz quiz;

	public GetQuestionRes() {
		super();
	}

	public GetQuestionRes(int code, String message) {
		super(code, message);
	}

	public GetQuestionRes(int code, String message, Quiz quiz, List<QuestionVo> questionList) {
		super(code, message);
		this.quiz = quiz;
		this.questionList = questionList;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public List<QuestionVo> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List<QuestionVo> questionList) {
		this.questionList = questionList;
	}

}
