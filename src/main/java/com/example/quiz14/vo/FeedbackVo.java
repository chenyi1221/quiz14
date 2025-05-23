package com.example.quiz14.vo;

import java.time.LocalDate;
import java.util.List;

// 一個FeedbackVo表示一位填寫者的資訊與答覆
public class FeedbackVo {

	private String userName;

	private String phone;

	private String email;

	private int age;

	private LocalDate fillinDate;

	private List<QuestionAnswerVo> answerVoList;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<QuestionAnswerVo> getAnswerVoList() {
		return answerVoList;
	}

	public void setAnswerVoList(List<QuestionAnswerVo> answerVoList) {
		this.answerVoList = answerVoList;
	}

	public LocalDate getFillinDate() {
		return fillinDate;
	}

	public void setFillinDate(LocalDate fillinDate) {
		this.fillinDate = fillinDate;
	}

}
