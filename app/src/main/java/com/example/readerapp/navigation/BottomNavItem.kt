package com.example.readerapp.navigation

import com.example.readerapp.R

data class BottomNavItem(
    val route: String,
    val icon: Int,
    val label:String,
)

object BottomNavItems {
    val  BottomNavItems = listOf(
        BottomNavItem("books", R.drawable.ic_books_bn, "Книги"),
        BottomNavItem("upload", R.drawable.ic_upload_bn, "Загрузка"),
        BottomNavItem("profile", R.drawable.ic_profile_bn, "Профиль"),
    )
}