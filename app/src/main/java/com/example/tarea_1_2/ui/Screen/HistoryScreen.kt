package com.example.tarea_1_2.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarea_1_2.viewmodel.HistoryViewModel
import java.util.Locale

private fun d2(x: Double) = String.format(Locale.US, "%.2f", x)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onOpenDetails: (Long) -> Unit
) {
    val vm: HistoryViewModel = viewModel()
    val items by vm.items.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (items.isEmpty()) {
                Text("No hay conversiones aún.")
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    Card(onClick = { onOpenDetails(item.id) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("${d2(item.amount)} ${item.fromCode} → ${item.toCode}")
                                Text("Resultado: ${d2(item.result)} ${item.toCode}")
                            }
                            IconButton(onClick = { vm.toggleFavorite(item) }) {
                                Text(if (item.isFavorite) "⭐" else "☆")
                            }
                        }
                    }
                }
            }
        }
    }
}


