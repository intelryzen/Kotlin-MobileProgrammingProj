//package com.example.team.screen.vocabulary
//
//import android.net.Uri
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.team.screen.DrawerContent
//import com.example.team.viewmodel.diary.DiaryViewModel
//import kotlinx.coroutines.launch
//
//@Composable
//fun VMS(
//    wordList: List<com.example.team.roomDB.VocabEntity>,
//    onMenuClick: () -> Unit = {}
//) {
//    Column(
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxHeight()
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Start
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Menu,
//                contentDescription = "Menu",
//                modifier = Modifier
//                    .padding(bottom = 8.dp)
//                    .clickable { onMenuClick() }
//            )
//        }
//        Text("단어장", fontSize = 28.sp, fontWeight = FontWeight.Bold)
//
//        Spacer(modifier = Modifier.height(8.dp))
//        Divider()
//
//        wordList.forEachIndexed { index, vocab ->
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Column {
//                Text(
//                    "${index + 1}. ${vocab.word}",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    textDecoration = TextDecoration.Underline
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text("${vocab.partOfSpeech} ${vocab.meaning}")
//                Text(
//                    text = vocab.example,
//                    color = Color.Gray,
//                    fontSize = 13.sp
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Divider()
//            }
//        }
//    }
//}
//
//@Composable
//fun VMSWithDrawer(
//    navController: NavController,
//    viewModel: DiaryViewModel
//) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//
//    val vocabList by viewModel.vocabs.observeAsState(emptyList())
//
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            DrawerContent(
//                viewModel = viewModel,
//                onDiaryClick = { index ->
//                    val diary = viewModel.diaries.value?.getOrNull(index) ?: return@DrawerContent
//                    viewModel.currentDiaryIndex.value = index
//                    navController.navigate("detail/${Uri.encode(diary.title)}/${Uri.encode("Drawer에서 진입한 일기입니다.")}")
//                },
//                onVocabularyClick = {
//                    scope.launch { drawerState.close() }
//                }
//            )
//        }
//    ) {
//        VMS(
//            wordList = vocabList,
//            onMenuClick = {
//                scope.launch { drawerState.open() }
//            }
//        )
//    }
//}
