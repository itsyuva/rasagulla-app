package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.repository.VijayTvRepository
import com.example.ui.screens.EpisodeDirectoryScreen
import com.example.ui.screens.ShowDirectoryScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory
import kotlinx.serialization.Serializable

import androidx.navigation.toRoute

@Serializable
object ShowDirectoryRoute

@Serializable
data class EpisodeDirectoryRoute(
    val showUrl: String,
    val showTitle: String = "",
    val thumbnailUrl: String = ""
)

@Serializable
data class VideoPlayerRoute(
    val episodeUrl: String,
    val episodeTitle: String = "",
    val dateStr: String = ""
)

class MainActivity : ComponentActivity() {

    private val repository by lazy { VijayTvRepository(applicationContext) }
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(repository, applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = ShowDirectoryRoute) {
                        composable<ShowDirectoryRoute> {
                            ShowDirectoryScreen(
                                viewModel = viewModel,
                                onShowSelected = { show ->
                                    navController.navigate(
                                        EpisodeDirectoryRoute(
                                            showUrl = show.url,
                                            showTitle = show.title,
                                            thumbnailUrl = show.thumbnailUrl
                                        )
                                    )
                                },
                                onEpisodeSelected = { episodeUrl, title, date ->
                                    viewModel.recordWatchHistory(title, episodeUrl, date)
                                    navController.navigate(
                                        VideoPlayerRoute(
                                            episodeUrl = episodeUrl,
                                            episodeTitle = title,
                                            dateStr = date
                                        )
                                    )
                                }
                            )
                        }
                        
                        composable<EpisodeDirectoryRoute> { backStackEntry ->
                            val route = backStackEntry.toRoute<EpisodeDirectoryRoute>()
                            EpisodeDirectoryScreen(
                                viewModel = viewModel,
                                showUrl = route.showUrl,
                                showTitle = route.showTitle,
                                thumbnailUrl = route.thumbnailUrl,
                                onBack = { navController.popBackStack() },
                                onEpisodeSelected = { episodeUrl, title, date ->
                                    viewModel.recordWatchHistory(
                                        episodeTitle = title,
                                        episodeUrl = episodeUrl,
                                        dateStr = date,
                                        showTitle = route.showTitle,
                                        showUrl = route.showUrl
                                    )
                                    navController.navigate(
                                        VideoPlayerRoute(
                                            episodeUrl = episodeUrl,
                                            episodeTitle = title,
                                            dateStr = date
                                        )
                                    )
                                }
                            )
                        }
                        
                        composable<VideoPlayerRoute> { backStackEntry ->
                            val route = backStackEntry.toRoute<VideoPlayerRoute>()
                            VideoPlayerScreen(
                                viewModel = viewModel,
                                episodeUrl = route.episodeUrl,
                                episodeTitle = route.episodeTitle,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
