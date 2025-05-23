package com.example.quiz14.vo;

import java.util.List;

import com.example.quiz14.constants.ConstantsMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;

public class QuesIdAnswerVo {

	@Min(value = 1, message = ConstantsMessage.PARAM_QUES_ID_ERROR)
	private int quesId;

	@JsonProperty(value = "optionArray")
	//答案不檢查，因為有可能是簡答題且非必填
	private List<String> answerList;

	public int getQuesId() {
		return quesId;
	}

	public void setQuesId(int quesId) {
		this.quesId = quesId;
	}

	public List<String> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<String> answerList) {
		this.answerList = answerList;
	}

}
