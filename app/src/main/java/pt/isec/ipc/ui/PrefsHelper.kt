package pt.isec.ipc.ui

import android.content.Context
import android.content.SharedPreferences

class PrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveCredentials(user: String, pass: String) {
        prefs.edit()
            .putString("username", user)
            .putString("password", pass)
            .apply() // .apply() writes asynchronously, .commit() writes synchronously
    }

    fun getUsername(): String = prefs.getString("username", "") ?: ""
    fun getPassword(): String = prefs.getString("password", "") ?: ""
}
