package app.revanced.bcv.actions

import app.revanced.bcv.SignatureGenerator
import app.revanced.bcv.resolver.InstructionHelper
import app.revanced.bcv.signature.Signature
import org.objectweb.asm.Type
import org.objectweb.asm.tree.MethodNode
import the.bytecode.club.bytecodeviewer.BytecodeViewer
import the.bytecode.club.bytecodeviewer.decompilers.impl.SmaliDisassembler
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.BytecodeViewPanel
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class GenerateSignatureAction(private val panel: BytecodeViewPanel) : ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        val line = panel.updateThread.updateUpdaterTextArea.caretLineNumber + 1 // we're always 1 less?
        val method = findMethod(line)
        if (method == null) {
            BytecodeViewer.showMessage("This line does not contain a method.")
            return
        }
        if (method.name.equals("<init>") || method.name.equals("<clinit>")) {
            val dialog = MultipleChoiceDialog(
                "Bytecode Viewer",
                """
                    Creating signatures for constructors usually won't work
                    if they are not very unique (have a lot of opcodes).
                    Are you sure you want to continue?
                """.trimIndent(),
                arrayOf(
                    TranslatedStrings.YES.toString(),
                    TranslatedStrings.NO.toString()
                )
            )
            if (dialog.promptChoice() != 0) return
        }

        //val sig = InstructionHelper.findUniquePattern(
        //    Signature(
        //        Type.getReturnType(method.desc),
        //        method.access,
        //        Type.getArgumentTypes(method.desc),
        //        method.instructions
        //    )
        //)
        //if (sig == null) {
        //    BytecodeViewer.showMessage("Could not find unique signature for method \"${method.name}\"!")
        //    return
        //}
//
        //BytecodeViewer.updateBusyStatus(true)
        //val selection = StringSelection(SignatureGenerator.createSignature(sig))
        //Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
        //BytecodeViewer.updateBusyStatus(false)
//
        //BytecodeViewer.showMessage(
        //    """
        //        Copied signature of method "${method.name}" to clipboard!
        //        Please note that you must always make some manual
        //        changes before pushing it to the repository.
        //    """.trimIndent()
        //)
    }

    private fun findMethod(line: Int): MethodNode? {
        panel.methods.forEach {
            if (it.key.contains(line)) {
                return@findMethod it.value
            }
        }
        return null
    }
}