import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import idle_game.composeapp.generated.resources.Res
import idle_game.composeapp.generated.resources.Screen
import org.jetbrains.compose.resources.painterResource
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
            var showDialog by remember { mutableStateOf(false) }

            Image(
                painterResource(Res.drawable.Screen),
                contentDescription = "A square",
                modifier = Modifier.offset(
                    x = 150.dp, y = 0.dp
                ).width(40000.dp).height(1400.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (showDialog) {
                    MinimalDialog {
                        showDialog = false
                    }
                }
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(255, 51, 133),  // Sets the background color to red
                        contentColor = Color.White
                    )   // Sets the text color to white
                ) {
                    Text("Vorgeschichte")
                }



                Modifier.fillMaxWidth().fillMaxHeight()

                Column() {
                    Text("created by")
                    Text("Mariama Djalo Aidara")
                }
                Text(
                    "sweet Dreams",
                    style = MaterialTheme.typography.h1,
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(255, 51, 133),  // Sets the background color to red
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
                            backgroundColor = Color(255, 51, 133),  // Sets the background color to red
                            contentColor = Color.White    // Sets the text color to white
                        )
                    )
                    {
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
            .background(Color(242, 170, 143), RoundedCornerShape(8.dp))
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
                    backgroundColor = Color(243, 43, 146),  // Sets the background color to red
                    contentColor = Color.White    // Sets the text color to white
                )
            ) {

                Text("Kaufen")
            }
        } else {
            Text("gekauft")
        }
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(243, 43, 146),  // Sets the background color to red
                contentColor = Color.White
            )  // Sets the text color to white) {Text("erweitern")
        ) {
            Text("erweitern")

        }
    }
}

@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(10.dp),
            backgroundColor = Color(232,173,147),
            shape = RoundedCornerShape(25.dp),
        ) {
            Text(
                text = "In sweet Dreams geht es um  Nayla eine Junge Frau die arm aufgewachsen ist \n" +
                        "Und keine Familie hat. Ihr Traum ist es eine Bäckerei zu eröffnen um Sich später " +
                        "ein Sportwagen und eine glamouröse Villa leisten zu können.\n" +
                        "Mit ihrem hart erarbeiteten Coins stellt sie neu Mitarbeiter ein um ihre Bäckerei " +
                        "ins rollen zu bringen. Zwischendurch renoviert sie ihre alte Bäckerei in ein Besonderen Ort." +
                        " Manchmal gehen Mashischinen kaputt oder Mitarbeiter kündigen ihren Job in der Bäckerei.\n" +
                        "Am Ende des Spiels ist Ihre bäckerei in der ganzen Stadt bekannt und sie kann sich endlich " +
                        "Ihr Traum Auto und Ihre Traum Villa Kaufen. " +
                        "Dein Ziel ist es jetzt so viele Münzen zu generieren bis " +
                        "Nayla sich ihre Träume erfüllen kann\n",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}
