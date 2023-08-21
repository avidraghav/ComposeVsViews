package com.raghav.composevsviews

import Item
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LazyColumnActivityVM : ViewModel() {

    var myListState: ListState by mutableStateOf(ListState.Empty)
        private set

    fun loadItems() {
        viewModelScope.launch(Dispatchers.Default) {
            updateLoadingState()
            delay(1000)
            val unsortedItems = (1..20).toList()
                .map { index ->
                    loadItem(index)
                }.toImmutableList()
            updateContentState(items = unsortedItems)
        }
    }

    suspend fun updateLoadingState() {
        withContext(Dispatchers.Main) {
            if (myListState is ListState.Content) {
                myListState = (myListState as ListState.Content).copy(
                    isRefreshing = true,
                )
            } else {
                myListState = ListState.Loading
            }
        }
    }

    suspend fun updateContentState(items: ImmutableList<Item>) {
        withContext(Dispatchers.Main) {
            myListState = ListState.Content(
                items = items,
            )
        }
    }

    fun loadItem(position: Int): Item {
        return Item(
            id = position,
        )
    }
}
