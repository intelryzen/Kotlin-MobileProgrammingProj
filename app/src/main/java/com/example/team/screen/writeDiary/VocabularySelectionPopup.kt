package com.example.team.screen.writeDiary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.team.model.VocabularyItem

@Composable
fun VocabularySelectionPopup(
    vocabularyItems: List<VocabularyItem>,
    onSaveSelected: (List<VocabularyItem>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedItems by remember { 
        mutableStateOf(vocabularyItems.map { it to true }.toMap()) 
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "단어 선택",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row {
                        TextButton(onClick = {
                            selectedItems = vocabularyItems.associateWith { true }
                        }) {
                            Text("전체 선택")
                        }
                        
                        TextButton(onClick = {
                            selectedItems = vocabularyItems.associateWith { false }
                        }) {
                            Text("전체 해제")
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // 단어 목록
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vocabularyItems) { item ->
                        VocabularyItemRow(
                            item = item,
                            isSelected = selectedItems[item] ?: false,
                            onSelectionChanged = { isSelected ->
                                selectedItems = selectedItems.toMutableMap().apply {
                                    put(item, isSelected)
                                }
                            }
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // 하단 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("취소")
                    }
                    
                    Button(
                        onClick = {
                            val selected = selectedItems.filter { it.value }.keys.toList()
                            onSaveSelected(selected)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        val selectedCount = selectedItems.values.count { it }
                        Text("저장 ($selectedCount)")
                    }
                }
            }
        }
    }
}

@Composable
private fun VocabularyItemRow(
    item: VocabularyItem,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.word,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${item.partOfSpeech} ${item.meaning}",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.exampleSentence,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
} 