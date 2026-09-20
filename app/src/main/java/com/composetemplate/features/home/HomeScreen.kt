package com.composetemplate.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.composetemplate.features.theme.ThemeViewModel
import java.text.NumberFormat
import java.util.Locale

private fun imageUrl(path: String?): String? {
    if (path.isNullOrEmpty()) return null
    if (path.startsWith("http")) return path
    return BuildConfig.API_URL.trimEnd('/') + path
}

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onProductClick: (Int) -> Unit = {},
    onCartClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    wishlistViewModel: com.composetemplate.features.wishlist.WishlistViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val allProducts = viewModel.products.collectAsStateLifecycleAware().value
    val dynamicCategories = viewModel.categories.collectAsStateLifecycleAware().value
    val selectedCategory = viewModel.selectedCategory.collectAsStateLifecycleAware().value
    val searchQuery = viewModel.searchQuery.collectAsStateLifecycleAware().value
    val wishlistIds = wishlistViewModel.items.collectAsStateLifecycleAware().value.map { it.id }.toSet()
    val darkModePref = themeViewModel.darkMode.collectAsStateLifecycleAware().value

    val isDark = darkModePref ?: isSystemInDarkTheme()

    val categories = remember(dynamicCategories) {
        val dynamicList = dynamicCategories.map { it.name to it.name }
        listOf("all" to "Semua") + dynamicList + listOf("BARU" to "Baru", "DISKON" to "Diskon")
    }

    val filtered = remember(allProducts, selectedCategory, searchQuery) {
        var result = allProducts
        if (searchQuery.isNotBlank()) {
            result = result.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
        when (selectedCategory) {
            "all" -> result
            "BARU" -> result.filter { it.badge == "BARU" }
            "DISKON" -> result.filter { it.badge == "DISKON" }
            else -> result.filter { it.category == selectedCategory }
        }
    }

    HomeScreen(
        products = filtered,
        categories = categories,
        selectedCategory = selectedCategory,
        searchQuery = searchQuery,
        wishlistIds = wishlistIds,
        isDark = isDark,
        onCategorySelected = viewModel::onCategorySelected,
        onSearchChanged = viewModel::onSearchChanged,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onWishlistClick = onWishlistClick,
        onToggleWishlist = { product -> wishlistViewModel.toggle(product) },
        onToggleDarkMode = { themeViewModel.toggle(isDark) },
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    products: List<Product>,
    categories: List<Pair<String, String>>,
    selectedCategory: String,
    searchQuery: String,
    wishlistIds: Set<Int>,
    isDark: Boolean,
    onCategorySelected: (String) -> Unit,
    onSearchChanged: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
            IconButton(onClick = onToggleDarkMode) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Dark mode"
                )
            }
            IconButton(onClick = onWishlistClick) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist")
            }
            IconButton(onClick = onCartClick) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang")
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Cari sepatu...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (key, label) ->
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
                if (searchQuery.isNotBlank()) {
                    Text("Tidak ada hasil untuk \"$searchQuery\"")
                } else {
                    CircularProgressIndicator()
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlistIds.contains(product.id),
                        onClick = { onProductClick(product.id) },
                        onToggleWishlist = { onToggleWishlist(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    isWishlisted: Boolean,
    onClick: () -> Unit,
    onToggleWishlist: () -> Unit,
) {
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
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (product.badge != null) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        color = if (product.badge == "DISKON") MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = product.badge,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatRupiah(product.price),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (product.oldPrice != null && product.oldPrice > product.price) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = formatRupiah(product.oldPrice),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}

private fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}
