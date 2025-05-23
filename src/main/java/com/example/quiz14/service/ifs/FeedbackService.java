package com.example.quiz14.service.ifs;

import com.example.quiz14.vo.BasicRes;
import com.example.quiz14.vo.FeedbackRes;
import com.example.quiz14.vo.FillinReq;
import com.example.quiz14.vo.StatisticsRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface FeedbackService {

	public BasicRes fillin(FillinReq req) throws Exception;
	
	public FeedbackRes feedback(int quizId) throws JsonProcessingException;
	
	public StatisticsRes statistics(int quizId) throws JsonProcessingException;

}
