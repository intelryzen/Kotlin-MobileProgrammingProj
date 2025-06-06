
package com.example.team.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.team.R
import com.example.team.viewmodel.diary.DiaryViewModel
import kotlinx.coroutines.launch

@Composable
fun DiaryListScreen(onMenuClick:() -> Unit = {},
                    onDetailClick: (String, String) -> Unit = {_,_ ->},
                    navController: NavController,
                    onCreateNewDiaryClick: () -> Unit = {},
                    viewModel: DiaryViewModel
                    ) {

    val context = LocalContext.current
    val diaryList = viewModel.diaryList
    val expandedStates = remember(diaryList.size) { mutableStateListOf(*List(diaryList.size) {false}.toTypedArray()) }

        Column(modifier = Modifier.padding(16.dp)
            .fillMaxHeight(1f)) {
            // 햄버거 메뉴
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.padding(bottom = 8.dp)
                        .clickable { onMenuClick() }

                )
            }

            // 프로필
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.im2),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Name", fontWeight = FontWeight.Bold)
                    Text("Description", color = Color.Gray)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // 다이어리 목록
            LazyColumn {
                itemsIndexed(diaryList) { index, diary ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable {
                                    expandedStates[index] = !expandedStates[index]
                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(diary.title, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = if (expandedStates[index]) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null
                                )
                            }
                            if (expandedStates[index]) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = diary.content, maxLines = 1, overflow = TextOverflow.Ellipsis)

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = {
                                            viewModel.currentDiaryIndex = index
                                            onDetailClick(diary.title, diary.content)
                                        }
                                    ) {
                                        Text(text = "자세히보기")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(onClick = {
                viewModel.createNewDiary()
                Toast.makeText(context, "새 일기가 시작되었습니다.", Toast.LENGTH_SHORT).show()
                onCreateNewDiaryClick()
            }) {
                Text("새 일기")
            }
        }
    }

@Composable
fun MainScreenWithDrawer(
    navController: NavController,
    viewModel: DiaryViewModel
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                viewModel = viewModel,
                onDiaryClick = {index ->
                    val diary = viewModel.diaryList.getOrNull(index) ?: return@DrawerContent
                    viewModel.currentDiaryIndex = index
                    scope.launch { drawerState.close() }
                    navController.navigate("detail/${Uri.encode(diary.title)}/${Uri.encode("Drawer에서 진입한 일기입니다.")}")
                },
                onVocabularyClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("vocabulary")
                }
            )
        }
    ) {
        DiaryListScreen(
            viewModel = viewModel,
            navController = navController,
            onMenuClick = {
                scope.launch { drawerState.open() }
            },
            onDetailClick = { title, content ->
                val encodedTitle = Uri.encode(title)
                val encodedContent = Uri.encode(content)
                navController.navigate("detail/$encodedTitle/$encodedContent")
            },
            onCreateNewDiaryClick = {
                navController.navigate("write")
            }
        )
    }
}