package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.collection.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionViewModel : ViewModel() {

    private val collectionRepo = CollectionRepository()
    val createAlbumList = collectionRepo.createAlbumList
    val assignPostAlbumCreated = collectionRepo.assignPostAlbumCreated
    val deleteAlbum = collectionRepo.deleteAlbum
    val createAlbum = collectionRepo.createAlbum
    val editAlbum = collectionRepo.editAlbum
    val failure = collectionRepo.failureLiveData

    fun createAlbumList(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.createAlbumList(json)
        }
    }

    fun assignPostAlbum(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.assignPostAlbum(json)
        }
    }

    fun deleteAlbum(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.deleteAlbum(json)
        }
    }

    fun createAlbum(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.createAlbum(json)
        }
    }

    fun editAlbum(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.editAlbum(json)
        }
    }
}