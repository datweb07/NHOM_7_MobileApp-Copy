# External setup required

Phase 1 deliberately builds without external credentials.

## Firebase

Detailed Phase 2 instructions are in `docs/FIREBASE_SETUP.md`.

Before Phase 2, the team must:

1. Create or select the RescueFarm Firebase project.
2. Register Android application ID `com.rescuefarm`.
3. Place the real `google-services.json` in `app/`.
4. Enable the required Authentication providers.
5. Create the Firestore database and review security rules before allowing writes.
6. Add FCM configuration when notifications are implemented.

Never commit a fabricated Firebase configuration.

## Cloudinary

Before image upload is implemented, provide a restricted unsigned upload preset for coursework or a trusted signing backend for production. Never place a Cloudinary API secret in the APK.

## Location

Foreground location permissions and `FusedLocationProviderClient` are intentionally deferred until the profile/mobile-rescue phases. Google Maps SDK and background tracking are outside the core scope.

## Promotion pricing

Phase 8 không cần dịch vụ hay dependency mới. Deploy Firestore rules và kiểm tra schema theo `docs/PHASE8_SETUP.md`.

## Cart sync

Phase 9 không cần dependency hay Room migration mới. Deploy Firestore rules cho `customerCarts` và kiểm tra theo `docs/PHASE9_SETUP.md`.

## Checkout reservation

Phase 10 cần deploy Firestore rules và index guest tracking. Xem `docs/PHASE10_SETUP.md`; không cần dependency mới.
