package com.saitejajanjirala.noteswithreminder.util

fun main(){
 calculate(1,2) { a, b ->
     a + b
 }
}

fun calculate(a:Int,b:Int,op:(Int,Int)->Int){
    op(a,b)
}