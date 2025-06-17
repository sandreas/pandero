package com.pilabor.pandero.data

import android.content.Context
import org.koin.core.annotation.Single
import androidx.core.content.edit

@Single
class MediaSession(private val context: Context) {
    // todo: use secure storage
    // https://github.com/daabr/secure-secret-storage/blob/master/app/src/main/java/wtf/daabr/securesecretstorage/MainActivity.kt

    private val sharedPreferences =
        context.getSharedPreferences("pandero_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit {
            putString("token", token)
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun saveUserName(userName: String) {
        sharedPreferences.edit {
            putString("user_name", userName)
        }
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("user_name", null)
    }

    fun clearSession() {
        sharedPreferences.edit() {
            clear()
        }
    }

}