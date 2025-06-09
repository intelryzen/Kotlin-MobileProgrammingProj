package com.example.team.screen.writeDiary

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.team.viewmodel.diary.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WriteDiary(
    viewModel: DiaryViewModel,
    navController: NavController
) {
    val diary = viewModel.currentDiary
    val isNewDiary = viewModel.currentDiaryIndex == -1
    var showPopup by remember { mutableStateOf(false) }
    
    // 새 일기 작성용 상태
    var title by remember { mutableStateOf(diary?.title ?: "") }
    var content by remember { mutableStateOf(diary?.content ?: "") }
    var editedContent by remember { mutableStateOf(diary?.editedContent ?: "") }
    var isOriginal by remember { mutableStateOf(true) }
    
    val dateTimeFormatter = remember {
        SimpleDateFormat("yyyy년 M월 d일 (HH:mm)", Locale.getDefault())
    }

    // 새 일기인지 기존 일기인지 판단하여 적절한 날짜 표시
    val displayDateTime = remember(diary) {
        if (diary != null) {
            dateTimeFormatter.format(diary.createdAt)
        } else {
            dateTimeFormatter.format(Date())
        }
    }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 앱바 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .size(22.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )

                Text(
                    text = displayDateTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                // 오른쪽 공간을 맞추기 위한 투명한 스페이서
                Spacer(modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (isNewDiary) title else diary?.title ?: "",
                onValueChange = { 
                    if (isNewDiary) title = it
                    else diary?.let { title = it.title }
                },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            if (!isNewDiary && diary != null) {
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
            }

            OutlinedTextField(
                value = if (isNewDiary) {
                    content
                } else if (diary?.isOriginal == true) {
                    diary.content
                } else {
                    diary?.editedContent ?: ""
                },
                onValueChange = { newValue ->
                    if (isNewDiary) {
                        content = newValue
                    } else if (diary?.isOriginal == true) {
                        diary?.let { it.content = newValue }
                    } else {
                        diary?.let { it.editedContent = newValue }
                    }
                },
                label = { Text(
                    if (isNewDiary) "내용" 
                    else if (diary?.isOriginal == true) "내용" 
                    else "수정된 내용"
                ) },
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
                Button(
                    onClick = {
                        if (isNewDiary) {
                            // 새 일기 저장
                            viewModel.saveNewDiary(
                                title = title,
                                content = content,
                                onSuccess = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            // 기존 일기 저장
                            viewModel.saveDiary(
                                onSuccess = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    // 저장 후 수정된 일기 탭으로 자동 전환
                                    diary?.let { it.isOriginal = false }
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text("저장")
                    }
                }
                if (!isNewDiary && diary != null) {
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




