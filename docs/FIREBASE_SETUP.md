# Firebase setup for Phase 2

The Android application ID is `com.rescuefarm`. Do not register the old sample package.

## 1. Create and register the Android app

1. Open the Firebase Console and create/select the RescueFarm project.
2. Add an Android app with package name `com.rescuefarm`.
3. Add the debug SHA-1 and SHA-256 fingerprints. Android Studio can show them with the Gradle `signingReport` task.
4. Download the generated `google-services.json`.
5. Copy it to `app/google-services.json` — not the repository root.
6. Sync Gradle. This project applies the Google Services plugin automatically when that file exists.

`google-services.json` contains project identifiers, not an API secret, but use the file from the correct Firebase project and avoid sharing unrelated environment configs.

## 2. Enable Authentication providers

In Firebase Console → Authentication → Sign-in method:

1. Enable **Email/Password**.
2. Enable **Google** and choose a project support email.
3. Download `google-services.json` again after enabling Google so it includes the Web OAuth client used as `default_web_client_id`.

Do not create or store passwords in Firestore. Firebase Authentication owns credentials.

## 3. Create Firestore

1. Open Firestore Database and create a database in the region selected by the team.
2. Do not leave the database in open test mode.
3. Deploy the repository's `firestore.rules` and `firestore.indexes.json`, or paste the rules into the Firebase Console Rules editor.
4. Publish the rules.

With Firebase CLI installed and the correct project selected, deployment is:

```powershell
firebase login
firebase use --add
firebase deploy --only firestore
```

Phase 2 writes profiles to `users/{firebaseUid}`. Customer profiles use role `CUSTOMER`. Seller profiles use role `SELLER` plus `sellerStatus = PENDING_APPROVAL`; seller approval is intentionally deferred.

## 4. Verify

1. Run `gradlew.bat :app:assembleDebug`.
2. Install the debug app.
3. Register a Customer and confirm both Authentication and `users/{uid}` exist.
4. Register a Seller and confirm `sellerStatus` is `PENDING_APPROVAL`.
5. Test password reset and Google Sign-In.
6. Remove network access temporarily and confirm auth shows an error instead of creating fake success.

Guest Mode does not use Firebase Anonymous Authentication. Its generated `guest_<uuid>` is stored only in SharedPreferences.
