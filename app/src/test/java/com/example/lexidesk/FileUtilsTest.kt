package com.example.lexidesk

import com.example.lexidesk.utils.FileUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class FileUtilsTest {

    @Test
    fun formatFileSize_zeroOrNegative_returnsZeroB() {
        assertEquals("0 B", FileUtils.formatFileSize(0L))
        assertEquals("0 B", FileUtils.formatFileSize(-100L))
    }

    @Test
    fun formatFileSize_bytes_returnsCorrectFormat() {
        assertEquals("1 B", FileUtils.formatFileSize(1L))
        assertEquals("512 B", FileUtils.formatFileSize(512L))
        assertEquals("1023 B", FileUtils.formatFileSize(1023L))
    }

    @Test
    fun formatFileSize_kilobytes_returnsCorrectFormat() {
        assertEquals("1.0 KB", FileUtils.formatFileSize(1024L))
        assertEquals("1.5 KB", FileUtils.formatFileSize(1536L))
        assertEquals("20.0 KB", FileUtils.formatFileSize(20480L))
    }

    @Test
    fun formatFileSize_megabytes_returnsCorrectFormat() {
        val oneMb = 1024L * 1024L
        assertEquals("1.00 MB", FileUtils.formatFileSize(oneMb))
        assertEquals("4.50 MB", FileUtils.formatFileSize((4.5 * oneMb).toLong()))
    }

    @Test
    fun formatFileSize_gigabytes_returnsCorrectFormat() {
        val oneGb = 1024L * 1024L * 1024L
        assertEquals("1.00 GB", FileUtils.formatFileSize(oneGb))
        assertEquals("2.50 GB", FileUtils.formatFileSize((2.5 * oneGb).toLong()))
    }
}
