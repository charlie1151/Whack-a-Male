package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MoleGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                MoleGameScreen(viewModel)
            }
        }
    }
}

@Composable
fun MoleGameScreen(viewModel: MoleGameViewModel) {
    val score by viewModel.score.collectAsState()  //讓viewmodel收集資料
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val moleIndices by viewModel.moleIndices.collectAsState()
    val hammerIndex by viewModel.hammerIndex.collectAsState()
    val gameResult by viewModel.gameResult.collectAsState()

    // 啟動遊戲
    LaunchedEffect(Unit) {
        viewModel.startGame()
    }
    //主畫面編排
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF98FB98))
            .padding(16.dp)
    ) {
        if (gameResult.isNotEmpty()) { //看gameResult是否有值
            GameOverScreen(score, gameResult)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Score: $score",
                    fontSize = 24.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Time: $timeRemaining",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    MoleGrid(
                        moleIndices = moleIndices,
                        hammerIndex = hammerIndex,
                        onMoleHit = { viewModel.hitMole(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun MoleGrid(
    moleIndices: List<Int>,
    hammerIndex: Int,
    onMoleHit: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (row in 0 until 4) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    MoleHole(
                        hasMole = moleIndices.contains(index),  //回傳是否有地鼠
                        hasHammer = index == hammerIndex, //是否需要槌子
                        onClick = { onMoleHit(index) } //是否點擊
                    )
                }
            }
        }
    }
}

@Composable
fun MoleHole(
    hasMole: Boolean,
    hasHammer: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hole),
            contentDescription = "Hole",
            modifier = Modifier.size(100.dp)
        )
        if (hasMole) {
            Image(
                painter = painterResource(id = R.drawable.mouse),
                contentDescription = "Mole",
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onClick() }
            )
        }
        if (hasHammer) {
            Image(
                painter = painterResource(id = R.drawable.yyy),
                contentDescription = "Hammer",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun GameOverScreen(score: Int, result: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        val resultText = if (result == "win") {
            "You Win!\nScore: $score"
        } else {
            "You Lose!\nScore: $score"
        }
        Text(
            text = resultText,
            fontSize = 28.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}







