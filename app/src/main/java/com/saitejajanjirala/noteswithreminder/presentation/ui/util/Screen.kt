package com.saitejajanjirala.noteswithreminder.presentation.ui.util

sealed class Screen(val route : String){
    object HomeScreen : Screen("home_screen")
    object Detail : Screen("detail_screen")
}