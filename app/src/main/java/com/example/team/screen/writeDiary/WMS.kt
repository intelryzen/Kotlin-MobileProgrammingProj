package com.example.team.screen.writeDiary

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.team.screen.DrawerContent
import com.example.team.viewmodel.diary.DiaryViewModel
import com.example.team.screen.writeDiary.QnAPopup
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WriteADiary(
    onMenuClick: () -> Unit = {},
    viewModel: DiaryViewModel,
    navController: NavController
) {
    val diary = viewModel.currentDiary
    var showPopup by remember { mutableStateOf(false) }
    val dateFormatter = remember {
        SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    }
    val currentDate = remember { dateFormatter.format(Date()) }
    val context = LocalContext.current

    if (diary == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("작성 중인 일기가 없습니다.\n[새 일기] 버튼을 눌러 시작해 주세요.", fontSize = 16.sp)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(32.dp)
                    .clickable { onMenuClick() }
            )

            OutlinedTextField(
                value = diary.title,
                onValueChange = { diary.title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentDate, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "내가 쓴 일기", fontWeight = FontWeight.Bold,
                    color = if (diary.isOriginal) Color.Black else Color.Gray,
                    modifier = Modifier.clickable { diary.isOriginal = true }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "| 수정된 일기",
                    color = if (!diary.isOriginal) Color.Black else Color.Gray,
                    modifier = Modifier.clickable {
                        diary.isOriginal = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = if (diary.isOriginal) diary.content else diary.editedContent,
                onValueChange = { newValue ->
                    if (diary.isOriginal) diary.content = newValue
                    else diary.editedContent = newValue
                },
                label = { Text(if (diary.isOriginal) "내용" else "수정된 내용") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .weight(1f),
                maxLines = 10
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    /* 일기 작성 완료 */
                    Toast.makeText(context, "일기 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }) {
                    Text("완료")
                }
                Button(onClick = {
                    /* 일기 수정 완료*/
                    Toast.makeText(context, "일기 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }) {
                    Text("수정")
                }
                Button(onClick = {
                    /* 일기 삭제 완료*/
                    viewModel.deleteCurrentDiary()
                    Toast.makeText(context, "일기 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack("main", inclusive = false)
                }) {
                    Text("삭제")
                }
                if (!diary.isOriginal) {
                    Button(
                        onClick = { /*단어수집동작 */
                            diary.wordCollect = !diary.wordCollect
                        }

                    ) {
                        Text(
                            "단어 수집"
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp, bottom = 90.dp)
                .size(48.dp)
                .background(Color.DarkGray, shape = CircleShape)
                .clickable {
                    showPopup = true
                },
            contentAlignment = Alignment.Center
        )
        {
            Text(
                text = "T",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        if (showPopup) {
            QnAPopup(onClose = { showPopup = false })
        }
    }
}


@Composable
fun WMSWithDrawer(
    title: String,
    content: String,
    navController: NavController,
    viewModel: DiaryViewModel
) {
    val drawerState =
        rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                viewModel = viewModel,
                onDiaryClick = { index ->
                    scope.launch {
                        drawerState.close()
                        val diary = viewModel.diaryList.getOrNull(index) ?: return@launch
                        viewModel.currentDiaryIndex = index
                        navController.navigate("detail/${Uri.encode(diary.title)}/${Uri.encode("Drawer에서 진입한 일기입니다.")}")
                    }
                },
                onVocabularyClick = {
                    println("단어장 이동")
                    scope.launch { drawerState.close() }
                    navController.navigate("vocabulary")
                }
            )
        }
    ) {
        WriteADiary(
            onMenuClick = {
                scope.launch { drawerState.open() }
            },
            viewModel = viewModel,
            navController = navController
        )
    }
}