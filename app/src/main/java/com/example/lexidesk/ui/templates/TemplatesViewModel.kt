package com.example.lexidesk.ui.templates

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DraftTemplateEntity
import com.example.lexidesk.pdf.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TemplateItem(
    val type: String,
    val title: String,
    val description: String,
    val category: String,
    val defaultContent: String
)

class TemplatesViewModel(private val app: LexiDeskApp) : ViewModel() {

    val sampleTemplates = listOf(
        TemplateItem(
            type = "EngagementLetter",
            title = "Client Engagement & Retainer Letter",
            description = "Standard terms of professional engagement, scope of work, and retainer fees.",
            category = "Agreements",
            defaultContent = """
RE: ENGAGEMENT OF PROFESSIONAL SERVICES

Dear [CLIENT_NAME],

We are pleased to confirm our engagement to provide professional advisory and representation services to you / your firm, [COMPANY_NAME], in connection with [MATTER_TITLE].

1. SCOPE OF ENGAGEMENT
Our services will include legal / financial analysis, preparation of requisite documentation, statutory submissions, and appearances before relevant authorities as necessary.

2. PROFESSIONAL FEES & BILLING
The agreed fee for this assignment is [AMOUNT], payable in installments as milestones are reached. Disbursements including court fees, stamp duties, and travel expenses will be billed at actuals.

3. CONFIDENTIALITY
All communications and documents furnished will remain strictly confidential and privileged.

Please acknowledge your acceptance of these terms by signing below.

Sincerely,
[FIRM_NAME]
Authorized Signatory
            """.trimIndent()
        ),
        TemplateItem(
            type = "LegalNotice",
            title = "Formal Legal / Demand Notice",
            description = "Draft legal notice for breach of contract, non-payment, or statutory demands.",
            category = "Notices",
            defaultContent = """
LEGAL NOTICE / FORMAL DEMAND
Ref No: [REFERENCE_NO]
Date: [DATE]

TO:
[RECIPIENT_NAME]
[ADDRESS]

UNDER INSTRUCTIONS FROM OUR CLIENT, [CLIENT_NAME], residing at [CLIENT_ADDRESS], WE HEREBY CALL UPON YOU AS FOLLOWS:

1. That you entered into an agreement dated [DATE] for the performance of agreed contractual obligations.
2. That in breach of the aforesaid covenants, you failed and neglected to make payment of [AMOUNT] despite multiple reminders.
3. WE HEREBY DEMAND that within 15 (fifteen) days from receipt of this notice, you remit the sum of [AMOUNT] along with interest.
4. Failure to comply will compel our client to initiate appropriate legal proceedings before the competent court at your risk and consequence.

Copy retained for legal record.

[FIRM_NAME]
Advocates & Legal Advisors
            """.trimIndent()
        ),
        TemplateItem(
            type = "Invoice",
            title = "Professional Fee Invoice",
            description = "Itemized invoice for professional consulting, litigation, or audit fees.",
            category = "Billing",
            defaultContent = """
TAX INVOICE FOR PROFESSIONAL SERVICES
Invoice No: [REFERENCE_NO]
Date: [DATE]

BILLED TO:
Client: [CLIENT_NAME]
Firm / Organization: [COMPANY_NAME]
Address: [ADDRESS]

DESCRIPTION OF SERVICES:
1. Professional consultation & drafting of documentation: [AMOUNT]
2. Statutory filing fees and disbursements: Paid at actuals
3. Review of compliance books: Completed

TOTAL PAYABLE: [AMOUNT]

Bank Account Name: [FIRM_NAME]
Payment Terms: Payable within 7 business days from receipt.

Thank you for your business.
            """.trimIndent()
        ),
        TemplateItem(
            type = "MeetingMinutes",
            title = "Professional Meeting Minutes & Action Items",
            description = "Structured record of client consultation, agreed deliverables, and deadlines.",
            category = "Notes",
            defaultContent = """
RECORD OF CONSULTATION / MEETING MINUTES
Date: [DATE]
Location / Forum: [VENUE]
Matter: [MATTER_TITLE]

ATTENDEES:
- Professional Team: [FIRM_NAME]
- Client Representatives: [CLIENT_NAME]

SUMMARY OF DISCUSSION:
1. Reviewed factual chronology and recent communications received from opposing party / tax authorities.
2. Assessed risk factors regarding pending statutory deadlines.

AGREED ACTION ITEMS:
- Client to furnish certified copies of agreements by [DATE].
- LexiDesk team to prepare initial draft reply by [DATE].
- Next briefing conference scheduled for next week.

Recorded by:
[FIRM_NAME]
            """.trimIndent()
        ),
        TemplateItem(
            type = "Receipt",
            title = "Payment Receipt & Acknowledgement",
            description = "Official receipt for client retainer or professional fees received.",
            category = "Billing",
            defaultContent = """
PAYMENT RECEIPT ACKNOWLEDGEMENT
Receipt No: [REFERENCE_NO]
Date: [DATE]

Received with thanks from: [CLIENT_NAME]
The sum of: [AMOUNT]
Payment Mode: Bank Transfer / UPI / Cheque
Towards: Professional fee retainer for [MATTER_TITLE]

Balance Outstanding: Nil / As per ledger

[FIRM_NAME]
Authorized Signature
            """.trimIndent()
        )
    )

    val clientsFlow: StateFlow<List<ClientEntity>> = app.clientRepository.allClients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val draftsFlow: StateFlow<List<DraftTemplateEntity>> = app.draftTemplateRepository.allDrafts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveDraft(
        id: Long = 0,
        type: String,
        title: String,
        content: String,
        clientId: Long?,
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val draft = DraftTemplateEntity(
                    id = id,
                    templateType = type,
                    title = title.trim(),
                    content = content,
                    clientId = clientId,
                    lastModified = System.currentTimeMillis()
                )
                val newId = if (id == 0L) {
                    app.draftTemplateRepository.insertDraft(draft)
                } else {
                    app.draftTemplateRepository.updateDraft(draft)
                    id
                }
                onSaved(newId)
            } catch (e: Exception) {
                Log.e("TemplatesViewModel", "Failed to save draft template", e)
            }
        }
    }

    suspend fun getDraftByIdOnce(id: Long): DraftTemplateEntity? {
        return app.draftTemplateRepository.getDraftByIdOnce(id)
    }

    fun generateAndSharePdf(
        context: Context,
        title: String,
        content: String,
        clientName: String,
        firmName: String
    ): File? {
        val file = PdfGenerator.generateDocumentPdf(
            context = context,
            title = title,
            content = content,
            clientName = clientName,
            firmName = firmName
        )
        if (file != null) {
            PdfGenerator.sharePdf(context, file, title)
        }
        return file
    }
}
