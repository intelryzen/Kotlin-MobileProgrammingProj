import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.team.model.VocabularyItem
import android.net.Uri

@Composable
fun VocabularyContent(
    wordList: List<VocabularyItem>,
    onMenuClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDeleteWords: (List<VocabularyItem>) -> Unit = {}
) {
    val context = LocalContext.current
    var isDeleteMode by remember { mutableStateOf(false) }
    var selectedWords by remember { mutableStateOf(mutableStateListOf<VocabularyItem>()) }
    var showDeletePopup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 고정 헤더임.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { onMenuClick() }
                )
                
                if (isDeleteMode) {
                    Row {
                        Button(
                            onClick = {
                                if (selectedWords.isNotEmpty()) {
                                    showDeletePopup = true
                                }
                            },
                            enabled = selectedWords.isNotEmpty()
                        ) {
                            Text("삭제하기")
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Button(
                            onClick = {
                                isDeleteMode = false
                                selectedWords.clear()
                            }
                        ) {
                            Text("취소")
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { isDeleteMode = true }
                    )
                }
            }

            Text("단어장", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(wordList) { index, vocab ->
                    Column(
                        modifier = Modifier.clickable {
                            if (!isDeleteMode) {
                                val url = "https://en.dict.naver.com/#/search?query=${Uri.encode(vocab.word)}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isDeleteMode) {
                                Checkbox(
                                    checked = selectedWords.contains(vocab),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            selectedWords.add(vocab)
                                        } else {
                                            selectedWords.remove(vocab)
                                        }
                                    }
                                )
                            }
                            Text(
                                "${index + 1}. ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                vocab.word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text(
                                text = "(${vocab.partOfSpeech})",
                                fontSize = 14.sp,
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(vocab.meaning)
                        Text(
                            text = vocab.example,
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                    }
                }

                // 하단 여백
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // 홈화면 액션 버튼
        FloatingActionButton(
            onClick = onHomeClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = null
            )
        }
    }

    if (showDeletePopup) {
        AlertDialog(
            onDismissRequest = { showDeletePopup = false },
            title = { Text("단어 삭제") },
            text = { Text("${selectedWords.size}개의 단어를 삭제하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWords(selectedWords)
                        isDeleteMode = false
                        selectedWords.clear()
                        showDeletePopup = false
                    }
                ) {
                    Text("네")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeletePopup = false }
                ) {
                    Text("아니오")
                }
            }
        )
    }
}
