package com._ithon.speeksee.global.seed;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com._ithon.speeksee.domain.script.domain.DifficultyLevel;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.domain.ScriptCategory;
import com._ithon.speeksee.domain.script.repository.ScriptRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitScriptData implements CommandLineRunner {
	private final ScriptRepository scriptRepository;

	@Override
	public void run(String... args) {
		// 중복 방지: 이미 레벨 테스트 스크립트가 있으면 건너뜀
		if (scriptRepository.existsByIsLevelTest(true))
			return;

		List<Script> scripts = List.of(
			Script.builder()
				.title("레벨 테스트 대본 1")
				.content(
					"오늘날 세계 경제는 복잡하고 역동적인 변화 속에 놓여 있습니다. 각국은 지속 가능한 성장을 위해 다양한 정책을 수립하고 있으며, 기후 변화와 같은 글로벌 이슈에 대해 협력하고자 노력하고 있습니다. 이러한 환경 속에서 기업들은 혁신적인 기술 도입과 경영 전략을 통해 경쟁력을 강화하고 있습니다. 한편, 소비자들은 더 나은 품질과 서비스에 대한 기대가 높아지고 있으며, 이는 시장 변화에 중요한 영향을 미치고 있습니다. 마지막으로, 전문가들은 미래의 경제 패러다임 전환을 주의 깊게 주시하면서 새로운 기회를 모색하고 있습니다.")
				.category(ScriptCategory.NEWS)
				.difficultyLevel(DifficultyLevel.MEDIUM)
				.isLevelTest(true)
				.build(),

			Script.builder()
				.title("레벨 테스트 대본 2")
				.content(
					"오늘은 날씨가 참 변덕스럽네요. 아침에는 햇살이 가득했지만, 오후 들어 흐려지더니 비가 내리기 시작했어요. 기상예보에 따르면 이번 비는 내일 아침까지 계속될 거라고 해요. 어제와 비교하면 기온도 조금 내려가서 쌀쌀한 느낌이에요. 이런 날씨에는 두꺼운 옷을 챙기는 게 좋겠어요.")
				.category(ScriptCategory.WEATHER)
				.difficultyLevel(DifficultyLevel.MEDIUM)
				.isLevelTest(true)
				.build(),

			Script.builder()
				.title("레벨 테스트 대본 3")
				.content(
					"안녕하세요, 제 이름은 김민수입니다. 저는 서울에서 태어나고 자랐고, 현재는 대학생입니다. 제 전공은 경영학이고, 다양한 문화에 관심이 많아서 해외 여행을 즐깁니다. 여가 시간에는 책을 읽거나 음악을 듣는 것을 좋아합니다. 앞으로도 많은 경험을 쌓고 싶어서 여러 도전을 준비하고 있습니다.")
				.category(ScriptCategory.SELF_INTRODUCTION)
				.difficultyLevel(DifficultyLevel.MEDIUM)
				.isLevelTest(true)
				.build(),

			Script.builder()
				.title("레벨 테스트 대본 4")
				.content(
					"안녕하세요, 제 이름은 김영수입니다. 저는 서울에서 태어나 자랐고, 현재는 대전에 살고 있습니다. 대학교에서는 컴퓨터 공학을 전공했으며, 지금은 IT 회사에서 엔지니어로 일하고 있습니다. 취미로는 주말마다 등산을 즐기고, 책 읽는 것도 좋아합니다. 앞날에 더 많은 도전에 나설 수 있기를 기대하고 있습니다.")
				.category(ScriptCategory.SELF_INTRODUCTION)
				.difficultyLevel(DifficultyLevel.MEDIUM)
				.isLevelTest(true)
				.build(),

			Script.builder()
				.title("레벨 테스트 대본 5")
				.content(
					"지난 주말에는 친구들과 함께 산에 갔어요. 산 정상에 오르기까지 꽤 오래 걸렸지만, 정상에서 본 경치가 정말 멋졌어요. 우리는 점심을 먹으면서 서로의 근황에 대해 이야기했어요. 하산할 때는 다리가 조금 아팠지만, 친구들과 함께여서 즐거웠어요. 내려오는 길에 우리는 다음에는 바다로 여행 가자는 계획을 세웠어요.")
				.category(ScriptCategory.DAILY)
				.difficultyLevel(DifficultyLevel.MEDIUM)
				.isLevelTest(true)
				.build()
		);

		scriptRepository.saveAll(scripts);
	}
}
