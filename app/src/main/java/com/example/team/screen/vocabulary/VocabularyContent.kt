import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.team.model.Vocabulary

@Composable
fun VocabularyContent(
    wordList: List<Vocabulary>,
    onMenuClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { onMenuClick() }
                    )
                }
            }
            
            item {
                Text("단어장", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
            }

            itemsIndexed(wordList) { index, vocab ->
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "${index + 1}. ${vocab.word}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${vocab.partOfSpeech} ${vocab.meaning}")
                    Text(
                        text = vocab.example,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                }
            }
            
            // 하단 여백 (FloatingActionButton과의 겹침 방지)
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // 홈화면으로 가는 FloatingActionButton
        FloatingActionButton(
            onClick = onHomeClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "홈화면으로 이동"
            )
        }
    }
}
