package com.example.team

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.team.nav.SeeAppNavGraph
import com.example.team.repository.DiaryRepository
import com.example.team.ui.theme.TeamTheme
import com.example.team.viewmodel.diary.DiaryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamTheme {
                val database = AikuDatabase.getDatabase(this@MainActivity)
                val repository = DiaryRepository(database.diaryDao())
                val diaryViewModel: DiaryViewModel = viewModel { DiaryViewModel(repository) }
                val navController = rememberNavController()
                SeeAppNavGraph(navController = navController, diaryViewModel = diaryViewModel)
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
