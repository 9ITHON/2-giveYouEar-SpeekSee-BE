package com._ithon.speeksee.domain.voicefeedback.statistics.dev;

import static com._ithon.speeksee.domain.script.domain.ScriptCategory.*;

import java.time.LocalDate;
import java.util.List;
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
public class DummyScriptPracticeCountTester implements CommandLineRunner {

	private final MemberRepository memberRepository;
	private final ScriptRepository scriptRepository;
	private final ScriptPracticeRepository practiceRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		Member member = memberRepository.save(Member.builder()
			.email("user@example.com")
			.nickname("누적테스트유저")
			.birthday(LocalDate.ofEpochDay(2002-02-22))
			.passwordHash(passwordEncoder.encode("1234secure"))
			.infoCompleted(true)
			.build());

		List<Script> scripts = List.of(
			createScript("AI 자기소개", DifficultyLevel.EASY, member),
			createScript("날씨 브리핑", DifficultyLevel.MEDIUM, member),
			createScript("경제 뉴스 헤드라인", DifficultyLevel.HARD, member)
		);

		for (Script script : scripts) {
			int practiceTotal = ThreadLocalRandom.current().nextInt(5, 15); // 5~14회

			for (int i = 0; i < practiceTotal; i++) {
				ScriptPractice practice = ScriptPractice.builder()
					.member(member)
					.script(script)
					.transcript("이건 누적 연습 테스트입니다.")
					.accuracy(ThreadLocalRandom.current().nextDouble(0.6, 0.95))
					.build();

				practiceRepository.save(practice);
				script.increasePracticeCount();
			}
		}
	}

	private Script createScript(String title, DifficultyLevel level, Member author) {
		return scriptRepository.save(Script.builder()
			.title(title)
			.content("AI가 생성한 대본입니다.")
			.difficultyLevel(level)
			.category(NEWS) // 테스트용이면 null 가능, 실제론 지정
			.practiceCount(0)
			.author(author)
			.build());
	}
}

