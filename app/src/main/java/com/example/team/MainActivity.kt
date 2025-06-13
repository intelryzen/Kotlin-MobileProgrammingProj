package com.example.team

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.team.nav.NavGraph
import com.example.team.repository.ChatRepository
import com.example.team.repository.DiaryRepository
import com.example.team.repository.VocabularyRepository
import com.example.team.roomDB.AikuDatabase
import com.example.team.ui.theme.TeamTheme
import com.example.team.viewmodel.diary.DiaryViewModel
import com.example.team.viewmodel.VocabularyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamTheme {
                val database = AikuDatabase.getDatabase(this@MainActivity)

                // 레포지토리들..
                val diaryRepository = DiaryRepository(database.diaryDao())
                val chatRepository = ChatRepository()
                val vocabularyRepository = VocabularyRepository(database.vocabDao())

                // 뷰모델에 레포지토리 주입
                val diaryViewModel: DiaryViewModel = viewModel {
                    DiaryViewModel(diaryRepository, vocabularyRepository, chatRepository) 
                }
                
                val vocabularyViewModel: VocabularyViewModel = viewModel {
                    VocabularyViewModel(vocabularyRepository)
                }

                val navController = rememberNavController()
                NavGraph(
                    navController = navController, 
                    diaryViewModel = diaryViewModel,
                    vocabularyViewModel = vocabularyViewModel
                )
            }
        }
    }
}
