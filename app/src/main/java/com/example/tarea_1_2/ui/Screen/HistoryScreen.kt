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

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onOpenDetails: (Long) -> Unit
) {
    val vm: HistoryViewModel = viewModel()
    val items by vm.items.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                Card(onClick = { onOpenDetails(item.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("${item.amount} ${item.fromCode} → ${item.toCode}")
                            Text("Resultado: ${item.result} ${item.toCode}")
                        }
                        IconButton(onClick = { vm.toggleFavorite(item) }) {
                            Text(if (item.isFavorite) "⭐" else "☆")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
