package com.example.quiz14.service.ifs;

import com.example.quiz14.vo.BasicRes;
import com.example.quiz14.vo.CreateReq;
import com.example.quiz14.vo.DelateReq;
import com.example.quiz14.vo.GetQuestionRes;
import com.example.quiz14.vo.SearchReq;
import com.example.quiz14.vo.SearchRes;
import com.example.quiz14.vo.UpdateReq;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface QuizService {

	public BasicRes create(CreateReq req) throws Exception;
	
	public SearchRes getAll();
	
	public SearchRes getAll(SearchReq req);
	
	public GetQuestionRes getQuestionByQuizId(int quizId) throws JsonProcessingException;
	
	public BasicRes update(UpdateReq req) throws Exception;
	
	public BasicRes delete(DelateReq req);
}
