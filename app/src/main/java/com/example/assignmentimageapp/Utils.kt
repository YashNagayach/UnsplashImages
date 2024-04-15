package com.example.assignmentimageapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

object Utils {
     fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

    @Composable
    fun showNetworkAlertDialog() {
        val dismissDialog = remember { mutableStateOf(true) }

        if (dismissDialog.value) {
            AlertDialog(
                onDismissRequest = { dismissDialog.value = false },
                title = { Text(text = "No Internet Connection") },
                text = { Text(text = "Please check your internet connection and try again.") },
                confirmButton = {
                    Button(
                        onClick = { dismissDialog.value = false }
                    ) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }
    @Composable
    fun ShowAlertDialog(
        title: String,
        message: String,
    ) {
        val dismissDialog = remember { mutableStateOf(true) }
        if(dismissDialog.value){
            AlertDialog(
                onDismissRequest = { dismissDialog.value=false },
                title = { Text(title) },
                text = { Text(message) },
                confirmButton = {
                    Button(
                        onClick = { dismissDialog.value=false }
                    ) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }
}
