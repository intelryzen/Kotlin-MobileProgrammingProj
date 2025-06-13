package com.example.team.screen.component
import DrawerContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.team.viewmodel.diary.DiaryViewModel
import kotlinx.coroutines.launch

// 공통 Screen 의 DrawerScreen 컴포넌트임.
@Composable
fun DrawerScreen(
    navController: NavController,
    viewModel: DiaryViewModel,
    content: @Composable (onMenuClick: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                viewModel = viewModel,
                onDiaryClick = { index ->
                    scope.launch { 
                        drawerState.close() 
                    }
                    navController.navigate("detail/$index") {
                        launchSingleTop = true
                    }
                },
                onVocabularyClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("vocabulary") {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        content { 
            scope.launch { drawerState.open() }
        }
    }
}

