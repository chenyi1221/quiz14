package com.example.quiz14.vo;

import com.example.quiz14.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;

public class UpdateReq extends CreateReq {
	
	@Min(value = 1, message = ConstantsMessage.PARAM_QUIZ_ID_ERROR)
	public int quizId;

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

}
