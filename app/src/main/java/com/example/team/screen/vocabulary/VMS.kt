package com.example.team.screen.vocabulary

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.team.model.Vocabulary
import com.example.team.screen.DrawerContent
import com.example.team.viewmodel.diary.DiaryViewModel
import kotlinx.coroutines.launch


@Composable
fun VMS(wordList: List<Vocabulary>,
        onMenuClick:() -> Unit = {}
) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(1f)
        ) {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { onMenuClick() }
                )
            }
            Text("단어장", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Divider()

            wordList.forEachIndexed { index, vocab ->
                Spacer(modifier = Modifier.height(12.dp))

                Column {
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
        }
}

@Composable
fun VMSWithDrawer(
    navController: NavController,
    viewModel: DiaryViewModel
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val sample = listOf(
        Vocabulary("weekend", "N.", "주말, 주말 휴가", "Are you doing anything over the weekend?"),
        Vocabulary("as soon as", "...", "하자마자", "Just as soon as the city issues the permit...")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                viewModel = viewModel,
                onDiaryClick = { index ->
                    val diary = viewModel.diaryList.getOrNull(index) ?: return@DrawerContent
                    viewModel.currentDiaryIndex = index
                    navController.navigate("detail/${Uri.encode(diary.title)}/${Uri.encode("Drawer에서 진입한 일기입니다.")
                    }")},
                onVocabularyClick = {
                    println("단어장 이동")
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        VMS(
            wordList = sample,
            onMenuClick = {
                scope.launch { drawerState.open() }
            }
        )
    }
}
