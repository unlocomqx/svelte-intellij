package dev.blachut.svelte.lang.codeInsight

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.lang.javascript.completion.JSReferenceCompletionUtil
import com.intellij.lang.javascript.psi.JSExpression
import gnu.trove.THashSet

class SvelteCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val text = parameters.position.text
        if (text.startsWith("$")) {
            val varName = text.removePrefix("$").removeSuffix("IntellijIdeaRulezzz")
            val newResult = result.withPrefixMatcher(varName)
            val ref = parameters.position.containingFile.findReferenceAt(parameters.offset)
            if (ref is JSExpression) {
                val variants = JSReferenceCompletionUtil.calcDefaultVariants(ref, ref.containingFile, false, THashSet(), parameters, newResult)
                result.addAllElements(variants)
            }
        }
    }
}
