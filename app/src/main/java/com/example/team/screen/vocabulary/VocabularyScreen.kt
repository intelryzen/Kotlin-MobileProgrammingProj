package com.example.team.screen.vocabulary

import VocabularyContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.team.screen.common.DrawerScreen
import com.example.team.viewmodel.diary.DiaryViewModel
import com.example.team.viewmodel.vocabulary.VocabularyViewModel

@Composable
fun VocabularyScreen(
    navController: NavController,
    viewModel: DiaryViewModel,
    vocabularyViewModel: VocabularyViewModel
) {

    DrawerScreen(
        navController = navController,
        viewModel = viewModel
    ) { onMenuClick ->
        VocabularyContent(
            wordList = vocabularyViewModel.vocabularyList,
            onMenuClick = onMenuClick,
            onHomeClick = {
                navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }
            },
            onDeleteWords = { words ->
                vocabularyViewModel.deleteWords(words)
            }
        )
    }
}
