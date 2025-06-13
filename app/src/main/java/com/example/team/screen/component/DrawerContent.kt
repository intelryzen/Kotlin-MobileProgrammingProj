import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.team.viewmodel.diary.DiaryViewModel

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel,
    onDiaryClick: (Int) -> Unit = {},
    onVocabularyClick: () -> Unit = {}
) {
    val diaryList = viewModel.diaryList

    Column(
        modifier = Modifier
            .width(300.dp)
            .background(Color.LightGray)
            .padding(16.dp)
            .fillMaxHeight(1f)
    ) {

        diaryList.forEachIndexed { index, diary ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onDiaryClick(index) },
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = diary.title.ifBlank { "제목 없음" },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onVocabularyClick() },
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "단어장",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "단어장",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}