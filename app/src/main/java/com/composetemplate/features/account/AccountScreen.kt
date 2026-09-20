package com.composetemplate.features.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composetemplate.arch.extensions.collectAsStateLifecycleAware
import com.composetemplate.core.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountRoute(
    onLoggedOut: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onOrdersClick: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val user = viewModel.user.collectAsStateLifecycleAware().value
    val loggedOut = viewModel.loggedOut.collectAsStateLifecycleAware().value
    val context = LocalContext.current

    LaunchedEffect(loggedOut) {
        if (loggedOut) onLoggedOut()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.name ?: "Pengguna",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = user?.email ?: "-",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    if (!user?.phone.isNullOrEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = user!!.phone,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Menu list
        MenuItem(
            icon = Icons.Default.ShoppingCart,
            title = "Pesanan Saya",
            onClick = onOrdersClick
        )
        MenuItem(
            icon = Icons.Default.Favorite,
            title = "Wishlist",
            onClick = onWishlistClick
        )
        MenuItem(
            icon = Icons.Default.ShoppingCart,
            title = "Keranjang",
            onClick = onCartClick
        )
        MenuItem(
            icon = Icons.Default.Star,
            title = "Hubungi Admin",
            onClick = {
                WhatsAppHelper.chatAdmin(context)
            }
        )

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("↩", fontSize = 20.sp, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            Text("Keluar", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}
