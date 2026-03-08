package pt.isec.ipc.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ConfigurationScreen() {
    // LocalContext provides the Android Context needed for SharedPreferences
    val context = LocalContext.current 
    val prefsHelper = remember { PrefsHelper(context) }

    // Load initial values from SharedPreferences
    var username by remember { mutableStateOf(prefsHelper.getUsername()) }
    var password by remember { mutableStateOf(prefsHelper.getPassword()) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation() // Masks the password input
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { 
            prefsHelper.saveCredentials(username, password) 
        }) {
            Text("Save Credentials")
        }
    }
}
