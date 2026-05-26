package com.example.quicknotes.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _isLoggedIn = MutableStateFlow(
        auth.currentUser != null
    )

    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userName = MutableStateFlow(
        auth.currentUser?.displayName ?: ""
    )

    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow(
        auth.currentUser?.email ?: ""
    )

    val userEmail: StateFlow<String> = _userEmail

    private val _profilePhoto = MutableStateFlow(
        auth.currentUser?.photoUrl?.toString() ?: ""
    )

    val profilePhoto: StateFlow<String> = _profilePhoto

    fun refreshUser() {

        val user = auth.currentUser

        _isLoggedIn.value = user != null

        _userName.value = user?.displayName ?: ""

        _userEmail.value = user?.email ?: ""

        _profilePhoto.value = user?.photoUrl?.toString() ?: ""
    }

    fun logout(
        onLogoutComplete: () -> Unit = {}
    ) {

        auth.signOut()

        _isLoggedIn.value = false

        _userName.value = ""

        _userEmail.value = ""

        _profilePhoto.value = ""

        onLogoutComplete()
    }
}