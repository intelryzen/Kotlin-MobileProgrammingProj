package com.example.team.screen.writeDiary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.team.viewmodel.diary.DiaryViewModel

@Composable
fun QnAPopup(
    viewModel: DiaryViewModel,
    onClose: () -> Unit
) {
    val diary = viewModel.currentDiary
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
                .align(Alignment.Center)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Q&A", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text(
                    "×",
                    modifier = Modifier
                        .clickable { onClose() }
                        .padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // 스크롤 가능한 내용 영역
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // 일기 내용란
                Text(
                    text = "일기 내용",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 200.dp)
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "원본:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = diary?.content ?: "일기 내용이 없습니다.",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "수정됨:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = diary?.editedContent ?: "수정된 내용이 없습니다.",
                            fontSize = 14.sp,
                            color = Color.Blue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                // 답변 내용
                Text(
                    text = "답변 내용",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .background(Color(0xFFFCFCFC), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    if (answer.isEmpty()) {
                        Text(
                            text = "질문을 하면 답변이 여기에 표시됩니다.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            text = answer,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 질문 입력란과 전송 버튼
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("질문을 입력하세요 (예: 왜 그렇게 바꾼거야?)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    viewModel.askQuestionAboutDiary(
                        question = question,
                        onSuccess = { response ->
                            answer = response
                            question = "" // 질문 초기화
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = !viewModel.isLoading && question.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("전송")
                }
            }
        }
    }
}

    @Preview
    @Composable
    private fun QnAPopupPreview() {
        // QnAPopup(viewModel) {  }
    }
