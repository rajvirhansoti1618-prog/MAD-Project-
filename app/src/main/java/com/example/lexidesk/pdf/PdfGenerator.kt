package com.example.lexidesk.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateDocumentPdf(
        context: Context,
        title: String,
        content: String,
        clientName: String = "",
        firmName: String = "LexiDesk Professional Services",
        referenceNumber: String = ""
    ): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 points
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Header background banner (Dark Navy: #0F2537)
        paint.color = Color.rgb(15, 37, 55)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Header Title
        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(firmName.ifBlank { "LexiDesk Services" }, 40f, 50f, paint)

        // Subheader Date & Ref
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dateString = "Date: ${dateFormat.format(Date())}"
        canvas.drawText(dateString, 40f, 75f, paint)

        if (referenceNumber.isNotBlank()) {
            val refText = "Ref: $referenceNumber"
            val refWidth = paint.measureText(refText)
            canvas.drawText(refText, 555f - refWidth, 75f, paint)
        }

        // Document Title
        paint.color = Color.rgb(15, 37, 55)
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(title, 40f, 130f, paint)

        // Client info if provided
        var currentY = 150f
        if (clientName.isNotBlank()) {
            paint.color = Color.rgb(71, 85, 105)
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText("Client: $clientName", 40f, currentY, paint)
            currentY += 20f
        }

        // Separator line
        paint.color = Color.rgb(226, 232, 240)
        paint.strokeWidth = 1.5f
        canvas.drawLine(40f, currentY, 555f, currentY, paint)
        currentY += 25f

        // Body content
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 11f
        paint.typeface = Typeface.DEFAULT
        paint.strokeWidth = 0f

        val maxWidth = 515f
        val lines = content.split("\n")

        for (paragraph in lines) {
            val words = paragraph.split(" ")
            var currentLine = StringBuilder()

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) > maxWidth) {
                    canvas.drawText(currentLine.toString(), 40f, currentY, paint)
                    currentY += 16f
                    currentLine = StringBuilder(word)
                } else {
                    currentLine = StringBuilder(testLine)
                }

                if (currentY > 780f) {
                    break // Keep single page clean for quick MAD export
                }
            }

            if (currentLine.isNotEmpty() && currentY <= 780f) {
                canvas.drawText(currentLine.toString(), 40f, currentY, paint)
                currentY += 16f
            }
            currentY += 8f

            if (currentY > 780f) break
        }

        // Footer
        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 9f
        canvas.drawText("Generated via LexiDesk • Professional Productivity Assistant", 40f, 815f, paint)

        document.finishPage(page)

        // Save PDF to app storage
        val timeStamp = System.currentTimeMillis()
        val safeTitle = title.replace(Regex("[^a-zA-Z0-9]"), "_").take(20)
        val file = File(context.filesDir, "LexiDesk_${safeTitle}_$timeStamp.pdf")

        return try {
            val outputStream = FileOutputStream(file)
            document.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }

    fun sharePdf(context: Context, file: File, title: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Document PDF"))
    }

    fun viewPdf(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // In case no PDF viewer is installed, trigger share
            sharePdf(context, file, file.name)
        }
    }

    fun viewDocument(context: Context, file: File, fileType: String = "") {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val mimeType = when (fileType.lowercase()) {
            "pdf" -> "application/pdf"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "txt" -> "text/plain"
            else -> {
                when (file.extension.lowercase()) {
                    "pdf" -> "application/pdf"
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    "txt" -> "text/plain"
                    else -> "*/*"
                }
            }
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            sharePdf(context, file, file.name)
        }
    }
}
