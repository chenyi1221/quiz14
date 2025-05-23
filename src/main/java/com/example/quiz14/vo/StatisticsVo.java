package com.example.quiz14.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

// 一個StatisticsVo表示一題的統計
public class StatisticsVo {

	private int quesId;

	private String question;

	private String type;

	private boolean required;

	private List<OptionCountVo> optionCountVoList;

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

	public List<OptionCountVo> getOptionCountVoList() {
		return optionCountVoList;
	}

	public void setOptionCountVoList(List<OptionCountVo> optionCountVoList) {
		this.optionCountVoList = optionCountVoList;
	}

}
