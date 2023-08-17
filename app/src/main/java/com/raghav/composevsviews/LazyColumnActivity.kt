package com.raghav.composevsviews

import Item
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raghav.composevsviews.ui.theme.ComposeVsViewsTheme

const val TAG = "LazyColumnActivity"

class LazyColumnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeVsViewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    val viewmodel: LazyColumnActivityVM = viewModel()
                    val state = viewmodel.myListState
                    when (state) {
                        is ListState.Content -> {
                            Content(
                                state = state,
                                modifier = Modifier,
                                onRefresh = { viewmodel.loadItems() },
                                viewModel = viewmodel,
                            )
                        }

                        ListState.Empty -> {
                            viewmodel.loadItems()
                        }

                        ListState.Loading -> {
                            Box {
                                CircularProgressIndicator(
                                    modifier =
                                    Modifier.size(200.dp).align(Alignment.Center),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Content(
    state: ListState.Content,
    viewModel: LazyColumnActivityVM,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        Header()
        ShowList(
            state = state,
            modifier = Modifier,
            onRefresh = { viewModel.loadItems() },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowList(
    state: ListState.Content,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
) {
    Log.d(TAG, "List Composed isRefreshing ${state.isRefreshing}")
    val sortedItems by remember {
        mutableStateOf(state.items)
    }
    val refreshState =
        rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = onRefresh)

    Box(modifier = modifier.pullRefresh(refreshState).fillMaxSize()) {
        LazyColumn(modifier = Modifier.matchParentSize()) {
            Log.d(TAG, "LazyColumn Composed")
            items(items = sortedItems, key = { it.id }) {
                Log.d(TAG, "item ${it.id} Composed")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                ) {
                    Log.d(TAG, "Column ${it.id} Composed")
                    ListItem(item = it)
                    Spacer(modifier = Modifier.size(5.dp))
                }
            }
        }

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
fun ListItem(item: Item, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().height(80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = item.id.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Text(text = "Lazy Column", modifier = modifier)
}

sealed class ListState {
    data class Content(
        val items: List<Item>,
        val isRefreshing: Boolean = false,
    ) : ListState()

    object Empty : ListState()
    object Loading : ListState()
}
