package com.example.tarea_1_2.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarea_1_2.data.model.Rate
import com.example.tarea_1_2.viewmodel.RatesViewModel

private val currencies = listOf("USD", "HNL", "GTQ", "NIO", "CRC", "PAB")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesScreen(onBack: () -> Unit) {
    val vm: RatesViewModel = viewModel()
    val state by vm.state.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Rate?>(null) }

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasas") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    editing = null
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar tasa") }

            if (state.items.isEmpty() && !state.isLoading) {
                Text("No hay tasas guardadas.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items) { r ->
                        Card(
                            onClick = {
                                editing = r
                                showDialog = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("${r.fromCode} → ${r.toCode}", style = MaterialTheme.typography.titleMedium)
                                    Text("Rate: ${r.rate}")
                                    Text(if (r.isCustom) "Personalizada" else "Base")
                                }

                                IconButton(
                                    onClick = { vm.toggleFavoriteRate(r) }
                                ) {
                                    Text(if (r.isFavorite) "⭐" else "☆")
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    if (showDialog) {
        RateUpsertDialog(
            initial = editing,
            onDismiss = { showDialog = false },
            onSave = { from, to, rate ->
                vm.saveRate(from, to, rate)
                showDialog = false
            }
        )
    }
}

@Composable
private fun RateUpsertDialog(
    initial: Rate?,
    onDismiss: () -> Unit,
    onSave: (from: String, to: String, rate: Double) -> Unit
) {
    var from by remember { mutableStateOf(initial?.fromCode ?: "HNL") }
    var to by remember { mutableStateOf(initial?.toCode ?: "USD") }
    var rateText by remember { mutableStateOf(initial?.rate?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Agregar tasa" else "Editar tasa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CurrencyDropdown("Moneda Origen", from, currencies) { from = it }
                CurrencyDropdown("Moneda Destino", to, currencies) { to = it }

                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it; error = null },
                    label = { Text("Tasa (rate)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                val rate = rateText.replace(',', '.').toDoubleOrNull()
                if (from == to) {
                    error = "Origen y destino no pueden ser iguales."
                    return@Button
                }
                if (rate == null || rate <= 0) {
                    error = "Ingresa una tasa válida (> 0)."
                    return@Button
                }
                onSave(from, to, rate)
            }) { Text("Guardar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(
    label: String,
    value: String,
    options: List<String>,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { code ->
                DropdownMenuItem(
                    text = { Text(code) },
                    onClick = { onChange(code); expanded = false }
                )
            }
        }
    }
}
