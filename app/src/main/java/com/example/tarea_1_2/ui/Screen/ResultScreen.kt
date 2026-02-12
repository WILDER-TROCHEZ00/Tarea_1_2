package com.example.tarea_1_2.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarea_1_2.viewmodel.ResultViewModel

@Composable
fun ResultScreen(
    conversionId: Long,
    onBack: () -> Unit
) {
    val vm: ResultViewModel = viewModel()
    val item by vm.item.collectAsState()

    LaunchedEffect(conversionId) {
        vm.load(conversionId)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Resultado", style = MaterialTheme.typography.headlineSmall)

        val c = item
        if (c == null) {
            Text("Cargando...")
        } else {
            Text("Monto original: ${c.amount} ${c.fromCode}")
            Text("Tasa aplicada: ${c.rate}")
            Text("Operación: ${c.amount} × ${c.rate}")
            Text("Resultado: ${c.result} ${c.toCode}")
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

