package app.revanced.bcv

import app.revanced.bcv.signature.Signature
import app.revanced.bcv.util.set
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.jf.dexlib2.AccessFlags

object SignatureGenerator {
    private val json: Gson = GsonBuilder().setPrettyPrinting().create()
    private var index = 0

    fun createSignature(sig: Signature): String {
        val o = JsonObject()
        index++

        o["name"] = "func$index"
        o["returns"] = sig.returnType

        val accessors = JsonArray()
        AccessFlags.getAccessFlagsForMethod(sig.accessFlags).forEach {
            accessors.add(it.name)
        }
        o["accessors"] = accessors

        val parameters = JsonArray()
        sig.methodParameters.forEach {
            // TODO: find elegant way
            parameters.add(it matches Regex("^(\\[*.)"))
        }
        o["parameters"] = parameters

        val opcodes = JsonArray()
        sig.opcodes.forEach { opcodes.add(it.name) }
        o["opcodes"] = opcodes

        return json.toJson(o)
    }
}
