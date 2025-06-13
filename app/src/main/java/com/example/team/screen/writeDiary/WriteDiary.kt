package com.example.team.screen.writeDiary

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.navigation.NavController
import com.example.team.model.VocabularyItem
import com.example.team.ui.theme.LightGreen80
import com.example.team.viewmodel.diary.DiaryViewModel
import com.example.team.viewmodel.vocabulary.VocabularyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WriteDiary(
    viewModel: DiaryViewModel,
    navController: NavController,
    vocabularyViewModel: VocabularyViewModel? = null
) {
    val diary = viewModel.currentDiary
    val isNewDiary = viewModel.currentDiaryIndex == -1

    var showPopup by remember { mutableStateOf(false) }
    var showVocabSelection by remember { mutableStateOf(false) }
    var vocabularyItems by remember { mutableStateOf<List<VocabularyItem>>(emptyList()) }

    // 뒤로가기 버튼 연속 클릭 방지
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    // 새 일기 작성용 상태 => diary가 변경될 때마다 업데이트
    var title by remember(diary) { mutableStateOf(diary?.title ?: "") }
    var content by remember(diary) { mutableStateOf(diary?.content ?: "") }

    val dateTimeFormatter = remember {
        SimpleDateFormat("yyyy년 M월 d일 (HH시 mm분)", Locale.getDefault())
    }

    // 새 일기인지 기존 일기인지 판단하여 날짜 표시
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
                        .clickable(enabled = isBackButtonEnabled) {
                            if (isBackButtonEnabled) {
                                isBackButtonEnabled = false
                                navController.popBackStack()
                            }
                        }
                )

                Text(
                    text = displayDateTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 새 일기 작성일 때만 더미 텍스트 타일 표시
            if (isNewDiary && title.isEmpty() && content.isEmpty()) {
                SampleTextTile(
                    onSampleTextSelected = { sampleTitle, sampleContent ->
                        title = sampleTitle
                        content = sampleContent
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = if (isNewDiary) title else diary?.title ?: "",
                onValueChange = { newValue ->
                    if (isNewDiary) {
                        title = newValue
                    } else {
                        diary?.let { it.title = newValue }
                    }
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
                        text = " |  수정된 일기", fontWeight = FontWeight.Bold,
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
                    diary?.correctedContent ?: ""
                },
                onValueChange = { newValue ->
                    if (isNewDiary) {
                        content = newValue
                    } else {
                        diary?.let { currentDiary ->
                            if (currentDiary.isOriginal) {
                                currentDiary.content = newValue
                            } else {
                                currentDiary.correctedContent = newValue
                            }
                        }
                    }
                },
                label = {
                    Text(
                        if (isNewDiary) "영어 일기 내용"
                        else if (diary?.isOriginal == true) "내용"
                        else "수정된 내용"
                    )
                },
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
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                    )
                } else if (isNewDiary) {
                    // 새 일기인 경우 교정 버튼만 표시
                    Button(
                        onClick = {
                            viewModel.saveNewDiary(
                                title = title,
                                content = content,
                                onSuccess = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    // 저장 후 수정된 일기 탭으로 자동 전환
                                    viewModel.currentDiary?.let { savedDiary ->
                                        savedDiary.isOriginal = false
                                    }
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        enabled = !viewModel.isLoading
                    ) {
                        Text("AI 교정 및 저장")
                    }
                } else if (diary != null) {

                    // 기존 일기인 경우 수정, 삭제 버튼만 표시
                    Button(
                        onClick = {
                            viewModel.updateCurrentDiary(
                                onSuccess = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    // 수정 후 수정된 일기 탭으로 자동 전환
                                    diary.isOriginal = false
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        enabled = !viewModel.isLoading
                    ) {
                        Text("수정")
                    }

                    Button(
                        onClick = {
                            viewModel.deleteCurrentDiary(
                                onSuccess = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    navController.popBackStack("main", inclusive = false)
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        enabled = !viewModel.isLoading
                    ) {
                        Text("삭제")
                    }

                    // 수정된 일기 탭에서만 단어 수집 버튼 표시
                    if (!diary.isOriginal) {
                        Button(
                            onClick = {
                                viewModel.collectVocabularies(
                                    onSuccess = { items ->
                                        vocabularyItems = items
                                        showVocabSelection = true
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            enabled = !viewModel.isLoading
                        ) {
                            Text("단어 수집")
                        }
                        Button(
                            onClick = {
                                showPopup = true
                            },
                            enabled = !viewModel.isLoading
                        ) {
                            Text("Q&A")
                        }
                    }
                }
            }
        }

        if (showPopup) {
            QnAPopup(
                viewModel = viewModel,
                onClose = { showPopup = false }
            )
        }

        if (showVocabSelection) {
            VocabPopup(
                vocabularyItems = vocabularyItems,
                onSaveSelected = { selectedItems ->
                    viewModel.saveSelectedVocabulary(
                        selectedItems = selectedItems,
                        onSuccess = { message ->
                            diary?.let { it.wordCollect = true }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            showVocabSelection = false
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        },
                        onVocabularyUpdated = {
                            vocabularyViewModel?.refreshVocabulary()
                        }
                    )
                },
                onDismiss = {
                    showVocabSelection = false
                }
            )
        }
    }
}

@Composable
fun SampleTextTile(
    onSampleTextSelected: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSampleTextSelected(
                    "My School Day",
                    "Tody is a very good date at school. I waked up early in the morning and eat breakfast with my family."
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = LightGreen80
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "처음 사용하시나요?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "이곳을 클릭하여 예시 일기로 시작해보세요!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

