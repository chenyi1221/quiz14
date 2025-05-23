package com.example.quiz14.constants;

public enum ResMessage {
	SUCCESS(200, "Success!!"), //
	DATE_FORMAT_ERROR(400, "Date format error!!"), //
	OPTIONS_INSUFFICIENT(400, "Options insufficient!!"), //
	TEXT_HAS_OPTIONS_ERROR(400, "Text has options!!"), //
	QUIZ_ID_MISMATCH(400, "Quiz id mismatch!!"), //
	QUIZ_NOT_FOUND(404, "Quiz not found!!"), //
	QUIZ_CANNOT_UPDATE(400, "Quiz cannot update!!"), //
	QUIZ_CANNOT_DELETE(400, "Quiz cannot delete!!"), //
	QUIZ_UPDATE_FAILED(400, "Quiz update failed!!"), //
	QUESTION_TYPE_ERROR(400, "Question type error!!"), //
	ANSWER_IS_REQUIRED(40, "Answer is required!!"), //
	OPTION_ANSWER_MISMATCH(400, "Option answer mismatch!!"), //
	EMAIL_DUPLICATED(400, "Email duplicated!!"), //
	QUIZ_ID_ERROR(400, "Quiz id error!!");

	private int code;

	private String message;

	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
