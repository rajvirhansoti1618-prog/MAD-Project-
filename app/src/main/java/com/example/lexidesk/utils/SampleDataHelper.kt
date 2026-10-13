package com.example.lexidesk.utils

import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DocumentEntity
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.entity.ReminderEntity
import com.example.lexidesk.data.entity.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SampleDataHelper {

    suspend fun loadSampleData(app: LexiDeskApp) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L

        // 1. Sample Clients
        val client1Id = app.clientRepository.insertClient(
            ClientEntity(
                fullName = "Aditya Sharma",
                clientType = "Individual",
                phone = "+91 98201 12345",
                email = "aditya.sharma@example.com",
                address = "Flat 402, Sea Crest Towers, Worli, Mumbai",
                companyName = "Self Employed",
                notes = "Retained for civil property litigation and will drafting.",
                tags = "Civil, High Net Worth"
            )
        )

        val client2Id = app.clientRepository.insertClient(
            ClientEntity(
                fullName = "NovaTech Innovations LLP",
                clientType = "Business",
                phone = "+91 99302 67890",
                email = "legal@novatechinnovations.com",
                address = "Plot 18, MIDC InfoTech Park, Andheri East, Mumbai",
                companyName = "NovaTech Innovations LLP",
                notes = "Corporate IT compliance, GST filings, and employment master service agreements.",
                tags = "Corporate, Tech, Retainer"
            )
        )

        val client3Id = app.clientRepository.insertClient(
            ClientEntity(
                fullName = "Meera Kapoor",
                clientType = "Individual",
                phone = "+91 98111 22334",
                email = "meera.kapoor@example.com",
                address = "B-12, Gulmohar Enclave, Vasant Kunj, New Delhi",
                companyName = "Kapoor Interiors",
                notes = "Trademark registration dispute and supplier contract breach.",
                tags = "IPR, Commercial"
            )
        )

        // 2. Sample Cases / Assignments
        val case1Id = app.caseRepository.insertCase(
            CaseEntity(
                clientId = client1Id,
                title = "Sharma Property Partition Appeal",
                category = "Civil Dispute",
                description = "Appeal before the High Court regarding ancestral property division deed.",
                referenceNumber = "HC-CIV-2026/894",
                priority = "Urgent",
                status = "In Progress",
                startDate = now - (15 * oneDay),
                dueDate = now + (2 * oneDay),
                courtOrLocation = "High Court of Judicature, Court Hall 4",
                notes = "Pre-hearing briefing scheduled with Senior Counsel."
            )
        )

        val case2Id = app.caseRepository.insertCase(
            CaseEntity(
                clientId = client2Id,
                title = "Annual Corporate Compliance & GST Audit 2025-26",
                category = "Tax & Corporate Advisory",
                description = "Annual statutory filing review, reconciliation of GSTR-2B, and Director disclosures.",
                referenceNumber = "AUD-NT-2026-03",
                priority = "High",
                status = "In Progress",
                startDate = now - (7 * oneDay),
                dueDate = now + (5 * oneDay),
                courtOrLocation = "Virtual / Corporate Registrar Portal",
                notes = "Awaiting final books of accounts signed by CFO."
            )
        )

        val case3Id = app.caseRepository.insertCase(
            CaseEntity(
                clientId = client3Id,
                title = "Trademark Infringement Notice & Reply",
                category = "Intellectual Property",
                description = "Cease and desist response regarding trademark 'Kapoor Living & Spaces'.",
                referenceNumber = "TM-DISP-781",
                priority = "Medium",
                status = "Waiting",
                startDate = now - (3 * oneDay),
                dueDate = now + (10 * oneDay),
                courtOrLocation = "Trademark Registry, Mumbai",
                notes = "Draft response letter reviewed with client."
            )
        )

        // 3. Sample Tasks
        app.taskRepository.insertTask(
            TaskEntity(
                title = "Draft Rebuttal Affidavit for HC Hearing",
                description = "Incorporate updated municipal land registry survey notes into Section 4.",
                clientId = client1Id,
                caseId = case1Id,
                priority = "Urgent",
                status = "In Progress",
                dueDate = now + (1 * oneDay),
                dueTime = "04:30 PM",
                reminderEnabled = true
            )
        )

        app.taskRepository.insertTask(
            TaskEntity(
                title = "Review NovaTech Form GSTR-9 Reconciliation",
                description = "Cross-check input tax credit disparity flagged by auditor.",
                clientId = client2Id,
                caseId = case2Id,
                priority = "High",
                status = "Pending",
                dueDate = now + (3 * oneDay),
                dueTime = "11:00 AM",
                reminderEnabled = true
            )
        )

        app.taskRepository.insertTask(
            TaskEntity(
                title = "Client Intake Consultation & KYC Sign-off",
                description = "Verify original identity documents and obtain signature on engagement contract.",
                clientId = client3Id,
                caseId = case3Id,
                priority = "Medium",
                status = "Completed",
                dueDate = now - (1 * oneDay),
                dueTime = "02:00 PM",
                reminderEnabled = false
            )
        )

        // 4. Sample Reminders / Calendar Events
        app.reminderRepository.insertReminder(
            ReminderEntity(
                title = "High Court Bench Hearing (Room 4)",
                description = "Matter item #14 on final cause list. Bring original partition deed bundle.",
                reminderType = "Court Hearing",
                eventTimestamp = now + (2 * oneDay) + (10 * 60 * 60 * 1000L),
                minutesBefore = 60,
                clientId = client1Id,
                caseId = case1Id
            )
        )

        app.reminderRepository.insertReminder(
            ReminderEntity(
                title = "Client Video Consultation with CFO",
                description = "Review Q4 balance sheet adjustments and TDS deductions.",
                reminderType = "Client Meeting",
                eventTimestamp = now + (1 * oneDay) + (4 * 60 * 60 * 1000L),
                minutesBefore = 30,
                clientId = client2Id,
                caseId = case2Id
            )
        )

        // 5. Sample Documents
        app.documentRepository.insertDocument(
            DocumentEntity(
                title = "Client Retainer Agreement",
                fileName = "Retainer_Agreement_Sharma.pdf",
                filePath = "sample/retainer.pdf",
                fileType = "pdf",
                fileSize = 428000,
                category = "Contracts",
                clientId = client1Id,
                caseId = case1Id,
                description = "Executed bilateral engagement agreement for civil representation."
            )
        )

        app.documentRepository.insertDocument(
            DocumentEntity(
                title = "GST Filing Summary & Challan",
                fileName = "GSTR_Summary_NovaTech.pdf",
                filePath = "sample/gst.pdf",
                fileType = "pdf",
                fileSize = 852000,
                category = "Tax Documents",
                clientId = client2Id,
                caseId = case2Id,
                description = "Challan payments and acknowledged receipt from GSTN portal."
            )
        )

        // 6. Notes
        app.noteRepository.insertNote(
            NoteEntity(
                clientId = client1Id,
                caseId = case1Id,
                title = "Preliminary Witness Statements",
                content = "Survey officer confirms boundaries marked in 2018 match the revenue maps.",
                followUpDate = now + (4 * oneDay)
            )
        )

        app.userPreferencesRepository.setSampleDataLoaded(true)
    }

    suspend fun clearAllData(app: LexiDeskApp) = withContext(Dispatchers.IO) {
        app.database.clearAllTables()
        app.userPreferencesRepository.setSampleDataLoaded(false)
    }
}
