# Firebase Realtime Database Synchronization Guide

This document describes the multi-device live synchronization architecture implemented for **LexiDesk**, connecting the local offline-first Room database to **Firebase Realtime Database**.

---

## 1. Firebase Project & Database Details

| Parameter | Value |
|---|---|
| **Project ID** | `lexidesk-b3d7b` |
| **Database URL** | `https://lexidesk-b3d7b-default-rtdb.asia-southeast1.firebasedatabase.app` |
| **Region** | `asia-southeast1` (Singapore) |
| **Application ID** | `com.example.lexidesk` |

---

## 2. Security Rules Setup

To protect client confidentiality and prevent unauthorized access, **public read and write are completely disabled**. Access is restricted strictly to authenticated users.

Copy the contents of [`firebase-database.rules.json`](../firebase-database.rules.json) into **Firebase Console -> Realtime Database -> Rules**:

```json
{
  "rules": {
    "workspaces": {
      "$workspaceId": {
        ".read": "auth != null",
        ".write": "auth != null",
        "clients": {
          "$clientId": {
            ".validate": "newData.hasChildren(['id', 'updatedAt'])"
          }
        },
        "cases": {
          "$caseId": {
            ".validate": "newData.hasChildren(['id', 'updatedAt'])"
          }
        },
        "tasks": {
          "$taskId": {
            ".validate": "newData.hasChildren(['id', 'updatedAt'])"
          }
        },
        "notes": {
          "$noteId": {
            ".validate": "newData.hasChildren(['id', 'updatedAt'])"
          }
        },
        "reminders": {
          "$reminderId": {
            ".validate": "newData.hasChildren(['id', 'updatedAt'])"
          }
        },
        "draft_templates": {
          "$draftId": {
            ".validate": "newData.hasChildren(['id', 'updatedAt'])"
          }
        }
      }
    },
    ".read": false,
    ".write": false
  }
}
```

> **Security Note:** Neither public reading nor public writing is allowed at the database root. All requests require valid Firebase Authentication credentials.

---

## 3. Architecture & Data Flow

```
+-------------------------------------------------------------+
|                     Jetpack Compose UI                      |
+-------------------------------------------------------------+
                              ^
                              | (Reactive Kotlin Flows)
                              v
+-------------------------------------------------------------+
|               Local Source of Truth: Room DB                |
+-------------------------------------------------------------+
               |                                ^
  (Local Write |                                | (Live Remote Event)
       Trigger)v                                |
+--------------------------------+   +------------------------+
|      CloudSyncPublisher        |   | RealtimeSyncSubscriber |
+--------------------------------+   +------------------------+
        |                  |                     ^
 (Online)|          (Offline|Fail)                |
        v                  v                     |
+---------------+  +---------------------+       |
| Firebase RTDB |  | OfflineMutationQueue|       |
+---------------+  +---------------------+       |
        ^                  |                     |
        |                  v (On Network Return) |
        +-----------[ SyncRetryWorker ]----------+
```

1. **Local-First Writes:** Creating, updating, or deleting a record immediately commits to Room so UI responses are instant.
2. **Cloud Publishing:** The repository asynchronously invokes `CloudSyncPublisher` to push changes to Firebase RTDB.
3. **Live Remote Ingestion:** `RealtimeSyncSubscriber` listens to child events (`onChildAdded`, `onChildChanged`, `onChildRemoved`) and updates Room safely.
4. **Loopback Prevention:** Ingestion ignores remote changes if the local timestamp is newer or equal (`local.updatedAt >= remote.updatedAt`).
5. **Deletions via Tombstones:** Records are flagged with `"isDeleted": true` and timestamped, ensuring deletions reliably propagate across offline/online devices without disappearing from sync history prematurely.
6. **Offline Queue & Retry:** If an upload fails due to network outage, it is enqueued into `OfflineSyncQueueManager` and retried automatically by `SyncRetryWorker` when connectivity returns.

---

## 4. Two-Device Verification Guide

Follow these steps to verify live synchronization across two devices or emulators:

### Scenario A: Realtime Creation & Editing
1. Launch LexiDesk on **Device 1** and **Device 2** (or emulator).
2. On **Device 1**, go to **Clients -> Add Client** and save a new client (e.g. *"Apex Law Partners"*).
3. Observe **Device 2**: within 1–2 seconds, *"Apex Law Partners"* appears automatically in the Clients list without manual refresh.
4. On **Device 2**, edit the client phone number.
5. Observe **Device 1**: the updated phone number reflects immediately.

### Scenario B: Record Deletion
1. On **Device 2**, open the client details and tap **Delete**.
2. Observe **Device 1**: the client is removed from the list immediately via the tombstone event.

### Scenario C: Offline Resilience & Recovery
1. Put **Device 1** in Airplane Mode (disable Wi-Fi and Cellular).
2. Create a new Task *"File Special Leave Petition"* on **Device 1**. It saves instantly to Room locally.
3. Turn off Airplane Mode on **Device 1**.
4. The background queue flushes the mutation to Firebase, and the task appears on **Device 2**.

---

## 5. Summary of Implementation Commits

All changes were implemented through 10 reviewable Git checkpoints:

1. `feat(firebase): configure Realtime Database integration`
2. `feat(firebase): add shared cloud data source`
3. `feat(firebase): add authenticated database access`
4. `feat(sync): map local entities to cloud models`
5. `feat(sync): add safe initial data synchronization`
6. `feat(sync): upload local record changes`
7. `feat(sync): receive live cloud updates`
8. `feat(sync): retry pending offline changes`
9. `test(sync): verify cloud synchronization and regressions`
10. `docs(firebase): document setup and sync verification`
