package com.example.quiz14.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FeedbackId implements Serializable {

	private String email;
	
	private int quizId;
	
	private int quesId;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getQuesId() {
		return quesId;
	}

	public void setQuesId(int quesId) {
		this.quesId = quesId;
	}
	
}
