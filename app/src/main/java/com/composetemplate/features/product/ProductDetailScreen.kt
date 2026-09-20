package com.composetemplate.features.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.composetemplate.BuildConfig
import com.composetemplate.arch.extensions.collectAsStateLifecycleAware
import com.composetemplate.core.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

private fun imageUrl(path: String?): String? {
    if (path.isNullOrEmpty()) return null
    if (path.startsWith("http")) return path
    return BuildConfig.API_URL.trimEnd('/') + path
}

private fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}

@Composable
internal fun ProductDetailRoute(
    productId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val id = productId.toIntOrNull()
    val product = viewModel.product.collectAsStateLifecycleAware().value
    val selectedSize = viewModel.selectedSize.collectAsStateLifecycleAware().value
    val quantity = viewModel.quantity.collectAsStateLifecycleAware().value

    LaunchedEffect(id) {
        if (id != null) viewModel.load(id)
    }

    if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        ProductDetailScreen(
            product = product,
            selectedSize = selectedSize,
            quantity = quantity,
            onBackClick = onBackClick,
            onSizeSelected = viewModel::onSizeSelected,
            onQuantityChanged = viewModel::onQuantityChanged,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    selectedSize: Int?,
    quantity: Int,
    onBackClick: () -> Unit,
    onSizeSelected: (Int) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val url = imageUrl(product.img)
                if (url != null) {
                    AsyncImage(
                        model = url,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("S", fontSize = 120.sp)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = product.category,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatRupiah(product.price),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (product.oldPrice != null && product.oldPrice > product.price) {
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = formatRupiah(product.oldPrice),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Pilih Ukuran (EU)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.sizes.forEach { size ->
                        FilterChip(
                            selected = selectedSize == size,
                            onClick = { onSizeSelected(size) },
                            label = { Text(size.toString()) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Jumlah", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onQuantityChanged(quantity - 1) },
                        enabled = quantity > 1
                    ) { Text("-", fontSize = 20.sp) }
                    Text(
                        text = quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    OutlinedButton(
                        onClick = { onQuantityChanged(quantity + 1) },
                        enabled = quantity < 10
                    ) { Text("+", fontSize = 20.sp) }
                }

                Spacer(Modifier.height(20.dp))

                Text("Deskripsi", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = product.desc.ifEmpty { "Belum ada deskripsi." },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(100.dp))
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = formatRupiah(product.price * quantity),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Tambah ke Keranjang", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
