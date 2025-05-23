package com.example.quiz14.vo;

import java.util.List;

import com.example.quiz14.constants.ConstantsMessage;

import jakarta.validation.constraints.NotEmpty;

public class DelateReq {
	
	@NotEmpty(message = ConstantsMessage.PARAM_QUIZ_ID_ERROR)
	private List<Integer> idList;

	public List<Integer> getIdList() {
		return idList;
	}

	public void setIdList(List<Integer> idList) {
		this.idList = idList;
	}

}
