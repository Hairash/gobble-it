package com.example.gobbleit

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gobbleit.ui.theme.GobbleITTheme
import kotlin.math.abs


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
    // Legend: empty cell - 0, first player +1, +2, ..., second player -1, -2, ...
    val gameState = remember { mutableStateOf(Array(4) { Array(4) { 0 } }) }
    val currentPlayer = remember { mutableStateOf(1) }  // values: 1 or -1
    val chipsPlayer1 = remember { mutableStateOf(listOf(3, 3, 3, 3)) }  // Player 1's chips
    val chipsPlayer2 = remember { mutableStateOf(listOf(3, 3, 3, 3)) }  // Player 2's chips
    val selectedChip = remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in 0 until 4) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (j in 0 until 4) {
                    Button(
                        onClick = {
//                            if (gameState.value[i][j] == 0 && selectedChip.value != null) {
//                                val newState = gameState.value.map { it.clone() }.toTypedArray() // Clone the existing state
//                                Log.d("TicTacToe", "${currentPlayer.value}") // Debugging log
//                                Log.d("TicTacToe", "${selectedChip.value!!}") // Debugging log
//                                Log.d("TicTacToe", "${newState[i][j]}") // Debugging log
//
//                                newState[i][j] = currentPlayer.value * selectedChip.value!!
//                                gameState.value = newState // Reassign the state to trigger recomposition
//                                Log.d("TicTacToe", "Cell [$i, $j] set to ${currentPlayer.value}") // Debugging log
//                                Log.d("TicTacToe", availableChips.value[currentPlayer.value + 1 / 2].toString())
//                                selectedChip.value = null
//                                currentPlayer.value = -currentPlayer.value
//                            }

                            selectedChip.value?.let { chip ->
                                if (gameState.value[i][j] == 0) {
                                    val newState = gameState.value.map { it.clone() }.toTypedArray()
                                    newState[i][j] = currentPlayer.value * chip
                                    gameState.value = newState
                                    if (currentPlayer.value == 1) {
                                        chipsPlayer1.value = chipsPlayer1.value.toMutableList().apply { this[chip - 1] -= 1 }
                                    } else {
                                        chipsPlayer2.value = chipsPlayer2.value.toMutableList().apply { this[chip - 1] -= 1 }
                                    }
                                    selectedChip.value = null
                                    currentPlayer.value = -currentPlayer.value
                                }
                            }
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        val textColor = if (gameState.value[i][j] > 0) Color.White else Color.Black
                        Text(
                            text = if (gameState.value[i][j] != 0) abs(gameState.value[i][j]).toString() else "",
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
            val chips = if (currentPlayer.value == 1) chipsPlayer1.value else chipsPlayer2.value
            chips.indices.forEach { index ->
                Button(onClick = {
                    if (chips[index] > 0) selectedChip.value = index + 1
                }) {
                    Text("${index + 1} (${chips[index]})")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GobbleITTheme {
        TicTacToeGame()
    }
}
