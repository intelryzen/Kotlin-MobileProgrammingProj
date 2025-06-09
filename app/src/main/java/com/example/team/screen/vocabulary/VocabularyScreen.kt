package com.example.team.screen.vocabulary

import VocabularyContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.team.model.Vocabulary
import com.example.team.screen.component.DrawerScreen
import com.example.team.viewmodel.diary.DiaryViewModel

@Composable
fun VocabularyScreen(
    navController: NavController,
    viewModel: DiaryViewModel
) {
    val sample = listOf(
        Vocabulary("weekend", "N.", "주말, 주말 휴가", "Are you doing anything over the weekend?"),
        Vocabulary("as soon as", "...", "하자마자", "Just as soon as the city issues the permit...")
    )

    DrawerScreen(
        navController = navController,
        viewModel = viewModel
    ) { onMenuClick ->
        VocabularyContent(
            wordList = sample,
            onMenuClick = onMenuClick,
            onHomeClick = {
                navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }
            }
        )
    }
}
