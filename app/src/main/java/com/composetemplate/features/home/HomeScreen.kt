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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        dynamicCategories.map { it.name }
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
        allProducts = allProducts,
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
    allProducts: List<Product>,
    categories: List<String>,
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
    val bestSellers = remember(allProducts) {
        allProducts.filter { it.badge == "DISKON" || it.oldPrice != null }.take(6)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // ============ HEADER ============
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sepatumu A-DHL",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Sepatunya warga Muhammadiyah",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleDarkMode) {
                Text(text = if (isDark) "☀️" else "🌙", fontSize = 20.sp)
            }
            IconButton(onClick = onWishlistClick) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist")
            }
            IconButton(onClick = onCartClick) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ============ SEARCH BAR ============
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari sepatu...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // ============ HERO BANNER ============
            if (searchQuery.isBlank() && selectedCategory == "all") {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    HeroBannerCarousel()
                }

                // ============ KATEGORI ============
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Column {
                        SectionHeader(title = "Kategori", subtitle = "Pilih sesuai gayamu")
                        LazyRow(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val allCats = listOf("all") + categories + listOf("BARU", "DISKON")
                            items(allCats) { cat ->
                                val label = when (cat) {
                                    "all" -> "Semua"
                                    "BARU" -> "Baru"
                                    "DISKON" -> "Diskon"
                                    else -> cat
                                }
                                CategoryPill(
                                    label = label,
                                    selected = selectedCategory == cat,
                                    onClick = { onCategorySelected(cat) }
                                )
                            }
                        }
                    }
                }

                // ============ TERLARIS ============
                if (bestSellers.isNotEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        SectionHeader(title = "Paling Diminati", subtitle = "Produk favorit pelanggan")
                    }

                    items(bestSellers, key = { "best-${it.id}" }, span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = wishlistIds.contains(product.id),
                            onClick = { onProductClick(product.id) },
                            onToggleWishlist = { onToggleWishlist(product) }
                        )
                    }
                }

                // ============ SEMUA PRODUK ============
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    SectionHeader(title = "Semua Koleksi", subtitle = "Sepatu lokal berkualitas")
                }
            }

            // Produk (difilter atau semua)
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    isWishlisted = wishlistIds.contains(product.id),
                    onClick = { onProductClick(product.id) },
                    onToggleWishlist = { onToggleWishlist(product) }
                )
            }

            // Empty state
            if (products.isEmpty()) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (searchQuery.isNotBlank()) "Tidak ada hasil untuk \"$searchQuery\""
                                else "Belum ada produk",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HeroBannerCarousel() {
    val banners = remember {
        listOf(
            HeroBanner(
                title = "Koleksi Terbaru",
                subtitle = "Sepatu kanvas breathable dari Gunungkidul",
                emoji = "👟",
                gradient = listOf(Color(0xFF0066FF), Color(0xFF003A99))
            ),
            HeroBanner(
                title = "Mulai Rp149.000",
                subtitle = "Kualitas premium, harga merakyat",
                emoji = "✨",
                gradient = listOf(Color(0xFFFF6A2C), Color(0xFFCC3D00))
            ),
            HeroBanner(
                title = "Dukung Ekonomi Umat",
                subtitle = "Produk lokal PDM Gunungkidul",
                emoji = "🤝",
                gradient = listOf(Color(0xFF10B981), Color(0xFF047857))
            ),
        )
    }
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) { pageIndex ->
            val banner = banners[pageIndex]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(banner.gradient))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = banner.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = banner.subtitle,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                    Text(text = banner.emoji, fontSize = 64.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
    }
}

data class HeroBanner(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val gradient: List<Color>,
)

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
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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
                // Wishlist button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    IconButton(
                        onClick = onToggleWishlist,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            modifier = Modifier.size(18.dp),
                            tint = if (isWishlisted) MaterialTheme.colorScheme.error
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Badge
                if (product.badge != null) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        color = if (product.badge == "DISKON") MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = product.badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatRupiah(product.price),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (product.oldPrice != null && product.oldPrice > product.price) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = formatRupiah(product.oldPrice),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
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
