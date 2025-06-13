//package com.example.team.screen
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.team.viewmodel.diary.DiaryViewModel
//
//@Composable
//fun DiaryCorrectionScreen(
//    viewModel: DiaryViewModel = viewModel()
//) {
//    val uiState by viewModel.uiState
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "일기 교정기",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )
//
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    text = "원본 일기",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Medium,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//
//                OutlinedTextField(
//                    value = uiState.originalText,
//                    onValueChange = { viewModel.updateOriginalText(it) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp),
//                    placeholder = { Text("일기 내용을 입력하세요...") },
//                    enabled = !uiState.isLoading
//                )
//            }
//        }
//
//        Button(
//            onClick = {
//                viewModel.correctDiary(uiState.originalText)
//            },
//            enabled = !uiState.isLoading && uiState.originalText.isNotEmpty(),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//        ) {
//            if (uiState.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.size(20.dp))
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("교정 중...")
//            } else {
//                Text("일기 교정하기")
//            }
//        }
//
//        uiState.errorMessage?.let { errorMessage ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
//            ) {
//                Text(
//                    text = errorMessage,
//                    color = MaterialTheme.colorScheme.onErrorContainer,
//                    modifier = Modifier.padding(16.dp),
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//
//        if (uiState.isSuccess && uiState.correctedText.isNotEmpty()) {
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(
//                        text = "교정된 일기",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer,
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    )
//                    Text(
//                        text = uiState.correctedText,
//                        fontSize = 16.sp,
//                        lineHeight = 24.sp
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            OutlinedButton(
//                onClick = { viewModel.resetCorrection() },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("다시 시작")
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun PrevDiary() {
//    DiaryCorrectionScreen()
//}