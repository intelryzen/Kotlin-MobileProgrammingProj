import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.team.R
import com.example.team.ui.theme.GreenGrey80
import com.example.team.viewmodel.diary.DiaryViewModel

@Composable
fun DiaryListContent(
    onMenuClick: () -> Unit = {},
    onDetailClick: (Int) -> Unit = { _ -> },
    navController: NavController,
    onCreateNewDiaryClick: () -> Unit = {},
    viewModel: DiaryViewModel
) {

    val diaryList = viewModel.diaryList
    val expandedDiaries =
        // boolean 타입으로 개수만큼 쭉 펼침
        remember(diaryList.size) { mutableStateListOf(*List(diaryList.size) { false }.toTypedArray()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(1f)
        ) {
            // 햄버거 메뉴
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "메뉴",
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { onMenuClick() }

                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(54.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("AIKU 다이어리", fontWeight = FontWeight.Bold)
                    Text("AI와 함께하는 영어 일기단어장", color = Color.Gray)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // 일기 목록
            LazyColumn {
                itemsIndexed(diaryList) { index, diary ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = GreenGrey80,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable {
                                    expandedDiaries[index] = !expandedDiaries[index]
                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(diary.title, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = if (expandedDiaries[index]) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null
                                )
                            }
                            if (expandedDiaries[index]) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = diary.content,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = {
                                            onDetailClick(index)
                                        }
                                    ) {
                                        Text(text = "자세히보기")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 액션버튼들
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    navController.navigate("vocabulary")
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = "단어장으로 이동"
                )
            }

            FloatingActionButton(
                onClick = {
                    onCreateNewDiaryClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "새 일기 추가"
                )
            }
        }
    }
}
