package com.example.team.nav

import DiaryListScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.team.screen.vocabulary.VocabularyScreen
import com.example.team.screen.writeDiary.WriteDiary
import com.example.team.viewmodel.diary.DiaryViewModel
import com.example.team.viewmodel.vocabulary.VocabularyViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    diaryViewModel: DiaryViewModel,
    vocabularyViewModel: VocabularyViewModel
) {
    NavHost(
        navController = navController, 
        startDestination = "main"
    ) {
        composable("main") {
            DiaryListScreen(
                navController = navController,
                viewModel = diaryViewModel
            )
        }

        composable(
            route = "detail/{diaryIndex}",
            arguments = listOf(
                navArgument("diaryIndex") { 
                    type = NavType.IntType
                    defaultValue = -1 
                }
            )
        ) { backStackEntry ->
            val diaryIndex = backStackEntry.arguments?.getInt("diaryIndex") ?: -1
            if (diaryIndex >= 0) {
                diaryViewModel.currentDiaryIndex = diaryIndex
            }
            WriteDiary(
                navController = navController,
                viewModel = diaryViewModel,
                vocabularyViewModel = vocabularyViewModel
            )
        }

        composable("vocabulary") {
            VocabularyScreen(
                navController = navController,
                viewModel = diaryViewModel,
                vocabularyViewModel = vocabularyViewModel
            )
        }

        // 새 일기 작성
        composable("write") {
            diaryViewModel.currentDiaryIndex = -1
            WriteDiary(
                navController = navController,
                viewModel = diaryViewModel,
                vocabularyViewModel = vocabularyViewModel
            )
        }
    }
}