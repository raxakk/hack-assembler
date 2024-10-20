import java.io.File

val output = StringBuilder()
val symbolTable = mutableMapOf<String, Int>()
var currentLineNumber = 0
var addressCounter = 16

fun main(args: Array<String>) {
    initSymbolTable()
    try {
        val lines = File(args[0]).readLines()
            .filter { line -> !line.trim().startsWith("//") && line.trim().isNotEmpty() }
            .map { line -> line.replace("//.*".toRegex(), "").trim() }
        lines.forEach { line: String ->
            if (line.contains("(") && line.contains(")")) {
                addLabelSymbol(line)
            } else {
                currentLineNumber++
            }

        }
        lines.forEach { line: String ->
            if (!line.contains("(") && !line.contains(")")) {
                output.append(toHackMachineCode(line)).append(System.lineSeparator())
            }
        }
    } catch (e: Exception) {
        println("ERROR in line $currentLineNumber")
    }

    File(args[1]).writeText(output.toString())
}

fun addLabelSymbol(line: String) {
    symbolTable[line.replace("(", "").replace(")", "")] = currentLineNumber
}

fun initSymbolTable() {
    for (i in 0..15) {
        symbolTable["R$i"] = i
    }
    symbolTable["SCREEN"] = 16384
    symbolTable["KBD"] = 24576
    symbolTable["SP"] = 0
    symbolTable["LCL"] = 1
    symbolTable["ARG"] = 2
    symbolTable["THIS"] = 3
    symbolTable["THAT"] = 4
}


fun toHackMachineCode(hackInstruction: String): String {
    currentLineNumber++
    return if (hackInstruction.startsWith('@')) {
        toAInstruction(hackInstruction)
    } else {
        toCInstruction(hackInstruction)
    }
}

fun toCInstruction(line: String): String {
    val cInstruction = StringBuilder("111")
    var dest = ""
    var jump = ""
    if (line.contains(';')) {
        jump = line.split(';').last()
    }
    if (line.contains('=')) {
        dest = line.split('=').first()
    }
    val comp: String = line.replace(".*=".toRegex(), "").replace(";.*".toRegex(), "")
    appendCompInstruction(comp, cInstruction)
    appendDestInstruction(dest, cInstruction)
    appendJumpInstruction(jump, cInstruction)
    return cInstruction.toString()
}

fun appendDestInstruction(dest: String, instruction: StringBuilder) {
    instruction.append(
        when (dest) {
            "" -> "000"
            "M" -> "001"
            "D" -> "010"
            "DM", "MD" -> "011"
            "A" -> "100"
            "AM" -> "101"
            "AD" -> "110"
            "ADM", "AMD" -> "111"
            else -> throw Exception("SyntaxError")
        }
    )
}

fun appendJumpInstruction(jump: String, instruction: StringBuilder) {
    instruction.append(
        when (jump) {
            "" -> "000"
            "JGT" -> "001"
            "JEQ" -> "010"
            "JGE" -> "011"
            "JLT" -> "100"
            "JNE" -> "101"
            "JLE" -> "110"
            "JMP" -> "111"
            else -> throw Exception("SyntaxError")
        }
    )
}

fun appendCompInstruction(comp: String, instruction: StringBuilder) {
    if (comp.contains('M')) {
        instruction.append("1")
    } else {
        instruction.append("0")
    }
    instruction.append(
        when (comp) {
            "0" -> "101010"
            "1" -> "111111"
            "-1" -> "111010"
            "D" -> "001100"
            "A", "M" -> "110000"
            "!D" -> "001101"
            "!A", "!M" -> "110001"
            "-D" -> "001111"
            "-A", "-M" -> "110011"
            "D+1" -> "011111"
            "A+1", "M+1" -> "110111"
            "D-1" -> "001110"
            "A-1", "M-1" -> "110010"
            "D+A", "D+M" -> "000010"
            "D-A", "D-M" -> "010011"
            "A-D", "M-D" -> "000111"
            "D&A", "D&M" -> "000000"
            "D|A", "D|M" -> "010101"
            else -> throw Exception("SyntaxError")
        }
    )
}

fun toAInstruction(line: String): String {
    val symbol = line.removeRange(0..0)
    return if (isNumeric(symbol)) {
        toBinaryString(symbol)
    } else {
        var value = symbolTable[symbol]
        if (value == null) {
            value = addressCounter
            symbolTable[symbol] = addressCounter++
        }
        toBinaryString(value.toString())
    }
}

fun isNumeric(string: String) = string.toIntOrNull() != null

fun toBinaryString(value: String): String {
    var binaryString = Integer.toBinaryString(value.toShort().toInt())
    if (binaryString.length > 16) {
        binaryString = binaryString.substring(binaryString.length - 16, binaryString.length)
    } else {
        while (binaryString.length < 16) {
            binaryString = "0$binaryString"
        }
    }
    return binaryString
}
