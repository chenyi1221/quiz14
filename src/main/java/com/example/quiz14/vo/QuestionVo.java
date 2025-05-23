package com.example.quiz14.vo;

import java.util.List;

import com.example.quiz14.constants.ConstantsMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class QuestionVo {

	private int quizId;

	@JsonProperty(value = "qId")
	@Min(value = 1, message = ConstantsMessage.PARAM_QUES_ID_ERROR)
	private int quesId;

	@JsonProperty(value = "qName")
	@NotBlank(message = ConstantsMessage.PARAM_QUESTION_ERROR)
	private String question;

	@JsonProperty(value = "typeName")
	@NotBlank(message = ConstantsMessage.PARAM_QUES_TYPE_ERROR)
	private String type;

	@JsonProperty(value = "must")
	private boolean required;

	private List<String> options;

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

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

}
