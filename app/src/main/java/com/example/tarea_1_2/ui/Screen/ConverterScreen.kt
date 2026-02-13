package com.example.tarea_1_2.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarea_1_2.viewmodel.ConverterViewModel
import androidx.compose.ui.platform.LocalFocusManager


private val currencies = listOf("USD", "HNL", "GTQ", "NI0", "CRC", "PAB")

@Composable
fun ConverterScreen(
    onGoHistory: () -> Unit,
    onGoRates: () -> Unit,
    onConvertSuccess: (Long) -> Unit
) {
    val vm: ConverterViewModel = viewModel()
    val state by vm.state.collectAsState()

    var amountText by rememberSaveable { mutableStateOf("") }
    var fromCode by rememberSaveable { mutableStateOf("HNL") }
    var toCode by rememberSaveable { mutableStateOf("USD") }
    var errorLocal by rememberSaveable { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current



    LaunchedEffect(state.lastId) {
        val id = state.lastId
        if (id != null) {
            // ✅ limpiar UI antes de navegar
            amountText = ""
            errorLocal = null
            focusManager.clearFocus()

            onConvertSuccess(id)
            vm.consumeLastId()
        }
    }




    LaunchedEffect(state.error) {
        if (state.error != null) errorLocal = state.error
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Conversor", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = amountText,
            onValueChange = { newValue ->
                amountText = newValue
                errorLocal = null
            },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !state.isLoading
        )

        CurrencyDropdown(
            label = "Moneda Origen",
            value = fromCode,
            options = currencies,
            onChange = { fromCode = it },
            enabled = !state.isLoading
        )

        CurrencyDropdown(
            label = "Moneda Destino",
            value = toCode,
            options = currencies,
            onChange = { toCode = it },
            enabled = !state.isLoading
        )

        if (errorLocal != null) {
            Text(errorLocal!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val amount = amountText.replace(',', '.').toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    errorLocal = "Ingresa un monto válido (mayor que 0)."
                    return@Button
                }
                if (fromCode.isBlank() || toCode.isBlank()) {
                    errorLocal = "Selecciona monedas válidas."
                    return@Button
                }


                vm.convert(amount, fromCode, toCode)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = amountText.isNotBlank() && !state.isLoading
        ) {
            Text(if (state.isLoading) "Convirtiendo..." else "Convertir")
        }

        OutlinedButton(
            onClick = onGoHistory,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) { Text("Historial") }

        OutlinedButton(
            onClick = onGoRates,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) { Text("Tasas") }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(
    label: String,
    value: String,
    options: List<String>,
    onChange: (String) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { code ->
                DropdownMenuItem(
                    text = { Text(code) },
                    onClick = {
                        onChange(code)
                        expanded = false
                    }
                )
            }
        }
    }

}
