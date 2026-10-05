# Phase 5 setup: Rescue Campaign and Mobile Rescue

Phase 5 reuses Firebase, Firestore, Fused Location Provider, and the foreground location permissions configured in Phases 2–4. It adds no API key, Maps SDK, background-location permission, or new Gradle dependency.

## 1. Deploy Firestore rules

From the repository root:

```powershell
firebase deploy --only firestore:rules
```

The new rules provide these contracts:

- Everyone can read only `ACTIVE` campaigns.
- An approved seller can create drafts and submit `PENDING_APPROVAL` campaigns that they own.
- Only draft/rejected campaigns can be edited by the seller.
- An `ACTIVE` mobile campaign can update its foreground location. A shared location uses Firestore server time.
- An Admin contract can change `PENDING_APPROVAL` to `ACTIVE` or `REJECTED`; Phase 5 intentionally has no Admin UI.
- Campaign progress and ProductBatch inventory mutations are validated independently and are written together by the repository transaction.

No composite Firestore index is required by the current equality-only queries.

## 2. Required existing data

Use a seller whose `users/{uid}` document contains:

```text
role: "SELLER"
sellerStatus: "APPROVED"
```

Create products and future, sellable batches from Phase 4 first. The campaign editor accepts one target per line:

```text
batchDocumentId=quantity
```

Example:

```text
batch-watermelon-01=120
batch-watermelon-02=80
```

The campaign target is calculated from the sum (`200` in this example). A campaign supports 1–8 batches. Submission is rejected when a batch is expired, belongs to another campaign, does not exist, or has less available stock than its target.

## 3. Approval test

1. Sign in as an approved seller and create a draft.
2. Submit it. Verify `campaigns/{campaignId}.status` is `PENDING_APPROVAL` and every targeted `productBatches/{batchId}.activeCampaignId` contains the campaign ID.
3. Until the Admin phase is implemented, use Firebase Console or a trusted Admin SDK environment to set the campaign status to `ACTIVE`. Do not add an approval switch to the Android seller client.
4. Refresh the public campaign list. Only the now-`ACTIVE` campaign should be highlighted.

## 4. Mobile Rescue test

1. Create the campaign with mode `MOBILE_POINT`, submit, and approve it.
2. Open **Seller profile → Manage campaigns → Mobile**.
3. Tap **Update current location** and grant foreground location permission.
4. From the public campaign list, tap **Mobile near me**. Only active mobile campaigns with sharing enabled and a location no older than 10 minutes are included and sorted using Haversine distance.
5. Wait beyond 10 minutes or disable sharing. The location must show as stale and disappear from the nearby list.

The app requests location only as the result of a user action. It does not use background tracking, Maps SDK, routes, or navigation.

## 5. Local cache and migration

Room database migration `2 → 3` adds the complete campaign fields to `campaign_cache` while preserving Phase 1–4 data. Test online refresh once, then reopen without network to verify cached active/seller campaigns remain visible.

After the first successful Android Studio/Gradle build, verify Room generated `app/schemas/com.rescuefarm.data.local.database.RescueFarmDatabase/3.json` and keep that schema file in version control.
