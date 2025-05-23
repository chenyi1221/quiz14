package com.example.quiz14.constants;

public enum QuesType {

	SINGLE("Single"), //
	MULTI("Multi"), //
	TEXT("Text");

	private String type;

	private QuesType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static boolean checkChoiceType(String input) {
		if (input.equalsIgnoreCase(QuesType.SINGLE.getType()) || //
				input.equalsIgnoreCase(QuesType.MULTI.getType())) {
			return true;
		}
		return false;
	}

	public static boolean checkAllType(String input) {
		for (QuesType type : values()) {
			if (input.equalsIgnoreCase(type.getType())) {
				return true;
			}
		}
		return false;
	}

}
