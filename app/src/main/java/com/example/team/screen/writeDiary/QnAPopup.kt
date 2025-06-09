package com.example.team.screen.writeDiary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.team.roomDB.ChatEntity
import com.example.team.viewmodel.diary.DiaryViewModel

@Composable
fun QnAPopup(
    onClose: () -> Unit,
    viewModel: DiaryViewModel
) {
    val allChats by viewModel.chats.observeAsState(emptyList())
    val diaryId = viewModel.currentDiary?.id ?: return
    val chats = allChats.filter { it.diaryId == diaryId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Q&A", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text(
                    "x",
                    modifier = Modifier
                        .clickable { onClose() }
                        .padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (chats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF0F0F0))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("QnA 기록이 없습니다.", fontSize = 14.sp, color = Color.DarkGray)
                }
            } else {
                chats.forEach { chat ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFCFCFC), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .padding(vertical = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = if (chat.isQuestion) "Q: ${chat.content}" else "A: ${chat.content}",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = chat.createdDate,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}