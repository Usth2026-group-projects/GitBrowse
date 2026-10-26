package dev.app.gitbrowse.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder

// One result that our existing cards can display.
data class SearchResult(
    val id: Long,
    val title: String,
    val description: String
)

class GitHubApiException(message: String) : Exception(message)

object GitHubApi {
    suspend fun search(
        query: String,
        searchPeople: Boolean
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val endpoint = if (searchPeople) "users" else "repositories"

        val url = URI.create(
            "https://api.github.com/search/$endpoint" +
                    "?q=$encodedQuery&per_page=30"
        ).toURL()

        val connection = url.openConnection() as HttpURLConnection

        try {
            currentCoroutineContext().ensureActive()

            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            connection.setRequestProperty(
                "Accept",
                "application/vnd.github+json"
            )
            connection.setRequestProperty(
                "X-GitHub-Api-Version",
                "2022-11-28"
            )
            connection.setRequestProperty(
                "User-Agent",
                "GitBrowse-Android"
            )

            val status = connection.responseCode

            if (status !in 200..299) {
                val message = when {
                    status == 403 ->
                        "GitHub declined the request. Its public API limit may have been reached."

                    status == 429 ->
                        "GitHub's rate limit was reached. Wait before trying again."

                    status == 422 ->
                        "GitHub could not understand this search."

                    status >= 500 ->
                        "GitHub is having trouble. Try again shortly."

                    else ->
                        "Search failed (HTTP $status)."
                }

                throw GitHubApiException(message)
            }

            val body = connection.inputStream
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            currentCoroutineContext().ensureActive()

            val items = JSONObject(body).getJSONArray("items")

            List(items.length()) { index ->
                val item = items.getJSONObject(index)

                SearchResult(
                    id = item.getLong("id"),
                    title = if (searchPeople) {
                        item.getString("login")
                    } else {
                        item.getString("full_name")
                    },
                    description = if (searchPeople) {
                        item.optString("type", "GitHub profile")
                    } else if (item.isNull("description")) {
                        "No description available."
                    } else {
                        item.getString("description")
                    }
                )
            }
        } finally {
            connection.disconnect()
        }
    }
}