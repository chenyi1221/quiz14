package com.example.quiz14.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz14.service.ifs.QuizService;
import com.example.quiz14.vo.BasicRes;
import com.example.quiz14.vo.CreateReq;
import com.example.quiz14.vo.DelateReq;
import com.example.quiz14.vo.GetQuestionRes;
import com.example.quiz14.vo.SearchReq;
import com.example.quiz14.vo.SearchRes;
import com.example.quiz14.vo.UpdateReq;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;

// 即預設的路徑會是 localhost:8080/atm/
// @RequestMapping(value = "quiz/") //表示此 controller 底下的所有 API 路徑的前綴會是以 quiz/開頭
// 若有使用的話，Controller中的API路徑就可以拿掉quiz/
@CrossOrigin
@RestController
public class QuizController {

	@Autowired
	private QuizService quizService;
	
	@PostMapping(value = "quiz/create")
	public BasicRes create(@Valid @RequestBody CreateReq req) throws Exception{
		return quizService.create(req);
	}
	
	@GetMapping(value = "quiz/getAll")
	public SearchRes getAll() {
		return quizService.getAll();
	}
	
	@PostMapping(value = "quiz/getAll")
	public SearchRes getAll(@RequestBody SearchReq req) {
		return quizService.getAll(req);
	}
	
	// API路徑: http://localhost:8080/quiz/getByQuizId?quizId=1
	@GetMapping(value = "quiz/getByQuizId")
	public GetQuestionRes getQuestionByQuizId(@RequestParam("quizId") int quizId) throws JsonProcessingException{
		return quizService.getQuestionByQuizId(quizId);
	}
	
	@PostMapping(value = "quiz/update")
	public BasicRes update(@Valid @RequestBody UpdateReq req) throws Exception{
		return quizService.update(req);
	}
	
	@PostMapping(value = "quiz/delete")
	public BasicRes delete(@Valid @RequestBody DelateReq req) {
		return quizService.delete(req);
	}
	
}
