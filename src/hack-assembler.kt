import java.io.File

fun main(args: Array<String>) {
    initSymbolTable()
    println(toHackMachineCode(" @17 "))
    println(toHackMachineCode(" @-2"))
    println(toHackMachineCode("fff=D-M"))
}

val symbolTable = mutableMapOf<String, Int>()

fun initSymbolTable() {
    for (i in 0..15) {
        symbolTable["R$i"] = i
    }
    symbolTable["SCREEN"] = 16384
    symbolTable["KBD"] = 24576
}

fun readFile(file: String) = File(file).forEachLine { line: String -> toHackMachineCode(line) }

fun toHackMachineCode(line: String): String {
    val line = line.trim()
    return if (line.startsWith('@')) {
        toAInstruction(line)
    } else {
        toCInstruction(line)
    }
}

fun toCInstruction(line: String): String {
    val cInstruction = StringBuilder("111")
    if (line.contains(';')) {
        val instructions = line.split(';')
    } else {
        cInstruction.append(toDestCompInstruction(line))
    }
    return cInstruction.toString()
}

fun toDestCompInstruction(line: String): String {
    val destCompInstruction = StringBuilder()
    val instructions = line.split('=')
    if (instructions[1].contains('M')) {
        destCompInstruction.append("1")
    } else {
        destCompInstruction.append("0")
    }
    destCompInstruction.append(
        when (instructions[1]) {
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
            else -> {}
        }
    )



    return destCompInstruction.toString()
}

fun toAInstruction(line: String): String {
    val value = line.removeRange(0..0)
    val binaryString = toBinaryString(value)
    return "0$binaryString"
}

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

