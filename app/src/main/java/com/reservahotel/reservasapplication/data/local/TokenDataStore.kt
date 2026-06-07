package com.reservahotel.reservasapplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences>
    by preferencesDataStore(name = "hotel_session")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        val KEY_ACCESS   = stringPreferencesKey("access_token")
        val KEY_REFRESH  = stringPreferencesKey("refresh_token")
        val KEY_USER_ID  = intPreferencesKey("user_id")
        val KEY_CLIENTE_ID = intPreferencesKey("cliente_id")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_EMAIL    = stringPreferencesKey("email")
        val KEY_ROL      = stringPreferencesKey("rol")
        val KEY_IS_STAFF = booleanPreferencesKey("is_staff")
    }

    val accessToken: Flow<String?>  = context.dataStore.data.map { it[KEY_ACCESS]  }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[KEY_REFRESH] }

    suspend fun getAccessToken():  String? = accessToken.first()
    suspend fun getRefreshToken(): String? = refreshToken.first()

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
        !it[KEY_ACCESS].isNullOrBlank()
    }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS]  = access
            prefs[KEY_REFRESH] = refresh
        }
    }

    suspend fun saveAccessToken(access: String) {
        context.dataStore.edit { it[KEY_ACCESS] = access }
    }

    suspend fun saveUser(id: Int, clienteId: Int, username: String, email: String, rol: String, isStaff: Boolean = false) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID]    = id
            prefs[KEY_CLIENTE_ID] = clienteId
            prefs[KEY_USERNAME]   = username
            prefs[KEY_EMAIL]      = email
            prefs[KEY_ROL]        = rol
            prefs[KEY_IS_STAFF]   = isStaff
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    data class UserSnapshot(
        val id:        Int,
        val clienteId: Int,
        val username:  String,
        val email:     String,
        val rol:       String,
        val isStaff:   Boolean = false,
    )

    val userSnapshot: Flow<UserSnapshot?> = context.dataStore.data.map { prefs ->
        val id = prefs[KEY_USER_ID] ?: return@map null
        val clienteId = prefs[KEY_CLIENTE_ID] ?: 0
        val rol = prefs[KEY_ROL]?.trim()?.lowercase() ?: "cliente"
        
        UserSnapshot(
            id        = id,
            clienteId = clienteId,
            username  = prefs[KEY_USERNAME] ?: "",
            email     = prefs[KEY_EMAIL]    ?: "",
            rol       = rol,
            isStaff   = rol == "administrador",
        )
    }
}
