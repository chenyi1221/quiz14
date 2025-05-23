package com.example.quiz14.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz14.service.ifs.FeedbackService;
import com.example.quiz14.vo.BasicRes;
import com.example.quiz14.vo.FeedbackRes;
import com.example.quiz14.vo.FillinReq;
import com.example.quiz14.vo.StatisticsRes;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
public class FeedbackController {
	
	@Autowired
	private FeedbackService feedbackService;
	
	@PostMapping(value = "quiz/fillin")
	public BasicRes fillin(@Valid @RequestBody FillinReq req) throws Exception{
		return feedbackService.fillin(req);
	}
	
	// api路徑: http://localhost:8080/quiz/feedback?quizId=1
	@PostMapping(value = "quiz/feedback")
	public FeedbackRes feedback(@RequestParam("quizId") int quizId) throws JsonProcessingException {
		return feedbackService.feedback(quizId);
	}
	
	@PostMapping(value = "quiz/statistics")
	public StatisticsRes statistics(@RequestParam("quizId") int quizId) throws JsonProcessingException {
		return feedbackService.statistics(quizId);
	}

}
