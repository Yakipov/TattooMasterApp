package com.ayforge.tattoomasterapp.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ayforge.tattoomasterapp.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val isSignedIn by viewModel.isSignedIn.collectAsState()

    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            navController.navigate("home") {
                popUpTo("signin") { inclusive = true }
            }
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.signIn(email, password)
            },
            enabled = !uiState.isLoading
        ) {
            Text(stringResource(R.string.sign_in))
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}
