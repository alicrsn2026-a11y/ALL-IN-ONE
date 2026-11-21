package com.allinone.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allinone.app.data.AppContainer
import com.allinone.app.data.security.EncryptedPrefs
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(container: AppContainer) {
    val settings = container.settingsRepository
    val prefs = container.encryptedPrefs
    val scope = rememberCoroutineScope()

    val themeMode by settings.themeMode.collectAsStateWithLifecycle()
    val space2d by settings.space2dMode.collectAsStateWithLifecycle()
    val aiProvider by settings.aiProvider.collectAsStateWithLifecycle()
    val backupAuto by settings.backupAuto.collectAsStateWithLifecycle()

    var apiKey by remember { mutableStateOf(prefs.getString(EncryptedPrefs.KEY_API_KEY)) }
    var smtpHost by remember { mutableStateOf(prefs.getString(EncryptedPrefs.KEY_EMAIL_SMTP_HOST)) }
    var smtpUser by remember { mutableStateOf(prefs.getString(EncryptedPrefs.KEY_EMAIL_USER)) }
    var smtpPass by remember { mutableStateOf(prefs.getString(EncryptedPrefs.KEY_EMAIL_PASSWORD)) }
    var appLock by remember { mutableStateOf(prefs.getBoolean(EncryptedPrefs.KEY_APP_LOCK)) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Setări", style = MaterialTheme.typography.titleLarge)

        Text("Personalizare", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Spațiu 2D (grilă)", modifier = Modifier.weight(1f))
            Switch(
                checked = space2d,
                onCheckedChange = { scope.launch { settings.setSpace2dMode(it) } }
            )
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Temă întunecată", modifier = Modifier.weight(1f))
            Switch(
                checked = themeMode == 2,
                onCheckedChange = { on ->
                    scope.launch { settings.setThemeMode(if (on) 2 else 0) }
                }
            )
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Backup automat", modifier = Modifier.weight(1f))
            Switch(
                checked = backupAuto,
                onCheckedChange = { scope.launch { settings.setBackupAuto(it) } }
            )
        }

        HorizontalDivider()

        Text("Securitate", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Blocare aplicație", modifier = Modifier.weight(1f))
            Switch(
                checked = appLock,
                onCheckedChange = {
                    appLock = it
                    prefs.putBoolean(EncryptedPrefs.KEY_APP_LOCK, it)
                }
            )
        }

        HorizontalDivider()

        Text("Furnizor AI", style = MaterialTheme.typography.titleMedium)
        Text("Cheie API (stocată criptat în Keystore)", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            placeholder = { Text("sk-…") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        TextButton(onClick = {
            prefs.putString(EncryptedPrefs.KEY_API_KEY, apiKey.trim())
        }) { Text("Salvează cheia") }

        HorizontalDivider()

        Text("Email (SMTP)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = smtpHost, onValueChange = { smtpHost = it }, label = { Text("Server SMTP") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = smtpUser, onValueChange = { smtpUser = it }, label = { Text("Utilizator") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = smtpPass, onValueChange = { smtpPass = it }, label = { Text("Parolă") }, modifier = Modifier.fillMaxWidth())
        TextButton(onClick = {
            prefs.putString(EncryptedPrefs.KEY_EMAIL_SMTP_HOST, smtpHost.trim())
            prefs.putString(EncryptedPrefs.KEY_EMAIL_USER, smtpUser.trim())
            prefs.putString(EncryptedPrefs.KEY_EMAIL_PASSWORD, smtpPass)
        }) { Text("Salvează config SMTP") }

        HorizontalDivider()

        Text("Despre", style = MaterialTheme.typography.titleMedium)
        Text("ALL-IN-ONE v1.0.0", style = MaterialTheme.typography.bodyMedium)
        Text("Pachet: com.allinone.app", style = MaterialTheme.typography.bodySmall)
    }
}
