package com._ithon.speeksee.domain.voicefeedback.streaming.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com._ithon.speeksee.domain.voicefeedback.streaming.dto.response.WordInfoDto;

public class LcsAligner {

	public static List<WordInfoDto> align(
		List<String> spokenWords,
		List<String> expectedWords,
		List<Double> startTimes,
		List<Double> endTimes
	) {
		int n = expectedWords.size();
		int m = spokenWords.size();
		int[][] dp = new int[n + 1][m + 1];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (expectedWords.get(i).equals(spokenWords.get(j))) {
					dp[i + 1][j + 1] = dp[i][j] + 1;
				} else {
					dp[i + 1][j + 1] = Math.max(dp[i][j + 1], dp[i + 1][j]);
				}
			}
		}

		List<WordInfoDto> result = new ArrayList<>();
		int i = n, j = m;

		while (i > 0 && j > 0) {
			String expected = expectedWords.get(i - 1);
			String spoken = spokenWords.get(j - 1);
			if (expected.equals(spoken)) {
				result.add(new WordInfoDto(spoken, expected, startTimes.get(j - 1), endTimes.get(j - 1), true));
				i--;
				j--;
			} else if (dp[i - 1][j] >= dp[i][j - 1]) {
				result.add(new WordInfoDto("", expected, -1.0, -1.0, false));
				i--;
			} else {
				result.add(new WordInfoDto(spoken, "", startTimes.get(j - 1), endTimes.get(j - 1), false));
				j--;
			}
		}

		while (i > 0) {
			result.add(new WordInfoDto("", expectedWords.get(i - 1), -1.0, -1.0, false));
			i--;
		}
		while (j > 0) {
			result.add(new WordInfoDto(spokenWords.get(j - 1), "", startTimes.get(j - 1), endTimes.get(j - 1), false));
			j--;
		}

		Collections.reverse(result);
		return result;
	}
}

