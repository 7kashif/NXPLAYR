package com.nxplayr.fsl.ui.fragments.userconnection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.userconnection.repository.ConnectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListModel : ViewModel() {

    private val connectionRepo = ConnectionRepository()
    val chatList = connectionRepo.chatList
    val connectionSuccess = connectionRepo.mConnectionTypePojo
    val userContactList = connectionRepo.userContactList

    fun getChatList(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            connectionRepo.chatList(json)
        }
    }

    fun connectionList(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            connectionRepo.connectionList(json)
        }
    }

    fun userContactList(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            connectionRepo.userContactList(json)
        }
    }
}
