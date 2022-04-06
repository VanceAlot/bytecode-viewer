package app.revanced.bcv.signature

import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode
import java.util.EnumSet

@Suppress("ArrayInDataClass")
data class Signature(
    val name: String,
    val returnType: String,
    val accessFlags: Int,
    val methodParameters: Iterable<String>,
    val opcodes: MutableSet<Opcode>
)