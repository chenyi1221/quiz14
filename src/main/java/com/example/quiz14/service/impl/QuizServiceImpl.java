package com.example.quiz14.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.quiz14.constants.QuesType;
import com.example.quiz14.constants.ResMessage;
import com.example.quiz14.dao.QuestionDao;
import com.example.quiz14.dao.QuizDao;
import com.example.quiz14.entity.Question;
import com.example.quiz14.entity.Quiz;
import com.example.quiz14.service.ifs.QuizService;
import com.example.quiz14.vo.BasicRes;
import com.example.quiz14.vo.CreateReq;
import com.example.quiz14.vo.DelateReq;
import com.example.quiz14.vo.GetQuestionRes;
import com.example.quiz14.vo.QuestionVo;
import com.example.quiz14.vo.SearchReq;
import com.example.quiz14.vo.SearchRes;
import com.example.quiz14.vo.UpdateReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@EnableScheduling
@Service
public class QuizServiceImpl implements QuizService {
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuestionDao questionDao;
	
	/**
	 * 1. @CacheEvict: 清除暫存資料，用於資料有變動時(Create，Update，Delete)
	 * 2. 只有 cacheNames 沒有 key，會把 cacheNames 就是 getAll 的所有暫存資料清除</br>
	 * 3. 如果是 cacheNames + key，則是只清除特定的暫存資料； key 的參數值一樣使用 #p0 來表示，但通常不會只清除特定資料</br>
	 * 4. allEntries: 強制刪除指定的 cacheNames 底下所有 key 對應的暫存資料，預設是 false
	 */
	@CacheEvict(cacheNames = "getAll", allEntries = true)
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes create(CreateReq req) throws Exception {
		// 檢查日期: 1. 開始日期不能比當天早(過去問卷) 2. 開始日期不能比結束日期晚
		// 排除法: 排除 1. 開始日期比當天早(req 中已驗證) 2. 開始日期比結束日期晚
		if (req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATE_FORMAT_ERROR.getCode(), //
					ResMessage.DATE_FORMAT_ERROR.getMessage());
		}
		// 檢查問題
		List<QuestionVo> questionVos = req.getQuestionVos();
		BasicRes checkRes = checkQuestions(questionVos);
		if (checkRes != null) {
			return checkRes;
		}
		try {
			// 新增問卷
			quizDao.create(req.getTitle(), req.getDescription(), req.getStartDate(), //
					req.getEndDate(), req.isPublished());
			// 取得上面新增問卷的quiz的id
			// 雖然透過@Transactional尚未將資料提交(commuit)進資料庫，但實際上SQL語法已經執行完畢
			// 依然可以取得對應值
			int quizId = quizDao.selectMaxId();
			// 新增問題
			for (QuestionVo vo : questionVos) {
				// 將vo中的options的資料型態從List<String>轉成String
				String optionStr = mapper.writeValueAsString(vo.getOptions());
				questionDao.insert(quizId, vo.getQuesId(), vo.getQuestion(), //
						vo.getType(), vo.isRequired(), optionStr);
			}
		} catch (Exception e) {
			// 將exception拋出
			throw e;
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	private BasicRes checkQuestions(List<QuestionVo> questionVos) {
		for (QuestionVo item : questionVos) {
			String type = item.getType();
			// 檢查問題型態是否符合規範
			if(!QuesType.checkAllType(type)) {
				return new BasicRes(ResMessage.QUESTION_TYPE_ERROR.getCode(), //
						ResMessage.QUESTION_TYPE_ERROR.getMessage());
			}
			// 1. 單選或多選 --> 選項至少要有2個
			if (QuesType.checkChoiceType(type)) {
				// QuesType.checkChoiceType(type) == true 表示是單選或多選
				// 排除: 選項是 null 或 選項小於2個
				if (item.getOptions() == null || item.getOptions().size() < 2) {
					return new BasicRes(ResMessage.OPTIONS_INSUFFICIENT.getCode(), //
							ResMessage.OPTIONS_INSUFFICIENT.getMessage());
				}
			}
			// 2. 題型是問答 --> 選項不能有值，表示選項可以是 null 或是 不能有內容
			if (type.equalsIgnoreCase(QuesType.TEXT.getType())) {
				// 排除法: 排除 非 null 且 內容的 size > 0
				if (item.getOptions() != null && item.getOptions().size() > 0) {
					return new BasicRes(ResMessage.TEXT_HAS_OPTIONS_ERROR.getCode(), //
							ResMessage.TEXT_HAS_OPTIONS_ERROR.getMessage());
				}
			}
		}
		return null;
	}

	@Cacheable(cacheNames = "getAll", key = "")
	@Override
	public SearchRes getAll() {
		System.out.println("=========================");
		return new SearchRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), quizDao.selectAll());
	}

	@Override
	public GetQuestionRes getQuestionByQuizId(int quizId) throws JsonProcessingException {
		List<Question> questionsList = questionDao.getByQuizId(quizId);
		Quiz quiz = quizDao.selectById(quizId);
		List<QuestionVo> voList = new ArrayList<>();
		// 把每個Question中的值設定到QuestionVo
		for (Question item : questionsList) {
			QuestionVo vo = new QuestionVo();
			vo.setQuizId(item.getQuizId());
			vo.setQuesId(item.getQuesId());
			vo.setQuestion(item.getQuestion());
			vo.setType(item.getType());
			vo.setRequired(item.isRequired());
			// 要把原本資料型態是List<String>的字串(String)轉回成原本的格式List<String>(參考creat新增問題的mapper)
			try {
				List<String> options = mapper.readValue(item.getOptions(), new TypeReference<>() {
				});
				vo.setOptions(options);
			} catch (JsonProcessingException e) {
				throw e;
			}
			voList.add(vo);
		}
		return new GetQuestionRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), quiz, voList);
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes update(UpdateReq req) throws Exception {
		// 檢查quiz的id和question中的quizId是否一致
		List<QuestionVo> questionVoList = req.getQuestionVos();
		for (QuestionVo vo : questionVoList) {
			if (vo.getQuizId() != req.getQuizId()) {
				return new GetQuestionRes(ResMessage.QUIZ_ID_MISMATCH.getCode(), //
						ResMessage.QUIZ_ID_MISMATCH.getMessage());
			}
		}
		// 檢查日期: 1. 開始日期不能比當天早(過去問卷) 2. 開始日期不能比結束日期晚
		// 排除法: 排除 1. 開始日期比當天早(req 中已驗證) 2. 開始日期比結束日期晚
		if (req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATE_FORMAT_ERROR.getCode(), //
					ResMessage.DATE_FORMAT_ERROR.getMessage());
		}
		// 檢查問題
		BasicRes checkRes = checkQuestions(questionVoList);
		if (checkRes != null) {
			return checkRes;
		}
		// 檢查問卷是否能更新: 問卷狀態進行中或已結束都不能更新
		Quiz quiz = quizDao.selectById(req.getQuizId());
		if(quiz == null) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), //
					ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		if(quiz.isPublished()) { //問卷已發布
			LocalDate nowDate = LocalDate.now();
			LocalDate startDate = quiz.getStartDate();
			// 只要判斷當前日期是否大於等於開始日期
			// 進行中: 當前日期>=開始日期
			// 已結束: 當前日期>結束日期 -->也表示當前日期>開始日期
			// 當前日期大於等於開始日期 --> 當前日期不在開始日期之前
			// nowDate.isEqual(startDate) || nowDate.isAfter(startDate) --> !nowDate.isBefore(startDate)
			if(!nowDate.isBefore(startDate)) {
				return new BasicRes(ResMessage.QUIZ_CANNOT_UPDATE.getCode(), //
						ResMessage.QUIZ_CANNOT_UPDATE.getMessage());
			}
		}
		try {
			// 更新問卷
			int res = quizDao.update(req.getQuizId(), req.getTitle(), req.getDescription(), //
					req.getStartDate(), req.getEndDate(), req.isPublished());
			// 因為問卷的id是pk，所以更新成功回傳是1，沒資料更新成功則回傳0
			if(res != 1) {
				return new BasicRes(ResMessage.QUIZ_UPDATE_FAILED.getCode(), //
						ResMessage.QUIZ_UPDATE_FAILED.getMessage());
			}
			// 更新問題:步驟1.刪除相同quizId的問題 步驟2.新增問題
			questionDao.deleteByQuizId(req.getQuizId());
			// 新增問題
			for (QuestionVo vo : questionVoList) {
			    // 將vo中的options的資料型態從List<String>轉成String
			    String optionStr = mapper.writeValueAsString(vo.getOptions());
			    questionDao.insert(req.getQuizId(), vo.getQuesId(), vo.getQuestion(), //
			      vo.getType(), vo.isRequired(), optionStr);
			   }
		} catch (Exception e) {
			throw e;
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes delete(DelateReq req) {
		// 檢查問卷是否能刪除: 問卷狀態進行中或已結束都不能刪除
		List<Quiz> quizList = quizDao.selectByIdIn(req.getIdList());
		for(Quiz item: quizList) {
			LocalDate nowDate = LocalDate.now();
			LocalDate startDate = item.getStartDate();
			if(!nowDate.isBefore(startDate)) {
				return new BasicRes(ResMessage.QUIZ_CANNOT_DELETE.getCode(), //
						ResMessage.QUIZ_CANNOT_DELETE.getMessage());
			}
		}
		// 刪除問卷和問題
		quizDao.deleteByIdIn(req.getIdList());
		questionDao.deleteByQuizIdIn(req.getIdList());
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	/**
	 * 1. key 等號後面的字串，因為 req 是物件，使用 #req 會取不到參數值，簡單點的方法是使用位置 #p0 來表示方法中第一個參數</br>
	 * 2. 參數若是 class 時，可使用點(.)來取得類別中的屬性</br>
	 * 3. 多參數的串接，不使用 concat，直接在字串中使用加號(+)串多個參數</br>
	 * 4. 字串中使用單引號來表示字串，符號的串接也要使用加號(+)</br>
	 * 5. 串接值的資料型態不是 String 時，可以使用 .toString() 轉換</br>
	 * 6. #result: 表示方法返回的結果；即使是不同方法有不同的返回資料型態，也通用</br>
	 * 7. unless 可以翻成排除的意思，後面的字串是指會排除符合條件的 --> 排除 res 不成功，即只暫存成功時的資料
	 */
	@Cacheable(cacheNames = "getAll", key = "#p0.quizName + '-' + #p0.startDate.toString()")
	@Override
	public SearchRes getAll(SearchReq req) {
		// 轉換參數值
		String quizName = req.getQuizName();
		if(!StringUtils.hasText(quizName)) {
			// quizName是null或空字串或全白字串時，設定成空字串
			quizName = "";
		}
		LocalDate startDate = req.getStartDate();
		if(startDate == null) {
			// 設定startDate一個很早的時間
			startDate = LocalDate.of(1970, 1, 1);
		}
		LocalDate endDate = req.getEndDate();
		if(endDate == null) {
			// 設定endDate一個很久之後的未來時間
			endDate = LocalDate.of(2999, 12, 31);
		}
		List<Quiz> resList = new ArrayList<>();
		if(req.isForFrontEnd()) {
			resList = quizDao.selectAllForFront(quizName, startDate, endDate);
		}else {
			resList = quizDao.selectAll(quizName, startDate, endDate);
		}
		return new SearchRes(ResMessage.SUCCESS.getCode(), //
						ResMessage.SUCCESS.getMessage(), resList);
	}
	
	// fixedRate: 固定的時間間隔執行，單位是毫秒
//	@Scheduled(fixedRate = 5000)
	public void fixedRateTest() {
		System.out.println(LocalDateTime.now());
	}
	
	// fixedRateString: 透過${}的方式從設定檔(application.properties)取值
//	@Scheduled(fixedRateString = "${fixed.rate}")
	public void fixedRateStringTest() {
		System.out.println(LocalDateTime.now());
	}

	// cron 後面的字串有6個單位，從左到右依序是 秒 分 時 日 月 星期幾，單位間要有空格
	@Scheduled(cron = "* * * * * *")
	public void cornTest() {
		System.out.println(LocalDateTime.now());
	}
}
