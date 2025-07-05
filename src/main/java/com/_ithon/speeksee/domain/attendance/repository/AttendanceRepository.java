package com._ithon.speeksee.domain.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._ithon.speeksee.domain.attendance.entity.Attendance;
import com._ithon.speeksee.domain.member.entity.Member;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	// 해당 날짜에 출석했는지 확인 -> 중복 출석x
	boolean existsByMemberAndDate(Member member, LocalDate date);

	// 특정 기간동안 출석한 기록 조회
	List<Attendance> findByMemberAndDateBetween(Member member, LocalDate start, LocalDate end);
}
