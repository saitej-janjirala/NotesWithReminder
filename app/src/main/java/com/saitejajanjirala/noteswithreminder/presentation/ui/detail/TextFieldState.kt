package com.saitejajanjirala.noteswithreminder.presentation.ui.detail

data class TextFieldState(
    var text : String = "",
    var hint : String = "",
    var isHintVisible : Boolean = false,
    var isEnabled : Boolean = true
)
