package com.example.quiz14.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quiz14.entity.Feedback;
import com.example.quiz14.entity.FeedbackId;
import com.example.quiz14.vo.FeedbackDto;

import jakarta.transaction.Transactional;

@Repository
public interface FeedbackDao extends JpaRepository<Feedback, FeedbackId> {

	@Query(value = "select count(email) from feedback where email = ?1 and quiz_id = ?2", //
			nativeQuery = true)
	public int selectCountByEmailAndQuiz(String email, int quizId);

	@Transactional
	@Modifying
	@Query(value = "insert into feedback " //
			+ " (user_name, phone, email, age, quiz_id, ques_id, answer, fillin_date) " //
			+ " values " + "(:userName, :phone, :email, :age, :quizId, :quesId, :answer, :fillinDate)", //
			nativeQuery = true)
	public void insert(//
			@Param("userName") String userName, //
			@Param("phone") String phone, //
			@Param("email") String email, //
			@Param("age") int age, //
			@Param("quizId") int quizId, //
			@Param("quesId") int quesId, //
			@Param("answer") String answer, //
			@Param("fillinDate") LocalDate fillinDate);//

	/**
	 * nativeQuery = false時，SQL語法中 <br>
	 * 1.select的欄位名稱會各個Entity class中的屬性變數名稱<br>
	 * 2.on後面的欄位名稱會是Entity class中的屬性變數名稱<br>
	 * 3.表的名稱會變成Entity class名稱<br>
	 * 4.select 後面的欄位要透過new建構方法來塞值，FeedbackDto中也要有對應的建構方法<br>
	 * 5.FeedbackDto要給定完整的路徑: com.example.quiz14.vo.FeedbackDto
	 */
	//SQL語法中的欄位名稱會變成FeedbackDto中的屬性變數名稱
	@Query(value = " select new com.example.quiz14.vo.FeedbackDto(Qz.title, Qz.description, " //
			   + " Qu.quesId, Qu.question, " //
			   + " F.userName, F.phone, F.email, F.age, F.answer, F.fillinDate) " //
			   + " from Quiz as Qz " //
			   + " join Question as Qu on Qz.id = Qu.quizId " //
			   + " join Feedback as F on Qu.quizId = F.quizId where Qz.id = ?1", //
			   nativeQuery = false)
			 public List<FeedbackDto> selectJoinByQuizId(int quizId);
	
	@Query(value = "select * from feedback where quiz_id = ?1", //
			nativeQuery = true)
	public List<Feedback> selectByQuizId(int quizId);
	
}
