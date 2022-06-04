package com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.friendrequest.repository.FriendRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendListModel : ViewModel() {
    var friendRepo = FriendRepo()
    val successFriend = friendRepo.successFriend
    val failure = friendRepo.failure

    fun friendApi(json: String, from: String) {
        viewModelScope.launch(Dispatchers.IO) {
            friendRepo.friendApi(json, from)
        }
    }
}
