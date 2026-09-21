package com.composetemplate.features.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import com.composetemplate.core.domain.model.ShippingRate
import java.text.NumberFormat
import java.util.Locale

private fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutRoute(
    onBackClick: () -> Unit,
    onPaymentUrl: (String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val form = viewModel.form.collectAsStateLifecycleAware().value
    val rates = viewModel.rates.collectAsStateLifecycleAware().value
    val selectedRate = viewModel.selectedRate.collectAsStateLifecycleAware().value
    val methods = viewModel.paymentMethods.collectAsStateLifecycleAware().value
    val selectedMethod = viewModel.selectedMethod.collectAsStateLifecycleAware().value
    val loading = viewModel.loading.collectAsStateLifecycleAware().value
    val error = viewModel.error.collectAsStateLifecycleAware().value
    val paymentUrl = viewModel.paymentUrl.collectAsStateLifecycleAware().value
    val items = viewModel.items.collectAsStateLifecycleAware().value
    val couponInput = viewModel.couponInput.collectAsStateLifecycleAware().value
    val appliedCoupon = viewModel.appliedCoupon.collectAsStateLifecycleAware().value
    val discount = viewModel.discount.collectAsStateLifecycleAware().value

    val subtotal = items.sumOf { it.subtotal }
    val shipping = selectedRate?.price ?: 0
    val total = (subtotal - discount).coerceAtLeast(0) + shipping

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (!error.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(paymentUrl) {
        if (!paymentUrl.isNullOrEmpty()) {
            onPaymentUrl(paymentUrl)
            viewModel.consumePaymentUrl()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = formatRupiah(total),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = { viewModel.onPrimaryAction() },
                        enabled = !loading && items.isNotEmpty() && viewModel.isPrimaryButtonEnabled(),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(
                            text = if (loading) "Memproses..." else viewModel.getPrimaryButtonLabel(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Alamat Pengiriman", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = form.name, onValueChange = viewModel::onName,
                label = { Text("Nama Penerima") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = form.phone, onValueChange = viewModel::onPhone,
                label = { Text("No. WhatsApp") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = form.address, onValueChange = viewModel::onAddress,
                label = { Text("Alamat Lengkap") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = form.postalCode, onValueChange = viewModel::onPostalCode,
                label = { Text("Kode Pos (5 digit)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Spacer(Modifier.height(20.dp))

            // ============ KUPON ============
            Text("Kode Kupon", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            if (appliedCoupon == null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = couponInput,
                        onValueChange = viewModel::onCouponInput,
                        placeholder = { Text("Mis: LEBARAN20") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.applyCoupon() },
                        enabled = !loading && couponInput.isNotBlank(),
                        modifier = Modifier.height(56.dp)
                    ) { Text("Pakai") }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🎟️ $appliedCoupon", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Hemat ${formatRupiah(discount)}", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                        IconButton(onClick = { viewModel.removeCoupon() }) {
                            Icon(Icons.Default.Close, contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (rates.isNotEmpty()) {
                Text("Pilih Kurir", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                rates.forEach { rate ->
                    RateRow(
                        rate = rate,
                        selected = selectedRate?.let { it.courierCode == rate.courierCode && it.serviceCode == rate.serviceCode } == true,
                        onClick = { viewModel.selectRate(rate) }
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            if (methods.isNotEmpty()) {
                Text("Metode Pembayaran", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                methods.forEach { m ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .selectable(selected = selectedMethod == m.effectiveCode,
                                onClick = { viewModel.selectMethod(m.effectiveCode) })
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedMethod == m.effectiveCode,
                            onClick = { viewModel.selectMethod(m.effectiveCode) })
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(m.effectiveName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            if (m.totalFee > 0) {
                                Text("Biaya admin: ${formatRupiah(m.totalFee)}", fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SummaryRow("Subtotal", formatRupiah(subtotal))
                    SummaryRow("Ongkir", if (shipping > 0) formatRupiah(shipping) else "-")
                    if (discount > 0) {
                        SummaryRow("Diskon", "- ${formatRupiah(discount)}", highlight = true)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("Total", formatRupiah(total), bold = true)
                }
            }

            Spacer(Modifier.height(80.dp))

            if (loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun RateRow(rate: ShippingRate, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("${rate.courierName} - ${rate.serviceName}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Estimasi ${rate.estimatedDays} hari", fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(formatRupiah(rate.price), fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (bold) 14.sp else 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = if (bold) 14.sp else 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold || highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
