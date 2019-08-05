package dev.blachut.svelte.lang.codeInsight

import com.intellij.lang.javascript.psi.JSNamedElement
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.resolve.JSResolveResult
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.filters.ElementFilter
import com.intellij.psi.filters.position.FilterPattern
import com.intellij.util.ProcessingContext

class SvelteReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val pattern = PlatformPatterns.psiElement(JSReferenceExpression::class.java).and(FilterPattern(object : ElementFilter {
            override fun isAcceptable(element: Any, context: PsiElement?): Boolean {
                if (element is JSReferenceExpression) {
                    return element.text.startsWith("$")
                }
                return false
            }

            override fun isClassAcceptable(hintClass: Class<*>): Boolean {
                return true
            }
        }))
        registrar.registerReferenceProvider(pattern, object : PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                return arrayOf(SvelteReactiveReference(element, element.textRange))
            }
        })
    }
}

class SvelteReactiveReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val varName = element.text.removePrefix("$")
        val decl: JSNamedElement = JSResolveUtil.findFileLocalElement(varName, element.containingFile) ?: return ResolveResult.EMPTY_ARRAY
        return arrayOf(JSResolveResult(decl))
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }
}
