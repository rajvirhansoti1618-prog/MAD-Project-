package com.example.lexidesk.utils

import java.util.Locale

object FileUtils {

    /**
     * Formats raw byte count into human-readable representation (e.g. 0 B, 512 B, 12.4 KB, 3.50 MB, 1.20 GB).
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0L) return "0 B"
        val kb = 1024.0
        val mb = kb * 1024.0
        val gb = mb * 1024.0

        return when {
            bytes >= gb -> String.format(Locale.US, "%.2f GB", bytes / gb)
            bytes >= mb -> String.format(Locale.US, "%.2f MB", bytes / mb)
            bytes >= kb -> String.format(Locale.US, "%.1f KB", bytes / kb)
            else -> "$bytes B"
        }
    }

    /**
     * Extracts lowercase file extension from a file name or path without the leading dot.
     * Returns empty string if no extension is found or fileName is null/blank.
     */
    fun getFileExtension(fileName: String?): String {
        if (fileName.isNullOrBlank()) return ""
        val cleanName = fileName.substringAfterLast('/').substringAfterLast('\\')
        val dotIndex = cleanName.lastIndexOf('.')
        return if (dotIndex > 0 && dotIndex < cleanName.length - 1) {
            cleanName.substring(dotIndex + 1).lowercase(Locale.US)
        } else {
            ""
        }
    }

    /**
     * Checks if a given file name or path has a .pdf extension.
     */
    fun isPdf(fileName: String?): Boolean {
        return getFileExtension(fileName) == "pdf"
    }
}
