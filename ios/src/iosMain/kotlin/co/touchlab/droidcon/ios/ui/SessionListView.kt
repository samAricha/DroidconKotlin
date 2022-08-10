package co.touchlab.droidcon.ios.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import co.touchlab.droidcon.ios.NavigationController
import co.touchlab.droidcon.ios.NavigationStack
import co.touchlab.droidcon.ios.viewmodel.session.BaseSessionListViewModel
import co.touchlab.kermit.Logger

@Composable
internal fun SessionListView(viewModel: BaseSessionListViewModel) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    NavigationStack(links = {
        NavigationLink(viewModel.observePresentedSessionDetail) {
            SessionDetailView(viewModel = it)
        }
    }) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Droidcon Berlin 2022") }) },
        ) {
            Column {
                val days by viewModel.observeDays.observeAsState()
                if (days?.isEmpty() != false) {
                    EmptyView()
                } else {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        indicator = { tabPositions ->
                            if (tabPositions.indices.contains(selectedTabIndex)) {
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                                )
                            } else {
                                Logger.w("SessionList TabRow requested an indicator for selectedTabIndex: $selectedTabIndex, but only got ${tabPositions.count()} tabs.")
                                TabRowDefaults.Indicator()
                            }
                        }
                    ) {
                        days?.forEachIndexed { index, daySchedule ->
                            Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                                Text(
                                    text = daySchedule.day,
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    days?.forEachIndexed { index, _ ->
                        val state = rememberLazyListState()
                        if (index == selectedTabIndex) {
                            LazyColumn(state = state, contentPadding = PaddingValues(vertical = 4.dp)) {
                                val daySchedule = days?.getOrNull(selectedTabIndex)?.blocks ?: emptyList()
                                items(daySchedule) { hourBlock ->
                                    Box(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
                                        SessionBlockView(hourBlock)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(16.dp),
            tint = Color.Yellow,
        )

        Text(
            text = "Sessions could not be loaded.",
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}
