package com.example.firebase_auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) { navController.navigate("main") } }
        composable("register") { RegisterScreen { navController.navigate("login") } }
        composable("main") { MainScreen() }
    }
}

@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onLoginSuccess() }
                        .addOnFailureListener { errorMessage = it.message }
                }
            ) {
                Text("Login")
            }

            Button(
                onClick = {
                    navController.navigate("register")
                }
            ) {
                Text("Register")
            }

            errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error)}
        }
    }
}

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onRegisterSuccess() }
                        .addOnFailureListener { errorMessage = it.message }
                }
            ) {
                Text("Register")
            }

            errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error)}
        }
    }
}

@Composable
fun MainScreen() {
    val db = FirebaseFirestore.getInstance()
    var inputText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter data") }
            )
            Button(
                onClick = {
                    val data = hashMapOf("message" to inputText)
                    db.collection("messages")
                        .add(data)
                        .addOnSuccessListener { message = "Data added successfully" }
                        .addOnFailureListener { e -> message = "Error: ${e.message}" }
                }
            ) {
                Text("Add to Firestore")
            }

            message?.let { Text(it) }
        }
    }
}