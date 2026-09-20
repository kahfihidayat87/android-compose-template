package com.composetemplate.features.sizeguide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SIZE_CHART = listOf(
    36 to 21.0,
    37 to 22.0,
    38 to 23.0,
    39 to 24.0,
    40 to 25.0,
    41 to 26.0,
    42 to 27.0,
    43 to 28.0,
    44 to 29.0,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeGuideScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panduan Ukuran") },
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
                .padding(16.dp)
        ) {
            Text(
                text = "Semua produk A-DHL tersedia ukuran EU 36–44. Ukur panjang telapak kakimu (dari tumit ke ujung jari terpanjang) dalam cm, lalu cocokkan dengan tabel berikut.",
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("Ukuran (EU)", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Panjang Kaki (cm)", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }
                    Divider()
                    SIZE_CHART.forEach { (eu, cm) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$eu", fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text("${cm} cm", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Cara Mengukur Kaki", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            val steps = listOf(
                "Berdiri di atas selembar kertas dengan berat badan bertumpu normal.",
                "Tandai ujung tumit dan ujung jari kaki terpanjang.",
                "Ukur jarak kedua tanda dengan penggaris (dalam cm).",
                "Ukur kedua kaki — pakai hasil ukuran yang lebih besar.",
                "Ukur di sore/malam hari, saat kaki sedikit lebih besar dari pagi hari."
            )
            steps.forEachIndexed { index, step ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("${index + 1}.", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text(step, fontSize = 13.sp, lineHeight = 20.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tips Tambahan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Semua model A-DHL memakai material kanvas breathable yang sedikit melar. Kalau ukuran kakimu pas di antara dua ukuran, kami sarankan pilih yang lebih besar untuk kenyamanan ekstra.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
