package com._ithon.speeksee.domain.voicefeedback.statistics.dev;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.member.repository.MemberRepository;
import com._ithon.speeksee.domain.script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.repository.ScriptRepository;
import com._ithon.speeksee.domain.voicefeedback.practice.entity.ScriptPractice;
import com._ithon.speeksee.domain.voicefeedback.statistics.repository.ScriptPracticeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DummyPracticeDataInitializer implements CommandLineRunner {

	private final MemberRepository memberRepository;
	private final ScriptRepository scriptRepository;
	private final ScriptPracticeRepository practiceRepository;
	private final PasswordEncoder passwordEncoder;


	@Override
	public void run(String... args) throws Exception {
		Member member = memberRepository.save(Member.builder()
			.email("user@example.com")
			.username("테스트유저")
			.passwordHash(passwordEncoder.encode("1234secure"))
			.build());

		Script script = scriptRepository.save(Script.builder()
			.title("테스트 대본")
			.difficultyLevel(DifficultyLevel.MEDIUM)
			.content("테스트 문장입니다.")
			.author(member)
			.build());

		LocalDate today = LocalDate.now();

		// 1. 최근 7일
		generatePractices(today.minusDays(6), today, member, script);

		// 2. 반년 전 (약 6개월 전 ~ 5개월 전)
		generatePractices(today.minusMonths(6), today.minusMonths(5), member, script);

		// 3. 1년 전 (약 12개월 전 ~ 11개월 전)
		generatePractices(today.minusYears(1), today.minusMonths(11), member, script);
	}

	private void generatePractices(LocalDate start, LocalDate end, Member member, Script script) {
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			int countPerDay = ThreadLocalRandom.current().nextInt(1, 4); // 하루 1~3회
			for (int j = 0; j < countPerDay; j++) {
				ScriptPractice practice = ScriptPractice.builder()
					.member(member)
					.script(script)
					.transcript("이건 연습 텍스트입니다.")
					.accuracy(ThreadLocalRandom.current().nextDouble(0.7, 1.0))
					.build();
				practice.setCreatedAt(date.atTime(10 + j, 0));
				practiceRepository.save(practice);
			}
		}
	}

}
