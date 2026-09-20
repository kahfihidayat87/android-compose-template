package com.composetemplate.features.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composetemplate.arch.extensions.collectAsStateLifecycleAware
import java.text.NumberFormat
import java.util.Locale

private fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailRoute(
    orderId: Int,
    onBackClick: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val order = viewModel.order.collectAsStateLifecycleAware().value
    val loading = viewModel.loading.collectAsStateLifecycleAware().value
    val error = viewModel.error.collectAsStateLifecycleAware().value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(orderId) {
        viewModel.load(orderId)
    }

    LaunchedEffect(error) {
        if (!error.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            loading && order == null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            order == null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Pesanan tidak ditemukan") }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("No. Pesanan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(order.orderNumber, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        Text("Status", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(order.statusLabel, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Alamat Pengiriman", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(order.shippingAddress, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            order.shippingPhone,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Rincian Pembayaran", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryRow("Subtotal", formatRupiah(order.subtotal))
                        SummaryRow("Ongkir", formatRupiah(order.shippingCost))
                        if (order.discountAmount > 0) {
                            SummaryRow("Diskon", "- ${formatRupiah(order.discountAmount)}")
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                formatRupiah(order.total),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (!order.resi.isNullOrEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text("Nomor Resi", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Text(
                            order.resi,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp)
        Text(value, fontSize = 13.sp)
    }
}
