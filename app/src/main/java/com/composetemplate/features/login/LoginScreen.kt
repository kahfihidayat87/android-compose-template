package com.composetemplate.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composetemplate.arch.extensions.collectAsStateLifecycleAware
import com.composetemplate.core.ui.AppBackground
import com.composetemplate.core.ui.AppButton
import com.composetemplate.core.ui.InputTextField
import com.composetemplate.core.ui.InputTextFieldType


@Composable
internal fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToHome: () -> Unit = {},
    navigateToRegister: () -> Unit = {}
) {
    val loginUiInfo = viewModel.loginUiInfo.collectAsStateLifecycleAware().value

    LaunchedEffect(Unit) {
        viewModel.loginResultFlow.collect {
            navigateToHome()
        }
    }

    LoginScreen(
        loginUiInfo = loginUiInfo,
        onUserNameChanged = viewModel::onUserNameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        login = viewModel::login,
        navigateToHome = navigateToHome,
        navigateToRegister = navigateToRegister
    )
}

@Composable
fun LoginScreen(
    loginUiInfo: LoginUiInfo,
    onUserNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    login: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sepatumu",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Masuk untuk mulai belanja",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        InputTextField(
            text = loginUiInfo.userName,
            label = "Email",
            type = InputTextFieldType.Outlined
        ) {
            onUserNameChanged(it)
        }
        Spacer(modifier = Modifier.height(10.dp))
        InputTextField(
            text = loginUiInfo.password,
            label = "Password",
            type = InputTextFieldType.Outlined
        ) {
            onPasswordChanged(it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        AppButton(text = "Masuk") {
            login()
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = navigateToRegister) {
            Text("Belum punya akun? Daftar di sini")
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    AppBackground {
        LoginScreen(LoginUiInfo("", ""), {}, {}, {}, {}, {})
    }
}
