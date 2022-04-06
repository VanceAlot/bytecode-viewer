package app.revanced.bcv.resolver

import app.revanced.bcv.signature.Signature

object InstructionHelper {
    // Checks if the signature is unique.
    // If not, tries to generate a signature that is unique.
    fun findUniquePattern(sig: Signature): Signature? {
        if (MethodResolver(sig).resolve()) {
            println("Pattern not unique, retrying with modified sig..")

            return findUniquePattern(uniquePattern(sig) ?: return null)
        }
        println("Found unique pattern!")
        return sig
    }

    // TODO: Make this more efficient by starting with x opcodes and increasing the size of the opcode array.
    private fun uniquePattern(sig: Signature): Signature? {
        if (sig.opcodes.count() <= 1) return null // not possible to find a unique pattern anymore
        sig.opcodes.remove(sig.opcodes.last())
        return sig
    }
}