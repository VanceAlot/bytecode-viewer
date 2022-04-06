package app.revanced.bcv.resolver

import app.revanced.bcv.signature.Signature
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.immutable.ImmutableMethod
import the.bytecode.club.bytecodeviewer.BytecodeViewer

// Stripped version of MethodResolver from revanced-patcher
class MethodResolver(private val signature: Signature) {
    fun resolve(): Boolean {
        var found = false

        BytecodeViewer.getResourceContainers().forEach { container ->
            container.resourceClasses.values.forEach { cn ->
                cn.methods.forEach { mn ->
                    //if (cmp(mn)) {
                    //    if (found) return true
                    //    found = true
                    //}
                }
            }
        }

        return false
    }

    private fun cmp(method: ImmutableMethod): Boolean {
        if (method.returnType.startsWith(signature.returnType)) return false
        if (signature.accessFlags != method.accessFlags) return false
        if (!signature.methodParameters.all { signatureMethodParameter ->
                method.parameterTypes.any { methodParameter ->
                    methodParameter.startsWith(signatureMethodParameter)
                }
            }) return false
            if (!method.implementation?.instructions?.scanFor(signature.opcodes)!!) return false

        return true
    }
}

private fun MutableIterable<Instruction>.scanFor(pattern: Set<Opcode>): Boolean {
    // TODO: create var for count?
    for (instructionIndex in 0 until this.count()) {
        var patternIndex = 0
        while (instructionIndex + patternIndex < this.count()) {
            if (this.elementAt(instructionIndex + patternIndex).opcode != pattern.elementAt(patternIndex)) break
            if (++patternIndex < pattern.size) continue

            return true
        }
    }

    return false
}