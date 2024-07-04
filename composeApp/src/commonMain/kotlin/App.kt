import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}

@Composable
@Preview
fun Screen() {
    Scaffold(
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }


            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Column () {
                    Text("created by")
                    Text("Mariama djalo Aidara")
                }
                Text(
                    "sweet Dreams",
                    style = MaterialTheme.typography.h1,
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(255, 51, 133, ),  // Sets the background color to red
                        contentColor = Color.White    // Sets the text color to white
                    )
                ) {
                    Text("Spiel zurücksetzen")
                }

                gameState?.let { state ->
                    Text(
                        "Dein Konto: ${currentMoney?.toHumanReadableString()} Coins",
                        style = MaterialTheme.typography.h4,
                    )
                    Button(
                        onClick = { viewModel.clickMoney(state) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(255,51, 133, ),  // Sets the background color to red
                            contentColor = Color.White    // Sets the text color to white
                        )
                    ) {
                        Text("Coins generieren")
                    }

                    state.availableJobs.forEach { availableJob ->
                        Generator(
                            gameJob = availableJob,
                            alreadyBought = state.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(state, availableJob) },
                            onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(8.dp)
            .background(Color(242,170, 143, ), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(" ${gameJob.name}")
            Text("Level: ${gameJob.level.level}")
            Text("Preis: ${gameJob.level.cost.toHumanReadableString()} Coins")
            Text("gewinn: ${gameJob.level.earn.toHumanReadableString()} Coins")
            Text("Dauer: ${gameJob.level.duration.inWholeSeconds} Sekunden")
        }
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(243,  43,146 ),  // Sets the background color to red
                    contentColor = Color.White    // Sets the text color to white
                             )) {

                Text("Kaufen")
            }
        } else {
            Text("gekauft")
        }
        Button(
            onClick = onUpgradecolors,
            colors =ButtonDefaults.buttonColors(
                 backgroundColor = Color(243,  43,146 ),  // Sets the background color to red
                 contentColor = Color.White,   // Sets the text color to white) {Text("erweitern")

    }
}