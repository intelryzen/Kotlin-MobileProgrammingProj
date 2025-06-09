package com.example.team.screen.component
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.team.viewmodel.diary.DiaryViewModel
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(modifier: Modifier = Modifier,
                  viewModel: DiaryViewModel,
                  onDiaryClick: (Int) -> Unit = {},
                  onVocabularyClick: () -> Unit = {}
) {
    val diaryList = viewModel.diaryList

    Column (modifier = Modifier
        .width(300.dp)
        .background(Color.LightGray)
        .padding(16.dp)
        .fillMaxHeight(1f)){

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
                .clickable { onVocabularyClick()},
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Text(
                text = "단어장",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// 공통 DrawerScreen 컴포넌트
@Composable
fun DrawerScreen(
    navController: NavController,
    viewModel: DiaryViewModel,
    content: @Composable (onMenuClick: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                viewModel = viewModel,
                onDiaryClick = { index ->
                    scope.launch { 
                        drawerState.close() 
                        navController.navigate("detail/$index")
                    }
                },
                onVocabularyClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("vocabulary")
                }
            )
        }
    ) {
        content { 
            scope.launch { drawerState.open() }
        }
    }
}

