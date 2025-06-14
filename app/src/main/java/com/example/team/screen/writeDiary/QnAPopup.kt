package com.example.team.screen.writeDiary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.team.ui.theme.LightGreen80
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

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Q&A", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text(
                        "X",
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "일기 내용",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Text(
                            text = "원본:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp, max = 120.dp)
                                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = diary?.content ?: "일기 내용이 없습니다.",
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Text(
                            text = "수정된 내용:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp, max = 120.dp)
                                .background(LightGreen80, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = diary?.correctedContent ?: "수정된 내용이 없습니다.",
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "답변 내용",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 150.dp)
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
                                color = Color.Black,
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

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
                        viewModel.askQuestion(
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
}

@Preview
@Composable
private fun QnAPopupPreview() {
    // QnAPopup(viewModel) {  }
}
