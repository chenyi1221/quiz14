package com.example.quiz14.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz14.constants.QuesType;
import com.example.quiz14.constants.ResMessage;
import com.example.quiz14.dao.FeedbackDao;
import com.example.quiz14.dao.QuestionDao;
import com.example.quiz14.dao.QuizDao;
import com.example.quiz14.entity.Feedback;
import com.example.quiz14.entity.Question;
import com.example.quiz14.entity.Quiz;
import com.example.quiz14.service.ifs.FeedbackService;
import com.example.quiz14.vo.BasicRes;
import com.example.quiz14.vo.FeedbackDto;
import com.example.quiz14.vo.FeedbackRes;
import com.example.quiz14.vo.FeedbackVo;
import com.example.quiz14.vo.FillinReq;
import com.example.quiz14.vo.OptionCountVo;
import com.example.quiz14.vo.QuesIdAnswerVo;
import com.example.quiz14.vo.QuestionAnswerVo;
import com.example.quiz14.vo.StatisticsRes;
import com.example.quiz14.vo.StatisticsVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private FeedbackDao feedbackDao;

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuestionDao questionDao;

	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes fillin(FillinReq req) throws Exception {
		// 1.檢查問卷是否存在、問卷是否允許開放填寫、是否已發布
		LocalDate now = LocalDate.now();
		int quizCount = quizDao.selectCountById(req.getQuizId(), now);
		if (quizCount != 1) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), //
					ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		// 為了接下來的檢查，把List<QuesIdAnswerVo>轉成map
		Map<Integer, List<String>> quesIdAnswerMap = new HashMap<>();
		for (QuesIdAnswerVo vo : req.getAnswerVoList()) {
			quesIdAnswerMap.put(vo.getQuesId(), vo.getAnswerList());
		}

		// 2.檢查問題: 答案是否與選項相符、必填是否有答案
		List<Question> questionList = questionDao.getByQuizId(req.getQuizId());
		for (Question ques : questionList) {
			// 排除必填但沒作答
			// !quesIdAnswerMap.containsKey(ques.getQuesId()): 表示map中沒有該題號以及相對應的答案
			if (ques.isRequired() && !quesIdAnswerMap.containsKey(ques.getQuesId())) {
				return new BasicRes(ResMessage.ANSWER_IS_REQUIRED.getCode(), //
						ResMessage.ANSWER_IS_REQUIRED.getMessage());
			}
			// 跳過題型是簡答題: 因為簡答題沒有選項可以比對
			if (ques.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				continue;
			}
			// 要把原本資料型態是List<String>的字串(String)轉回成原本的格式List<String>(參考creat新增問題的mapper)
			try {
				List<String> options = mapper.readValue(ques.getOptions(), new TypeReference<>() {
				});
				List<String> answerList = quesIdAnswerMap.get(ques.getQuesId());
				if (!options.containsAll(answerList)) {
					return new BasicRes(ResMessage.OPTION_ANSWER_MISMATCH.getCode(), //
							ResMessage.OPTION_ANSWER_MISMATCH.getMessage());
				}
			} catch (JsonProcessingException e) {
				throw e;
			}
		}
		// 3.檢查email是否已填寫該張問卷
		int emailCount = feedbackDao.selectCountByEmailAndQuiz(req.getEmail(), req.getQuizId());
		if (emailCount > 0) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATED.getCode(), //
					ResMessage.EMAIL_DUPLICATED.getMessage());
		}
		// 存資料
		try {
			for (QuesIdAnswerVo vo : req.getAnswerVoList()) {
				String answerStr = mapper.writeValueAsString(vo.getAnswerList());
				feedbackDao.insert(req.getUserName(), req.getPhone(), req.getEmail(), req.getAge(), //
						req.getQuizId(), vo.getQuesId(), answerStr, now);
			}
		} catch (Exception e) {
			throw e;
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	@Override
	public FeedbackRes feedback(int quizId) throws JsonProcessingException {
		// 1.quiz
		Quiz quiz = quizDao.selectById(quizId);
		if (quiz == null) {
			return new FeedbackRes(ResMessage.QUIZ_NOT_FOUND.getCode(), //
					ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		FeedbackRes res = new FeedbackRes();
		res.setTitle(quiz.getTitle());
		res.setDescription(quiz.getDescription());
		// 2.question
		List<Question> questionList = questionDao.getByQuizId(quizId);
		// 問題編號和問題的map
		Map<Integer, String> map = new HashMap<>();
		// 將每個Question中的問題編號和問題設定到QuestionAnswerVo中
		for (Question item : questionList) {
			map.put(item.getQuesId(), item.getQuestion());
		}
		// 3.feedback
		List<Feedback> feedbackList = feedbackDao.selectByQuizId(quizId);
		// feedbackList的結果可能會有重複地填寫者資訊，所以要先整理
		// email和FeedbackVo的map:一張問卷只會有一個email
		Map<String, FeedbackVo> emailMap = new HashMap<>();
		for (Feedback item : feedbackList) {
			// 判斷email是否已存在於emailMap
			if (emailMap.containsKey(item.getEmail())) {
				// 若email已存在於emailMap中，將對應的FeedbackVo取出
				FeedbackVo vo = emailMap.get(item.getEmail());
				// 設定作答
				List<QuestionAnswerVo> answerVoList = setQuesAns(item, map);
				// 從vo中取出之前的作答
				List<QuestionAnswerVo> otherAnswerVoList = vo.getAnswerVoList();
				// 將兩者合併
				otherAnswerVoList.addAll(answerVoList);
				// 將合併的結果設定回vo
				vo.setAnswerVoList(otherAnswerVoList);
				// 將最終結果放回emailMap
				emailMap.put(item.getEmail(), vo);
			} else {
				FeedbackVo vo = new FeedbackVo();
				// 設定填寫者資訊
				vo.setUserName(item.getUserName());
				vo.setPhone(item.getPhone());
				vo.setEmail(item.getEmail());
				vo.setAge(item.getAge());
				vo.setFillinDate(item.getFillinDate());
				// 設定作答
				List<QuestionAnswerVo> answerVoList = setQuesAns(item, map);
				vo.setAnswerVoList(answerVoList);
				emailMap.put(item.getEmail(), vo);
			}

		}

		List<FeedbackVo> feedbackVoList = new ArrayList<>();
		for (Entry<String, FeedbackVo> item : emailMap.entrySet()) {
			feedbackVoList.add(item.getValue());
		}
		res.setFeedbackVoList(feedbackVoList);
		res.setCode(ResMessage.SUCCESS.getCode());
		res.setMessage(ResMessage.SUCCESS.getMessage());
		return res;

	}

	private List<QuestionAnswerVo> setQuesAns(Feedback item, Map<Integer, String> map) throws JsonProcessingException {
		// 設定作答
		List<QuestionAnswerVo> answerVoList = new ArrayList<>();
		QuestionAnswerVo quesAnsVo = new QuestionAnswerVo();
		quesAnsVo.setQuesId(item.getQuesId());
		// 使用quesId當作key從map中取得對應的value(問題)
		String question = map.get(item.getQuesId());
		quesAnsVo.setQuestion(question);
		// 取得答案的字串
		String ansStr = item.getAnswer();
		// 將字串轉成List<String>
		try {
			List<String> answerList = mapper.readValue(ansStr, new TypeReference<>() {
			});
			quesAnsVo.setAnswerList(answerList);
		} catch (JsonProcessingException e) {
			throw e;
		}
		answerVoList.add(quesAnsVo);
		return answerVoList;
	}

	@Override
	public StatisticsRes statistics(int quizId) throws JsonProcessingException {
		// 檢查 quizId
		int count = quizDao.selectCountById(quizId);
		if (count != 1) {
			return new StatisticsRes(ResMessage.QUIZ_NOT_FOUND.getCode(), //
					ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		// 取得問題的相關資料
		List<Question> questionList = questionDao.getByQuizId(quizId);
		List<StatisticsVo> statisticsVoList = new ArrayList<>();
		// 問題編號、問題選項與對應次數的 map
		Map<Integer, Map<String, Integer>> quesIdOptionCountMap = new HashMap<>();
		for (Question item : questionList) {
			// 跳過簡答題: 因為簡答題沒有選項
			if (item.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				continue;
			}
			StatisticsVo vo = new StatisticsVo();
			vo.setQuesId(item.getQuesId());
			vo.setQuestion(item.getQuestion());
			vo.setType(item.getType());
			vo.setRequired(item.isRequired());
			statisticsVoList.add(vo);
			// 設定選項和次數的 map
			setOptionMap(item, quesIdOptionCountMap);
		}
		// 統計次數
		statistics(quizId, quesIdOptionCountMap);
		// 把統計次數設定到對應的題號中
		for(StatisticsVo voItem : statisticsVoList) {
			int quesId = voItem.getQuesId();
			// optionCountMap 是每一題的選項與對應次數
			Map<String, Integer> optionCountMap = quesIdOptionCountMap.get(quesId);
			// 將 optionCountMap 轉成 List<OptionCountVo>
			List<OptionCountVo> ocVoList = new ArrayList<>();
			for(Entry<String, Integer> ocItem: optionCountMap.entrySet()) {
				// 把每個 ocItem 的 key-value 轉成單一個 OptionCountVo
				OptionCountVo ocVo = new OptionCountVo();
				ocVo.setOption(ocItem.getKey());
				ocVo.setCount(ocItem.getValue());
				ocVoList.add(ocVo);
			}
			voItem.setOptionCountVoList(ocVoList);
			//========================
			// 以下是 Lambda 表達式，效果同上面的 foreach
//			optionCountMap.forEach((k,v) -> {
//				OptionCountVo ocVo = new OptionCountVo();
//				ocVo.setOption(k);
//				ocVo.setCount(v);
//				ocVoList.add(ocVo);
//			});
		}
		return new StatisticsRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), statisticsVoList);
	}
	private void setOptionMap(Question item, Map<Integer, Map<String, Integer>> optionCountMap)
			throws JsonProcessingException {
		// 先不設定 List<OptionCountVo>，把之轉換成 optionCountMap
		// 將字串轉成 List<String>
		List<String> optionList = convertStrToList(item.getOptions());
		// 把每個選項放到 map 中
		Map<String, Integer> map = new HashMap<>();
		for (String str : optionList) {
			map.put(str, 0);
		}
		optionCountMap.put(item.getQuesId(), map);
	}
	private List<String> convertStrToList(String inputStr) throws JsonProcessingException {
		try {
			return mapper.readValue(inputStr, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			throw e;
		}
	}
	private void statistics(int quizId, Map<Integer, Map<String, Integer>> quesIdOptionCountMap)
			throws JsonProcessingException {
		List<Feedback> feedbackList = feedbackDao.selectByQuizId(quizId);
		for (Feedback feedback : feedbackList) {
			Map<String, Integer> optionCountMap = quesIdOptionCountMap.get(feedback.getQuesId());
			// 因為之前跳過簡答題的選項收集，所以該題是簡答題時不存在，optionCountMap 會是 null
			if (optionCountMap == null) {
				continue;
			}
			// 將 answer 的 String 格式轉回成原本的 List<String>: 一題的答案會有多個表示該題是多選題
			List<String> answerList = convertStrToList(feedback.getAnswer());
			// 透過問題編號取得 問題選項與對應次數的 map
			for (String answer : answerList) {
				// 取得選項對應的次數
				int optionCount = optionCountMap.get(answer);
				// 將次數+1
				optionCount++;
				// 將更新後的次數放回到 map 中
				optionCountMap.put(answer, optionCount);
			}
		}
	}



}
