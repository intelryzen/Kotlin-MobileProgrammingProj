package com.example.team.nav

import DiaryListScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.team.AikuDatabase
import com.example.team.repository.VocabularyRepository
import com.example.team.screen.vocabulary.VocabularyScreen
import com.example.team.screen.writeDiary.WriteDiary
import com.example.team.viewmodel.diary.DiaryViewModel
import com.example.team.viewmodel.VocabularyViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    diaryViewModel: DiaryViewModel
) {
    val context = LocalContext.current
    
    // VocabularyRepository와 VocabularyViewModel 생성
    val vocabularyRepository = remember {
        val database = AikuDatabase.getDatabase(context)
        VocabularyRepository(database.vocabDao())
    }
    
    val vocabularyViewModel = remember {
        VocabularyViewModel(vocabularyRepository)
    }
    
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

        composable("write") {
            // 새 일기 작성 모드로 설정
            diaryViewModel.currentDiaryIndex = -1
            WriteDiary(
                navController = navController,
                viewModel = diaryViewModel,
                vocabularyViewModel = vocabularyViewModel
            )
        }
    }
}