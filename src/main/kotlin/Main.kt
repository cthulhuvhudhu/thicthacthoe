import kotlin.math.abs

enum class Status(internal val desc: String) {
    X_WIN("X wins"),
    O_WIN("O wins"),
    DRAW("Draw"),
    ACTIVE("Game not finished"),
    N_A("Impossible");
}

enum class Player(internal val value: Char) {
    X('X'),
    O('O'),
    EMPTY('_');

    fun getOpponent(): Player {
        check(this != EMPTY) { "Not a player, doesn't have an opponent" }
        return if (this == X) O
        else X
    }
}
const val SIZE = 3
val board = MutableList(SIZE) { MutableList(SIZE) { Player.EMPTY } }


fun main() {

    println(boardToString(board))
    var currentTurn = Player.X

    while (analyze() == Status.ACTIVE) {
        val move = readln().split(" ")
        try {
            val y = move[0].toInt()
            val x = move[1].toInt()
            if (x !in 1..SIZE || y !in 1..SIZE) {
                println("Coordinates should be from 1 to $SIZE!")
                continue
            } else if (board[y-1][x-1] != Player.EMPTY) {
                println("This cell is occupied! Choose another one!")
                continue
            } else {
                board[y-1][x-1] = currentTurn
                currentTurn = currentTurn.getOpponent()
                println(boardToString(board))
            }
        } catch (e: NumberFormatException) {
            println("You should enter numbers!")
        }
    }
    println(analyze().desc)
}

fun boardToString(board: List<List<Player>>): String {
    return buildString {
        appendLine("---------")
        board.forEach { it ->
            appendLine("| ${it.map { it.value }.joinToString(" ")} |")
        }
        appendLine("---------")
    }
}

fun analyze(): Status {
    val totX = board.flatten().count { it == Player.X }
    val totO = board.flatten().count { it == Player.O }

    // There should be max 1 difference between Xs and Os
    if (abs(totX - totO) > 1) {
        return Status.N_A
    }

    val candidateRows = buildCandidateRows()
    val wins = calcWins(candidateRows)

    if (wins.first > 0) {
        if (wins.second > 0) {
            return Status.N_A
        }
        return Status.X_WIN
    }
    if (wins.second > 0) {
        return Status.O_WIN
    }
    if (board.flatten().any { it == Player.EMPTY }) {
        return Status.ACTIVE
    }
    return Status.DRAW
}

fun buildCandidateRows(): List<List<Player>> {
    val candidateRows = mutableListOf<List<Player>>()
    candidateRows.addAll(board)

    // Check columns
    (0..<SIZE).forEach { c ->
        val colPlayers = mutableListOf<Player>()
        (0 until SIZE).forEach { r ->
            colPlayers.add(board[r][c])
        }
        candidateRows.add(colPlayers)
    }

    // Check diagonals
    val l2r = mutableListOf<Player>()
    val r2l = mutableListOf<Player>()

    (0..<SIZE).forEach { i ->
        l2r.add(board[i][i])
        r2l.add(board[i][SIZE-1-i])
    }
    candidateRows.add(l2r)
    candidateRows.add(r2l)
    return candidateRows
}

fun calcWins(candidates: List<List<Player>>): Pair<Int, Int> {
    val xWins = candidates.count { row -> row.all { it == Player.X } }
    val oWins = candidates.count { row -> row.all { it == Player.O } }

    return xWins to oWins
}
