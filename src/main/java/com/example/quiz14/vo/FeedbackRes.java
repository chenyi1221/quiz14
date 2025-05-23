package com.example.quiz14.vo;

import java.util.List;

public class FeedbackRes extends BasicRes {

	private String title;

	private String description;

	private List<FeedbackVo> feedbackVoList;

	public FeedbackRes() {
		super();
	}

	public FeedbackRes(int code, String message) {
		super(code, message);
	}

	public FeedbackRes(int code, String message, String title, String description, //
			List<FeedbackVo> feedbackVoList) {
		super(code, message);
		this.title = title;
		this.description = description;
		this.feedbackVoList = feedbackVoList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<FeedbackVo> getFeedbackVoList() {
		return feedbackVoList;
	}

	public void setFeedbackVoList(List<FeedbackVo> feedbackVoList) {
		this.feedbackVoList = feedbackVoList;
	}

}