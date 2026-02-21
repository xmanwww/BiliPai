package com.android.purebilibili.feature.video.danmaku

private const val REGEX_RULE_PREFIX = "regex:"
private const val SHORT_REGEX_RULE_PREFIX = "re:"
private val DANMAKU_RULE_SPLITTER = Regex("[\\n,，]+")

internal sealed interface DanmakuBlockRuleMatcher {
    fun matches(content: String): Boolean
}

internal data class DanmakuKeywordMatcher(
    val keyword: String
) : DanmakuBlockRuleMatcher {
    override fun matches(content: String): Boolean {
        return content.contains(keyword, ignoreCase = true)
    }
}

internal data class DanmakuRegexMatcher(
    val regex: Regex
) : DanmakuBlockRuleMatcher {
    override fun matches(content: String): Boolean {
        return regex.containsMatchIn(content)
    }
}

fun parseDanmakuBlockRules(raw: String): List<String> {
    return raw.split(DANMAKU_RULE_SPLITTER)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
}

fun matchesDanmakuBlockRule(content: String, rule: String): Boolean {
    val matcher = resolveDanmakuBlockRuleMatcher(rule) ?: return false
    return matcher.matches(content)
}

fun shouldBlockDanmakuByRules(
    content: String,
    rules: List<String>
): Boolean {
    if (content.isBlank() || rules.isEmpty()) return false
    val matchers = compileDanmakuBlockRules(rules)
    if (matchers.isEmpty()) return false
    return matchers.any { it.matches(content) }
}

internal fun shouldBlockDanmakuByMatchers(
    content: String,
    matchers: List<DanmakuBlockRuleMatcher>
): Boolean {
    if (content.isBlank() || matchers.isEmpty()) return false
    return matchers.any { it.matches(content) }
}

internal fun compileDanmakuBlockRules(rules: List<String>): List<DanmakuBlockRuleMatcher> {
    return rules.asSequence()
        .mapNotNull(::resolveDanmakuBlockRuleMatcher)
        .toList()
}

private fun resolveDanmakuBlockRuleMatcher(rule: String): DanmakuBlockRuleMatcher? {
    val normalized = rule.trim()
    if (normalized.isEmpty()) return null

    val regexBody = when {
        normalized.startsWith(REGEX_RULE_PREFIX, ignoreCase = true) -> {
            normalized.substring(REGEX_RULE_PREFIX.length).trim()
        }
        normalized.startsWith(SHORT_REGEX_RULE_PREFIX, ignoreCase = true) -> {
            normalized.substring(SHORT_REGEX_RULE_PREFIX.length).trim()
        }
        normalized.length >= 2 && normalized.startsWith("/") && normalized.endsWith("/") -> {
            normalized.substring(1, normalized.length - 1).trim()
        }
        else -> null
    }

    if (regexBody != null) {
        if (regexBody.isBlank()) return null
        val compiled = runCatching { Regex(regexBody, setOf(RegexOption.IGNORE_CASE)) }.getOrNull()
            ?: return null
        return DanmakuRegexMatcher(compiled)
    }

    return DanmakuKeywordMatcher(normalized)
}
