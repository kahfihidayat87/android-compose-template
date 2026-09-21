package com.composetemplate.features.wakaf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.composetemplate.core.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakafScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF10B981), Color(0xFF047857))
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gerakan Wakaf Sepatu",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Salurkan sepatu untuk santri & dhuafa di panti asuhan Muhammadiyah.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }
                Text(text = "🤝", fontSize = 72.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Cara Berwakaf",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        val steps = listOf(
            "Pilih sepatu dari koleksi A-DHL",
            "Saat checkout, pilih opsi \"Wakafkan Sepatu Ini\"",
            "Isi data panti asuhan tujuan (opsional)",
            "Kami akan kirim langsung ke panti yang kamu pilih",
            "Kamu dapat notifikasi saat sepatu sudah diterima"
        )

        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = step,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"Sebaik-baik manusia adalah yang paling bermanfaat bagi manusia lainnya.\"",
                    fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "— HR. Ahmad",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                WhatsAppHelper.chatAdmin(
                    context,
                    "Halo A-DHL, saya mau tanya tentang program Wakaf Sepatu."
                )
            },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Tanya via WhatsApp", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
    }
}
