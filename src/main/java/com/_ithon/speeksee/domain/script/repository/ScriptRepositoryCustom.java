package com._ithon.speeksee.domain.script.repository;

import java.util.List;

import com._ithon.speeksee.domain.member.entity.Member;
import com._ithon.speeksee.domain.script.domain.Script;
import com._ithon.speeksee.domain.script.domain.ScriptSortOption;

public interface ScriptRepositoryCustom {
	List<Script> findByAuthorWithSort(Member author, ScriptSortOption sortOption);
}
