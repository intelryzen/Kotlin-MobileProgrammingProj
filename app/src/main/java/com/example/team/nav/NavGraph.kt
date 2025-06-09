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

@Composable
fun NavGraph(
    navController: NavHostController,
    diaryViewModel: DiaryViewModel
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
                viewModel = diaryViewModel
            )
        }

        composable("vocabulary") {
            VocabularyScreen(
                navController = navController,
                viewModel = diaryViewModel
            )
        }

        composable("write") {
            // 새 다이어리 작성 시 인덱스 초기화
            diaryViewModel.currentDiaryIndex = -1
            WriteDiary(
                navController = navController,
                viewModel = diaryViewModel
            )
        }
    }
}