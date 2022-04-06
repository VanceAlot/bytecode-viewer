package app.revanced.bcv.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import eu.bibl.banalysis.asm.desc.OpcodeInfo
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LineNumberNode

internal operator fun JsonObject.set(k: String, v: String) =
    this.addProperty(k, v)

internal operator fun JsonObject.set(k: String, v: JsonElement) =
    this.add(k, v)