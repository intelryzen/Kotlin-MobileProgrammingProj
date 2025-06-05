package com.example.team.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // 추가
import androidx.compose.foundation.verticalScroll      // 추가
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.team.viewmodel.AikuViewModel
import com.example.team.roomDB.ChatEntity
import com.example.team.roomDB.DiaryEntity
import com.example.team.roomDB.VocabEntity
import java.time.LocalDate

@Composable
fun AikuCRUDScreen(viewModel: AikuViewModel = viewModel()) {
    var diaryTitle by remember { mutableStateOf("") }
    var diaryContent by remember { mutableStateOf("") }
    var diaryCorrected by remember { mutableStateOf("") }
    var editingDiaryId by remember { mutableStateOf<Int?>(null) }

    var vocabWord by remember { mutableStateOf("") }
    var vocabPos by remember { mutableStateOf("") }
    var vocabExample by remember { mutableStateOf("") }
    var editingVocabId by remember { mutableStateOf<Int?>(null) }

    var chatContent by remember { mutableStateOf("") }
    var chatIsQuestion by remember { mutableStateOf(true) }
    var editingChatId by remember { mutableStateOf<Int?>(null) }

    val diaries by viewModel.diaries.observeAsState(emptyList())
    val vocabs by viewModel.vocabs.observeAsState(emptyList())
    val chats by viewModel.chats.observeAsState(emptyList())

    val scrollState = rememberScrollState() // 스크롤 상태 추가

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState) // 전체 Column에 세로 스크롤 적용
    ) {
        // 일기 입력 및 리스트
        Text("\uD83D\uDCD8 일기", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(diaryTitle, { diaryTitle = it }, label = { Text("제목") })
        OutlinedTextField(diaryContent, { diaryContent = it }, label = { Text("내용") })
        OutlinedTextField(diaryCorrected, { diaryCorrected = it }, label = { Text("수정된 내용") })
        Button(onClick = {
            if (editingDiaryId != null) {
                viewModel.updateDiary(
                    DiaryEntity(
                        id = editingDiaryId!!,
                        title = diaryTitle,
                        content = diaryContent,
                        correctedContent = diaryCorrected,
                        createdDate = LocalDate.now().toString()
                    )
                )
                editingDiaryId = null
            } else {
                viewModel.insertDiary(diaryTitle, diaryContent, diaryCorrected)
            }
            diaryTitle = ""; diaryContent = ""; diaryCorrected = ""
        }) {
            Text(if (editingDiaryId != null) "Update Diary" else "Add Diary")
        }

        diaries.forEach { diary ->
            Text("\uD83D\uDCC4 ${diary.title}")
            Row {
                Button(onClick = {
                    diaryTitle = diary.title
                    diaryContent = diary.content
                    diaryCorrected = diary.correctedContent
                    editingDiaryId = diary.id
                }) { Text("Edit") }
                Spacer(Modifier.width(4.dp))
                Button(onClick = { viewModel.deleteDiary(diary) }) { Text("Delete") }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 단어장 입력 및 리스트
        Text("\uD83D\uDD02 단어장", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(vocabWord, { vocabWord = it }, label = { Text("단어") })
        OutlinedTextField(vocabPos, { vocabPos = it }, label = { Text("품사") })
        OutlinedTextField(vocabExample, { vocabExample = it }, label = { Text("예문") })
        Button(onClick = {
            if (editingVocabId != null) {
                viewModel.updateVocab(
                    VocabEntity(
                        id = editingVocabId!!,
                        word = vocabWord,
                        partOfSpeech = vocabPos,
                        example = vocabExample,
                        createdDate = LocalDate.now().toString()
                    )
                )
                editingVocabId = null
            } else {
                viewModel.insertVocab(vocabWord, vocabPos, vocabExample)
            }
            vocabWord = ""; vocabPos = ""; vocabExample = ""
        }) {
            Text(if (editingVocabId != null) "Update Vocab" else "Add Vocab")
        }

        vocabs.forEach { vocab ->
            Text("\uD83D\uDCDA ${vocab.word} [${vocab.partOfSpeech}]: ${vocab.example}")
            Row {
                Button(onClick = {
                    vocabWord = vocab.word
                    vocabPos = vocab.partOfSpeech
                    vocabExample = vocab.example
                    editingVocabId = vocab.id
                }) { Text("Edit") }
                Spacer(Modifier.width(4.dp))
                Button(onClick = { viewModel.deleteVocab(vocab) }) { Text("Delete") }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 챗봇 입력 및 리스트
        Text("\uD83E\uDD16 챗봇 기록", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(chatContent, { chatContent = it }, label = { Text("질문 or 답변") })
        Row {
            Text("질문 여부:")
            Checkbox(checked = chatIsQuestion, onCheckedChange = { chatIsQuestion = it })
        }
        Button(onClick = {
            if (editingChatId != null) {
                viewModel.updateChat(
                    ChatEntity(
                        id = editingChatId!!,
                        diaryId = null,
                        isQuestion = chatIsQuestion,
                        content = chatContent,
                        createdDate = LocalDate.now().toString()
                    )
                )
                editingChatId = null
            } else {
                viewModel.insertChat(null, chatIsQuestion, chatContent)
            }
            chatContent = ""
        }) {
            Text(if (editingChatId != null) "Update Chat" else "Add Chat")
        }

        chats.forEach { chat ->
            val prefix = if (chat.isQuestion) "❓Q:" else "\uD83D\uDCACA:"
            Text("$prefix ${chat.content}")
            Row {
                Button(onClick = {
                    chatContent = chat.content
                    chatIsQuestion = chat.isQuestion
                    editingChatId = chat.id
                }) { Text("Edit") }
                Spacer(Modifier.width(4.dp))
                Button(onClick = { viewModel.deleteChat(chat) }) { Text("Delete") }
            }
        }
    }
}
