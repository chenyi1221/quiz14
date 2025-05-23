package com.example.quiz14.vo;

import java.time.LocalDate;

public class SearchReq {

	private String quizName;

	private LocalDate startDate;

	private LocalDate endDate;

	private boolean forFrontEnd;

	public String getQuizName() {
		return quizName;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public boolean isForFrontEnd() {
		return forFrontEnd;
	}

	public void setForFrontEnd(boolean forFrontEnd) {
		this.forFrontEnd = forFrontEnd;
	}

}
