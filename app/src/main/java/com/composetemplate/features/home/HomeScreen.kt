package com.composetemplate.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

private val CATEGORIES = listOf(
    "all" to "Semua",
    "slipon" to "Slip-On",
    "sneakers" to "Sneakers",
    "new" to "Terbaru",
    "best" to "Best Seller"
)

private fun imageUrl(path: String?): String? {
    if (path.isNullOrEmpty()) return null
    return BuildConfig.API_URL.trimEnd('/') + path
}

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val allProducts = viewModel.products.collectAsStateLifecycleAware().value
    val selectedCategory = viewModel.selectedCategory.collectAsStateLifecycleAware().value

    val filtered = remember(allProducts, selectedCategory) {
        when (selectedCategory) {
            "all" -> allProducts
            "new" -> allProducts.filter { it.isNew }
            "best" -> allProducts.filter { it.isBestSeller }
            else -> allProducts.filter { it.category == selectedCategory }
        }
    }

    HomeScreen(
        products = filtered,
        selectedCategory = selectedCategory,
        onCategorySelected = viewModel::onCategorySelected,
        onProductClick = onProductClick,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    products: List<Product>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Sepatumu",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sepatu lokal Muhammadiyah",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CATEGORIES) { (key, label) ->
                FilterChip(
                    selected = selectedCategory == key,
                    onClick = { onCategorySelected(key) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                val url = imageUrl(product.image)
                if (url != null) {
                    AsyncImage(
                        model = url,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(product.emoji ?: "S", fontSize = 64.sp)
                }
                if (product.isNew) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "BARU",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatRupiah(product.price),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("★ ${product.rating}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Text("(${product.reviews})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}