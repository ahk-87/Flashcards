package flashcards

import java.io.File

class FlashCardsGame() {

    private val cards = mutableMapOf<String, String>()
    private val stats = mutableMapOf<String, Int>()
    private val logContainer = StringBuilder()
    fun printlnLog(s: String) = logContainer.appendLine(s).also { println(s) }
    fun readlnLog() = readln().also { logContainer.appendLine("> $it") }

    fun add() {
        val term = printlnLog("The card:").let { readlnLog() }
        if (cards.containsKey(term)) {
            printlnLog("The card \"$term\" already exists.\n"); return
        }

        val definition = printlnLog("The definition of the card:").let { readlnLog() }
        if (cards.containsValue(definition)) {
            printlnLog("The definition \"$definition\" already exists.\n"); return
        }

        cards[term] = definition
        printlnLog("The pair (\"$term\":\"$definition\") has been added.\n")
    }

    fun remove() {
        val term = printlnLog("Which card?").let { readlnLog() }
        stats.remove(term)
        if (cards.containsKey(term))
            cards.remove(term).also { printlnLog("The card has been removed.\n") }
        else
            printlnLog("Can't remove \"$term\": there is no such card.\n")
    }

    fun import() {
        val file = printlnLog("File name:").let { File(readlnLog()) }
        if (file.exists()) {
            file.readLines().run {
                forEach { line ->
                    val (t, d, m) = line.split("   ")
                    cards[t] = d
                    if (m != "0") stats[t] = m.toInt()
                }
                printlnLog("$size cards have been loaded.\n")
            }
        } else
            printlnLog("File not found.\n")
    }

    fun export() {
        val file = printlnLog("File name:").let { File(readlnLog()) }
        file.writeText(buildString {
            cards.forEach { (k, v) -> appendLine("$k   $v   ${stats[k] ?: "0"}") }
        })
        printlnLog("${cards.size} cards have been saved.\n")
    }

    fun ask() {
        val r = printlnLog("How many times to ask?").let { readlnLog().toInt() }
        repeat(r) {
            val term = cards.keys.random()
            val def = cards[term]!!
            val userInput = printlnLog("Print the definition of \"${term}\":").let { readlnLog() }
            if (userInput == def) {
                printlnLog("Correct!")
            } else {
                stats[term] = (stats[term] ?: 0) + 1
                printlnLog(buildString {
                    append("Wrong. The right answer is \"${def}\"")
                    val defExist = cards.filterValues { it == userInput }.keys.singleOrNull()
                    defExist?.let { append(", but your definition is correct for \"$it\"") }
                    appendLine(".\n")
                })
            }
        }
    }

    fun saveLog() {
        val file = printlnLog("File name:").let { File(readlnLog()) }
        file.writeText(logContainer.toString())
        println("The log has been saved.\n")
    }

    fun showStats() {
        if (stats.isNotEmpty()) {
            val maxErrors = stats.maxOf { it.value }
            val hardestCards = stats.filterValues { it == maxErrors }
            val str = hardestCards.keys.joinToString { "\"$it\"" }
            if (hardestCards.size == 1)
                printlnLog("The hardest card is $str. You have $maxErrors errors answering it.")
            else
                printlnLog("The hardest cards are $str. You have $maxErrors errors answering them.")
        } else
            printlnLog("There are no cards with errors.")
    }

    fun resetStats() = stats.clear().also { printlnLog("Card statistics have been reset") }

    fun play() {
        while (true) {
            printlnLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
            val input = readlnLog()
            when (input) {
                "add" -> add()
                "remove" -> remove()
                "import" -> import()
                "export" -> export()
                "ask" -> ask()
                "log" -> saveLog()
                "hardest card" -> showStats()
                "reset stats" -> resetStats()
                "exit" -> println("Bye bye!").also { return }
            }
        }
    }
}

fun main() {
    FlashCardsGame().play()
}