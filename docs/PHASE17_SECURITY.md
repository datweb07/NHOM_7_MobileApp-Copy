# Phase 17 — Security hardening

## Firebase setup and deployment

Firestore access defaults to deny. Review the rules and indexes in this repository before deploying them to the Firebase project configured for this app:

```powershell
firebase deploy --only firestore:rules,firestore:indexes --project YOUR_FIREBASE_PROJECT_ID
```

This command deploys to the named live Firebase project. Run the emulator tests first; do not substitute a production project ID when running tests.

### App Check (Play Integrity)

The Android app installs the App Check Debug provider only in debug builds and Play Integrity in release builds when Firebase is configured. To activate protection:

1. Register the Android app in Firebase App Check and link the correct Play Console app.
2. Ensure the Play Integrity API is enabled and the app's signing certificate fingerprints are registered in Firebase.
3. Install a Play-distributed/internal-testing build on a supported device and verify valid App Check traffic in Firebase metrics.
4. For local debug builds, copy the debug token printed in Logcat and register it as an App Check debug token. Do not commit or share this token. For release, monitor Play Integrity metrics before enabling enforcement for Firestore. Enforcement blocks clients without valid App Check tokens.

Do not commit App Check debug tokens. The debug and release installer classes live in separate Android source sets, and the debug-only dependency keeps the Debug provider out of release builds; release installs Play Integrity only.

## Tests

From the repository root:

```powershell
npm --prefix security-tests install
firebase emulators:exec --only firestore --project demo-rescuefarm-security "npm --prefix security-tests test"
```

The `demo-` project ID and emulator ensure these tests do not access production data. Tests cover guest read/write denial, order ownership, an allowed atomic checkout, cross-seller reservation rejection, seller self-approval/inventory-forgery denial, profile moderation-field denial, hard-delete denial for order/item/payment records, and unknown/credential-like product fields.

## Guest checkout and trusted pricing limits

Guest cart remains local. Guest Firestore order creation and guest order lookup are deliberately disabled: a client-chosen order code plus phone number is not a secure authorization credential. Customers must authenticate before online checkout/tracking.

Firestore Rules can check ownership and transaction relationships, but cannot securely recompute arbitrary aggregates across all order item documents or independently run the full promotion/volume-discount pricing algorithm. Client-side customer checkout therefore is not a sufficient production trust boundary for pricing or inventory economics. Before enabling high-value production sales, move checkout validation/reservation to a trusted backend (for example, a callable Cloud Function) that derives prices and totals from authoritative product, batch, and promotion data and applies idempotency. Rules still deny guest writes and cross-seller or invalid reservation paths.

## Secrets and media uploads

Firebase client configuration/API key is an app identifier, not a server secret; authorization must come from Firebase Auth, Rules, and App Check. Do not put service-account credentials, Cloudinary API secrets, or other private keys in source control or the APK. The current project exposes media through an abstraction and has no Cloudinary credentials/preset to harden. If Cloudinary uploads are introduced, use a restricted unsigned upload preset (folder, file types, and size limits), and keep signing/API secrets on a trusted backend.

## Operational notes

- Firebase Console setup and Rules deployment are intentionally not performed automatically against a live project.
- App Check enforcement should be enabled only after valid production traffic has been verified.
- Keep Firestore emulator tests in CI and add cases whenever a new write path or security-sensitive schema is introduced.
- Do not describe unauthenticated guest Firestore writes as production-grade; the supported guest experience is local-only until a trusted guest-token backend is implemented.
