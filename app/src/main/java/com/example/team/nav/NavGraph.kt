package com.example.team.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.team.screen.MainScreenWithDrawer
import com.example.team.screen.vocabulary.VMSWithDrawer
import com.example.team.screen.writeDiary.WMSWithDrawer
import com.example.team.screen.writeDiary.WriteADiary
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
            route = "detail/{title}/{content}",
            arguments = listOf(
                navArgument("title") { defaultValue = "" },
                navArgument("content") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val content = backStackEntry.arguments?.getString("content") ?: ""
            WMSWithDrawer(
                title = title,
                content = content,
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
            WriteADiary(
                navController = navController,
                viewModel = diaryViewModel
            )
        }
    }
}