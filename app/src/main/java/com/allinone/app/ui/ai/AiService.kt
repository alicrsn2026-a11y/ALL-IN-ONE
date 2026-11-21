package com.allinone.app.ui.ai

import com.allinone.app.data.security.EncryptedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Minimal OpenAI-compatible chat client. Reads the API key from EncryptedPrefs.
 * When no key is configured it falls back to a local echo so the module stays
 * usable offline. Never logs or exposes the key.
 */
class AiService(private val prefs: EncryptedPrefs) {

    suspend fun sendMessage(provider: String, model: String, history: List<Pair<String, String>>): String =
        withContext(Dispatchers.IO) {
            val apiKey = prefs.getString(EncryptedPrefs.KEY_API_KEY)
            if (apiKey.isBlank()) {
                return@withContext localEcho(history.lastOrNull()?.second ?: "")
            }
            try {
                callOpenAi(apiKey, model.ifBlank { "gpt-4o-mini" }, history)
            } catch (e: Exception) {
                "Eroare la conectarea la furnizor: ${e.message}"
            }
        }

    private fun callOpenAi(apiKey: String, model: String, history: List<Pair<String, String>>): String {
        val base = prefs.getString(EncryptedPrefs.KEY_API_BASE_URL)
            .ifBlank { "https://api.openai.com/v1" }
        val url = URL("$base/chat/completions")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.connectTimeout = 20000
        conn.readTimeout = 30000
        conn.doOutput = true

        val messages = JSONArray()
        history.forEach { (role, content) ->
            messages.put(JSONObject().put("role", role).put("content", content))
        }
        val body = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("temperature", 0.7)

        OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = stream.bufferedReader().use { it.readText() }
        if (code !in 200..299) {
            return "Eroare ${code}: $text"
        }
        val json = JSONObject(text)
        return json.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
    }

    private fun localEcho(userText: String): String {
        return "Am primit mesajul tău. (Mod demo — configurează o cheie API în Setări pentru răspunsuri reale.)\n\n„$userText”"
    }
}
