package com.example.team.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.team.screen.MainScreenWithDrawer
import com.example.team.screen.vocabulary.VMSWithDrawer
import com.example.team.screen.writeDiary.WMSWithDrawer
import com.example.team.viewmodel.diary.DiaryViewModel

@Composable
fun SeeAppNavGraph(
    navController: NavHostController,
    diaryViewModel: DiaryViewModel
) {
    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreenWithDrawer(
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
            WMSWithDrawer(
                title = "",
                content = "",
                navController = navController,
                viewModel = diaryViewModel
            )
        }

        composable("vocabulary") {
            VMSWithDrawer(
                navController = navController,
                viewModel = diaryViewModel
            )
        }

        composable("write") {
            WMSWithDrawer(
                title = "",
                content = "",
                navController = navController,
                viewModel = diaryViewModel
            )
        }
    }
}