package com.composetemplate.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composetemplate.core.domain.model.OnboardingPage
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    pages: List<OnboardingPage>,
    onFinish: () -> Unit
) {
    if (pages.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val currentPage = listState.firstVisibleItemIndex
    val isLastPage = currentPage == pages.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinish) {
                Text("Lewati", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(pages.size) { index ->
                val page = pages[index]
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .fillParentMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = page.emoji, fontSize = 96.sp)
                        Spacer(Modifier.height(32.dp))
                        Text(
                            text = page.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = page.description,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { index ->
                val selected = currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Button(
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        scope.launch {
                            listState.animateScrollToItem(currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isLastPage) "Mulai Belanja" else "Lanjut",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
