# Phase 3 setup: Profile, Address, Seller Application, Location

Phase 3 reuses the Firebase project and `app/google-services.json` from Phase 2. It does not require Maps SDK, background location, a service account, or a separate geocoding API key.

## 1. Deploy the updated Firestore rules

From the project root, authenticate with Firebase CLI if necessary and deploy:

```powershell
firebase deploy --only firestore:rules
```

The rules allow a customer to manage only their own addresses and a seller to read and submit only their own application. A rejected application may be resubmitted as `PENDING`; no client can set `sellerStatus` or approve its own application.

## 2. Location prerequisites

The app declares only foreground `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. Permission is requested after the user taps **Dùng vị trí hiện tại**. There is deliberately no `ACCESS_BACKGROUND_LOCATION` permission.

Use a physical Android device with Google Play services, or an emulator image that includes Google APIs/Google Play. Turn Location on, then grant either approximate or precise access. Approximate access is supported.

Android's `Geocoder` is device-provided and best-effort. When it is unavailable or returns no result, coordinates are kept and the user can enter the address fields manually.

## 3. Firestore document shape

- Common profile: `users/{uid}`
- Customer addresses: `users/{uid}/addresses/{addressId}`
- Seller application: `sellerApplications/{uid}`

`users/{uid}.defaultAddressId` is the source of truth for the default address. The repository changes it and the address `isDefault` flags in one Firestore transaction.

## 4. Manual smoke test

1. Log in as a customer, open **Hồ sơ**, edit the common profile, then add two addresses.
2. Verify the first address becomes default; choose the second as default and then delete the first.
3. Tap **Dùng vị trí hiện tại**, test approximate/precise permission, denial, Location off, and manual entry after a Geocoder failure.
4. Log in as a seller, open **Hồ sơ người bán**, and submit an application with an HTTPS proof-image URL.
5. Verify Firestore stores it as `PENDING`, while the seller remains `PENDING_APPROVAL` and cannot sell.
