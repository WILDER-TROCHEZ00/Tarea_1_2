package com.example.tarea_1_2.ui.Screen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tarea_1_2.ui.Screen.ConverterScreen
import com.example.tarea_1_2.ui.Screen.HistoryScreen
import com.example.tarea_1_2.ui.Screen.ResultScreen

object Routes {
    const val CONVERTER = "converter"
    const val HISTORY = "history"
    const val RESULT = "result"

    fun resultRoute(id: Long) = "$RESULT/$id"
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.CONVERTER) {

        composable(Routes.CONVERTER) {
            ConverterScreen(
                onGoHistory = { nav.navigate(Routes.HISTORY) },
                onConvertSuccess = { conversionId ->
                    nav.navigate(Routes.resultRoute(conversionId))
                }
            )
        }

        composable(
            route = "${Routes.RESULT}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            ResultScreen(
                conversionId = id,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { nav.popBackStack() },
                onOpenDetails = { id ->
                    nav.navigate(Routes.resultRoute(id))
                }
            )
        }
    }
}
