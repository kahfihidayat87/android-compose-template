package com.composetemplate.features.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
fun WishlistRoute(
    onProductClick: (Int) -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val items = viewModel.items.collectAsStateLifecycleAware().value
    WishlistScreen(items = items, onProductClick = onProductClick)
}

@Composable
fun WishlistScreen(
    items: List<Product>,
    onProductClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Wishlist",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Sepatu favorit kamu",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Wishlist kosong", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tap ikon hati di produk untuk menyimpan",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(items, key = { it.id }) { product ->
                    WishlistCard(product = product, onClick = { onProductClick(product.id) })
                }
            }
        }
    }
}

@Composable
private fun WishlistCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
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
                    Text("S", fontSize = 64.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(
                formatRupiah(product.price),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
