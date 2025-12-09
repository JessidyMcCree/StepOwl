package pt.iade.games.stepowl.Components

import android.media.Image
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.iade.games.stepowl.R
import pt.iade.games.stepowl.ui.theme.StepOwlTheme

@Composable
fun QuestItem(
    title: String,
    description: String,
    currentValue: Int,
    goal: Int,
    questActive: Boolean = false,

    onGoClick: () -> Unit = {}
) {
    if(currentValue >= goal && questActive){
        Text( "LESS GO")
    } else {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(10.dp)
                ) {
                    Text(title)
                    Text(
                        text = description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    modifier = Modifier
                        .width(50.dp)
                        .padding(5.dp, 5.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (questActive) {
                        Text(
                            text = "$currentValue"
                        )
                    } else {
                        Button(
                            onClick = onGoClick
                        ) {
                            Text("Go!")
                        }
                    }
                }
            }
        }
    }

}

@Composable
@Preview(showBackground = false)
fun QuestItemPreview() {
    StepOwlTheme {
        Column {
            QuestItem(
                title = "A Doable Thing",
                description = "Get the Gold dust! Walk 30 steps!",
                currentValue = 8,
                goal = 10
            )

            QuestItem(
                title = "A Doable Thing",
                description = "Get the Gold dust! Walk 30 steps!",
                currentValue = 8,
                questActive = true,
                goal = 20
            )
        }
    }
}
