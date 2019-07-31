package dev.blachut.svelte.lang.codeInsight


import com.intellij.lang.ecmascript6.psi.ES6FromClause
import com.intellij.lang.javascript.frameworks.modules.JSModuleFileReferenceSet
import com.intellij.lang.javascript.psi.resolve.JSModuleReferenceContributor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider

class SvelteModuleReferenceContributor : JSModuleReferenceContributor {
    override fun getAllReferences(unquotedEscapedText: String, host: PsiElement, offset: Int, provider: PsiReferenceProvider?): Array<PsiReference> {
        val path = JSModuleReferenceContributor.getActualPath(unquotedEscapedText)
        val modulePath = path.second as String
        val resourcePathStartInd = path.first as Int
        val index = resourcePathStartInd + offset
        return this.getReferences(host, provider, modulePath, index)
    }

    protected fun getReferences(host: PsiElement, provider: PsiReferenceProvider?, modulePath: String, index: Int): Array<PsiReference> {
        val referenceSet = object : JSModuleFileReferenceSet(modulePath, host, index, provider, null) {
            override fun isSoft(): Boolean {
                return true
            }
        }
        return referenceSet.allReferences as Array<PsiReference>
    }

    override fun getCommonJSModuleReferences(unquotedEscapedText: String, host: PsiElement, offset: Int, provider: PsiReferenceProvider?): Array<PsiReference> {
        return PsiReference.EMPTY_ARRAY
    }

    override fun isApplicable(host: PsiElement): Boolean {
        return host is ES6FromClause
    }

}
