import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.team.screen.component.DrawerScreen
import com.example.team.viewmodel.diary.DiaryViewModel

@Composable
fun DiaryListScreen(
    navController: NavController,
    viewModel: DiaryViewModel
) {
    DrawerScreen(
        navController = navController,
        viewModel = viewModel
    ) { onMenuClick ->
        DiaryListContent(
            viewModel = viewModel,
            navController = navController,
            onMenuClick = onMenuClick,
            onDetailClick = { index ->
                navController.navigate("detail/$index") {
                    launchSingleTop = true
                }
            },
            onCreateNewDiaryClick = {
                navController.navigate("write") {
                    launchSingleTop = true
                }
            }
        )
    }
}