# Phase 4 setup: Catalog, Product Batch, and Inventory

Phase 4 reuses the Firebase project from Phases 2–3. No new Google API key is required.

## 1. Sync the new dependency

Sync Gradle so Android Studio downloads Glide `5.0.9`. `INTERNET` and `ACCESS_NETWORK_STATE` are already declared. No Glide annotation processor or custom module is required for the current basic URL-loading use case.

## 2. Deploy Firestore rules

```powershell
firebase deploy --only firestore:rules
```

The rules provide public read access only to active categories/products, allow inventory reads, and require an authenticated seller with `users/{uid}.sellerStatus == APPROVED` for product and batch writes. Product ratings are immutable from seller clients. Batch updates require `inventoryVersion + 1`, a valid quantity invariant, and a recognized stock transition.

## 3. Seed at least one category

Use Firebase Console → Firestore Database → **Start collection** and create `categories/{categoryId}`. Example document ID: `vegetables`.

```text
name: "Rau củ"
imageUrl: "https://example.com/vegetables.jpg"
active: true
displayOrder: 1
```

Repeat for categories such as `fruits`, `rice`, or `processed`. Product writes are rejected when `categoryId` does not point to an active category.

## 4. Approve the seller used for testing

Phase 4 deliberately does not self-approve sellers. In Firestore, the test seller's user document must have:

```text
role: "SELLER"
sellerStatus: "APPROVED"
```

Until Phase 10 adds the admin approval UI, set this only from Firebase Console or a trusted Admin SDK environment—not from the Android client.

## 5. Collections used

- `categories/{categoryId}`
- `products/{productId}`
- `productBatches/{batchId}`

Room mirrors these collections in `category_cache`, `product_cache`, and `product_batch_cache`. Database migration `1 → 2` preserves existing Room data.

## 6. Smoke test

1. Open the catalog as a guest, refresh online, then reopen without network and verify cached products remain visible.
2. Sign in as an approved seller and create a product using an active category ID.
3. Create a batch with `harvestDate < expiryDate`, a future expiry date, and positive quantity.
4. Open the same batch on two clients. Save one, then save the stale copy and verify the second operation reports a conflict.
5. Verify expired batches are excluded from sellable stock.
6. Verify a batch with reserved/sold quantity cannot change its initial quantity or be deleted.
