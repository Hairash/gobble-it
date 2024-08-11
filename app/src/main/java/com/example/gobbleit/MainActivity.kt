package com.example.gobbleit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gobbleit.ui.theme.GobbleITTheme
import kotlin.math.abs
import kotlin.math.absoluteValue


object Colors {
    val cell = Color(11, 85, 184)
    val selectedCell = Color(67, 120, 191)
}

class GameStateHolder {
    // Legend: empty cell - 0, first player +1, +2, ..., second player -1, -2, ...
    var gameState = mutableStateOf(Array(4) { Array(4) { mutableListOf<Int>() } })
    var playerChips = mutableStateOf(
        mapOf(
            1 to MutableList(4) { 3 },  // Player 1's chips: 3 of each type
            -1 to MutableList(4) { 3 }  // Player 2's chips: 3 of each type
        )
    )
    var currentPlayer = mutableStateOf(1)  // values: 1 or -1
    var selectedChipHand = mutableStateOf<Int?>(null)
    var selectedChipBoard = mutableStateOf<Pair<Int, Int>?>(null) // to store the position of the selected chip on the board
    var gameWinner = mutableStateOf<Int?>(null)

    fun resetGame() {
        gameState.value = Array(4) { Array(4) { mutableListOf() } }
        playerChips.value = mapOf(1 to MutableList(4) { 3 }, -1 to MutableList(4) { 3 })
        currentPlayer.value = 1
        selectedChipHand.value = null
        selectedChipBoard.value = null
        gameWinner.value = null
    }
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GobbleITTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TicTacToeGame()
                }
            }
        }
    }
}

@Composable
fun TicTacToeGame() {
    val gsh = remember { GameStateHolder() }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in 0 until 4) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (j in 0 until 4) {
                    Button(
                        onClick = {
                            if (gsh.gameWinner.value != null) return@Button
                            if (gsh.selectedChipHand.value != null) {
                                gsh.selectedChipHand.value?.let { chip ->
                                    // Try to place chip from hand to the board
                                    if (gsh.gameState.value[i][j].isEmpty()) {
                                        gsh.gameState.value[i][j].add(gsh.currentPlayer.value * chip)
                                        gsh.playerChips.value[gsh.currentPlayer.value]?.let { list ->
                                            list[chip - 1] -= 1
                                        }
                                        gsh.selectedChipHand.value = null
                                        gsh.currentPlayer.value = -gsh.currentPlayer.value
                                    }
                                    // Or select chip on the board
                                    else if (gsh.gameState.value[i][j].last() / gsh.currentPlayer.value > 0) {
                                        gsh.selectedChipBoard.value = Pair(i, j)
                                        gsh.selectedChipHand.value = null
                                    }
                                }
                            }
                            else if (gsh.selectedChipBoard.value != null) {
                                gsh.selectedChipBoard.value?.let { position ->
                                    // Try to move the selected chip
                                    if (
                                        (
                                            gsh.gameState.value[i][j].isEmpty() ||
                                            abs(gsh.gameState.value[i][j].last()) < abs(gsh.gameState.value[position.first][position.second].last())
                                        ) && gsh.gameState.value[position.first][position.second].last() != 0
                                    ) {
                                        gsh.gameState.value[i][j].add(gsh.gameState.value[position.first][position.second].removeLast())

                                        gsh.selectedChipBoard.value = null
                                        gsh.currentPlayer.value = -gsh.currentPlayer.value
                                    }
                                    // Or select chip on the board
                                    else if (gsh.gameState.value[i][j].last() / gsh.currentPlayer.value > 0) {
                                        gsh.selectedChipBoard.value = Pair(i, j)
                                        gsh.selectedChipHand.value = null
                                    }
                                }
                            }
                            else {
                                // Select the chip on the board
                                if (gsh.gameState.value[i][j].isNotEmpty() && gsh.gameState.value[i][j].last() / gsh.currentPlayer.value > 0) {
                                    gsh.selectedChipBoard.value = Pair(i, j)
                                    gsh.selectedChipHand.value = null
                                }
                            }
                            gsh.gameWinner.value = checkWinCondition(gsh.gameState.value)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (gsh.selectedChipBoard.value == Pair(i, j)) Colors.selectedCell else Colors.cell // Highlighting the selected chip
                        ),
                        modifier = Modifier.size(80.dp)
                    ) {
                        val textColor = if (gsh.gameState.value[i][j].isNotEmpty() && gsh.gameState.value[i][j].last() > 0) Color.White else Color.Black
                        Text(
                            text = if (gsh.gameState.value[i][j].isNotEmpty() && gsh.gameState.value[i][j].last() != 0) abs(gsh.gameState.value[i][j].last()).toString() else "",
                            modifier = Modifier.padding(6.dp),
                            color = textColor,
                            style = TextStyle(fontSize = 30.sp)
                        )
                    }

                }
            }
        }

        // Display available chips
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            gsh.playerChips.value[gsh.currentPlayer.value]?.let { chips ->
                chips.indices.forEach { index ->
                    val isSelected = gsh.selectedChipHand.value == index + 1
                    Button(
                        onClick = {
                            if (chips[index] > 0) {
                                gsh.selectedChipHand.value = index + 1
                                gsh.selectedChipBoard.value = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.Gray else Color.LightGray
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text("${index + 1} (${chips[index]})")
                    }
                }
            }
        }

        Text(
            text = "Current Player: ${if (gsh.currentPlayer.value == 1) "White" else "Black"}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )

        gsh.gameWinner.value?.let {
            Text(
                text = "Player ${if (it > 0) "White" else "Black"} wins!",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
            )
            Button(
                onClick = { gsh.resetGame() },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("Play again")
            }
        }
    }
}

fun checkWinCondition(gameState: Array<Array<MutableList<Int>>>): Int? {
    val size = gameState.size
    // Check rows and columns
    for (i in 0 until size) {
        if (checkLine(gameState[i].map { if (it.isNotEmpty()) it.last() else 0 })) {
            return if (gameState[i][0].last() > 0) 1 else -1
        }
        if (checkLine(Array(size) { j -> gameState[j][i] }.map { if (it.isNotEmpty()) it.last() else 0 })) {
            return if (gameState[0][i].last() > 0)  1 else -1
        }
    }

    // Check diagonals
    if (checkLine(Array(size) { i -> gameState[i][i] }.map { if (it.isNotEmpty()) it.last() else 0 })) {
        return if (gameState[0][0].last() > 0)  1 else -1
    }
    if (checkLine(Array(size) { i -> gameState[i][size - 1 - i] }.map { if (it.isNotEmpty()) it.last() else 0 })) {
        return if (gameState[0][size - 1].last() > 0)  1 else -1
    }

    return null
}

fun checkLine(line: List<Int>): Boolean {
    if (line.any { it == 0 }) return false
    val reference = line[0].absoluteValue / line[0]
    return line.map { it.absoluteValue / it }.all { it == reference }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GobbleITTheme {
        TicTacToeGame()
    }
}
