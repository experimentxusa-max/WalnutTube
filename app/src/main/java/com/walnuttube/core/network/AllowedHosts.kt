package com.walnuttube.core.network

import java.net.URL

/**
 * Shared YouTube domain allowlist for SSRF protection.
 * Used by both [NewPipeDownloader] for initial URL validation and
 * [RedirectValidationInterceptor] for redirect target validation.
 */
object AllowedHosts {

    private val hosts = setOf(
        "www.youtube.com",
        "youtube.com",
        "m.youtube.com",
        "music.youtube.com",
        "i.ytimg.com",
        "img.youtube.com",
        "googlevideo.com",
        "*.googlevideo.com",
        "youtubei.googleapis.com",
        "www.youtube-nocookie.com",
        "youtube-nocookie.com"
    )

    /**
     * Checks if the given host is in the YouTube allowlist.
     * Supports wildcard patterns like "*.googlevideo.com".
     */
    fun isAllowed(host: String): Boolean {
        val lowerHost = host.lowercase()
        return hosts.any { pattern ->
            if (pattern.startsWith("*.")) {
                val suffix = pattern.substring(2)
                lowerHost == suffix || lowerHost.endsWith(".$suffix")
            } else {
                lowerHost == pattern
            }
        }
    }

    /**
     * Validates a full URL string against the allowlist.
     * Throws SecurityException if the host is not allowed.
     */
    fun validateUrl(urlString: String) {
        val url = URL(urlString)
        val host = url.host
        if (!isAllowed(host)) {
            throw SecurityException("Request to non-YouTube host blocked: $host")
        }
    }

    /**
     * Validates a redirect Location header against the allowlist.
     * Resolves relative URLs against the original request URL.
     * Throws SecurityException if the target host is not allowed.
     */
    fun validateRedirect(location: String, originalUrl: String) {
        val original = URL(originalUrl)
        // Resolve relative Location against original request URL
        val redirectUrl = URL(original, location)
        val host = redirectUrl.host
        if (!isAllowed(host)) {
            throw SecurityException("Redirect to non-YouTube host blocked: $host")
        }
    }
}