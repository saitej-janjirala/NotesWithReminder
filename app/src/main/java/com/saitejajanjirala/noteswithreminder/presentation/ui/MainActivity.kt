package com.saitejajanjirala.noteswithreminder.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.components.DetailScreen
import com.saitejajanjirala.noteswithreminder.presentation.ui.home.HomeScreen
import com.saitejajanjirala.noteswithreminder.presentation.ui.home.MainViewModel
import com.saitejajanjirala.noteswithreminder.presentation.ui.theme.NotesWithReminderTheme
import com.saitejajanjirala.noteswithreminder.presentation.ui.util.Screen

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel : MainViewModel by viewModels()
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            requestExactAlarmPermission()
        } else {
            // Permission denied, show rationale or inform the user
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesWithReminderTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = Screen.HomeScreen.route){
                    composable(Screen.HomeScreen.route){
                        HomeScreen(navController)
                    }
                    composable(
                        route = Screen.Detail.route+"?note_id={note_id}"+"?is_enabled={is_enabled}",
                        arguments = listOf(
                            navArgument(
                                "note_id"
                            ){
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(
                                "is_enabled"
                            ){
                                type=NavType.BoolType
                                defaultValue = true
                            }
                        ),
                    ){
                        DetailScreen(navController)
                    }
                }
            }
        }
        requestPermissions()

    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${applicationContext.packageName}")
            }
            startActivity(intent)
        }
    }

    private fun requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        else{
            requestExactAlarmPermission()
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesWithReminderTheme {
        Greeting("Android")
    }
}