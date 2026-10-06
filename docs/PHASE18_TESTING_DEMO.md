# Phase 18 — Regression, demo and handoff

## Scope freeze

Phase 18 adds verification and handoff material only; it does not add domain behavior.
The 25 model classes under `domain/model` and existing enum values are frozen. If a
regression reveals a contract defect, record it and fix it in its owning phase rather than
introducing a new feature during the demo pass.

## Test commands

Run from the repository root in PowerShell:

```powershell
.\gradlew.bat clean testDebugUnitTest assembleDebug
```

For the Android launch smoke test, start an API 29+ emulator or connect a device:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

For Firestore Rules tests (demo project only; does not access a real Firebase project):

```powershell
npm ci --prefix security-tests
firebase emulators:exec --only firestore --project demo-rescuefarm-security "npm --prefix security-tests test"
```

The Gradle wrapper is pinned to JDK 25 through `gradle/gradle-daemon-jvm.properties` and
may provision that toolchain on first run; allow network access for the initial download.
Rules tests need Firebase CLI, Node.js/npm and Java. UI tests need an Android emulator or
device. No production project ID belongs in an emulator command.

## Current regression coverage map

Unit suites live under `app/src/test`; Firebase authorization scenarios live in
`security-tests/firestore.rules.test.cjs`; the instrumented app-start check lives in
`app/src/androidTest`. This matrix maps the frozen model set to the existing policy,
validation, lifecycle, and mapper tests. It is a coverage index, not a claim that every
Firebase read/write permission has an emulator case.

| Model | Regression suites |
| --- | --- |
| `Address` | `AddressTest`, `AddressDefaultPolicyTest`, `ProfileValidatorTest` |
| `Admin` | `AdminTransitionPolicyTest` |
| `Banner` | `BannerCacheMapperTest` |
| `Cart` | `GuestCartMapperTest`, `CartGroupingServiceTest`, `CartRevalidationServiceTest` |
| `CartItem` | `GuestCartMapperTest`, `CartGroupingServiceTest`, `CartRevalidationServiceTest` |
| `Category` | `CatalogCacheMapperTest` |
| `Comment` | `CommentTest`, `EngagementSafetyTest` |
| `Customer` | `AuthViewModelTest`, `ProfileViewModelTest` |
| `Favorite` | `EngagementSafetyTest` |
| `Notification` | `EngagementSafetyTest` |
| `Order` | `CheckoutPolicyTest`, `OrderLifecycleServiceTest`, `OrderCacheMapperTest` |
| `OrderItem` | `CheckoutReservationContractTest`, `CartRevalidationServiceTest` |
| `Payment` | `OrderLifecycleServiceTest` |
| `Post` | `PostModerationTest`, `PostCacheMapperTest` |
| `Product` | `ProductBoundaryTest`, `ProductValidatorTest`, `PricingServiceTest` |
| `ProductBatch` | `ProductBatchTest`, `ProductBatchBoundaryTest`, `InventoryServiceTest`, `InventoryVersionPolicyTest` |
| `Promotion` | `PricingServiceTest` |
| `Reaction` | `ReactionPolicyTest`, `EngagementSafetyTest` |
| `Report` | `EngagementSafetyTest`, `AdminTransitionPolicyTest` |
| `RescueCampaign` | `RescueCampaignTest`, `RescueCampaignBoundaryTest`, `CampaignLocationServiceTest` |
| `Review` | `EngagementSafetyTest`, `AdminTransitionPolicyTest` |
| `Seller` | `SellerProfileTest`, `SellerDashboardAggregatorTest` |
| `SellerApplication` | `ProfileViewModelTest`, `AdminTransitionPolicyTest` |
| `Shipment` | `OrderLifecycleServiceTest` |
| `User` | `AuthValidatorTest`, `AuthViewModelTest`, `SellerProfileTest` |

Cross-cutting regression includes cache migration/no destructive drop, offline critical
action blocking, checkout idempotency identity restoration, discovery filter restoration,
campaign freshness/urgency, and home section ordering. The on-site rescue flow unit test
combines mobile location freshness, inventory/campaign reservation, legal fulfillment
transitions, payment completion, and rescued progress. It verifies the domain contract;
it does not replace running the Firestore Emulator/device demo against configured Firebase.

## The 25 frozen business rules

This checklist follows the numbered rules in `final.md` §17. Each rule has at least one
regression test mapped below; rules marked Emulator require the Rules suite as well as the
unit/domain policy test.

| # | Rule | Test evidence |
| ---: | --- | --- |
| 1 | Exactly four actors: Guest, Customer, Seller, Admin | `ActorContractTest` |
| 2 | Guest is not an authenticated `User` | `ActorContractTest`, `AuthViewModelTest` |
| 3 | `UserRole` does not contain GUEST | `ActorContractTest` |
| 4 | Seller approval required before selling | `SellerProfileTest`, seller self-approval Rules test |
| 5 | Seller post must be approved before publish | `PostModerationTest` |
| 6 | Campaign approval required before ACTIVE | `campaignSubmission_startsPendingApproval`, `AdminTransitionPolicyTest` |
| 7 | Campaign requires a `RescueReason` | `campaignNeedsReasonAndAtLeastOneBatch` |
| 8 | Campaign needs at least one product batch | `campaignNeedsReasonAndAtLeastOneBatch` |
| 9 | Expired batch cannot be sold | `ProductBatchBoundaryTest.inventoryService_refusesToReserveExpiredBatch` |
| 10 | Only ACTIVE campaigns create rescue highlights | `RescueCampaignBoundaryTest.inactiveCampaign_hasNoHighlight` |
| 11 | CRITICAL ranks before HIGH/NORMAL | `HomeContentBuilderTest.criticalIsFirst_andStaleMobileIsExcludedFromNearby` |
| 12 | Mobile rescue needs fresh location to appear active/nearby | `CampaignLocationServiceTest`, `OnSiteRescueFlowTest` |
| 13 | Stale location is labeled or excluded from nearby | `CampaignLocationServiceTest`, `HomeContentBuilderTest` |
| 14 | Checkout quantity cannot exceed available stock | `CheckoutReservationContractTest.insufficientSecondBatch_meansNoExternalMutationBeforeCommit` |
| 15 | Order creation reserves stock atomically | Firestore Rules valid customer checkout test (Emulator) |
| 16 | PENDING quantity is not rescued | `RescueCampaignTest.pendingOrder_doesNotIncreaseRescuedQuantity` |
| 17 | DELIVERED moves reserved to sold/rescued | `InventoryServiceTest`, `OnSiteRescueFlowTest` |
| 18 | Cancellation releases inventory | `OnSiteRescueFlowTest.cancelledOnSiteOrder_releasesBatchAndCampaignReservation` |
| 19 | Cancellation releases campaign reservation | `OnSiteRescueFlowTest.cancelledOnSiteOrder_releasesBatchAndCampaignReservation` |
| 20 | Order item preserves product/price snapshot | `OnSiteRescueFlowTest` snapshot assertions |
| 21 | One Order has one Seller | `CheckoutPolicyTest.multipleSellers_areRejected`, cross-seller Rules test (Emulator) |
| 22 | Delivery creates a Shipment | `OrderLifecycleServiceTest.deliveryFollowsShippingPath`, `requiresShipment` assertion |
| 23 | PICKUP/ON_SITE_RESCUE do not require Shipment | `OrderLifecycleServiceTest.pickupUsesReadyForPickup`, `onsiteCompletesWithoutShipment` |
| 24 | Only the owning customer can review a delivered order item | `EngagementSafetyTest.deliveredOwnedItemCanBeReviewed`, pending/other-customer denials |
| 25 | Transactional records are not hard-deleted | order/item/payment delete-denial Rules test (Emulator) |

## Five-minute demo runbook

### Before the demo

- Configure Firebase using `FIREBASE_SETUP.md` and `PHASE17_SECURITY.md`; deploy reviewed
  Rules/indexes to the demo Firebase project. Never use open test-mode Rules.
- Prepare three accounts: an approved Seller, an Admin account for setup/moderation, and a
  Customer. Seller approval is a one-time pre-demo setup step.
- Seed an active product and unexpired batch owned by the Seller. Create a campaign that
  targets that batch, uses `MOBILE_POINT`, and is approved/`ACTIVE` by Admin. Keep enough
  available batch and campaign target quantity for one purchase.
- Use two logged-in sessions/devices (Seller and Customer), or sign out and switch roles.
  Grant foreground location permission to the Seller device and keep network available.
- Use an internal-test/Play-signed build for release App Check; for a debug demo, register
  that installation's debug token privately. Do not put the token or account passwords in
  screenshots, source control, or this document.

### Live sequence

| Time | Actor | Action and expected result |
| --- | --- | --- |
| 0:00–0:45 | Seller | Open Seller Dashboard → Campaigns → active mobile campaign → Mobile Rescue. Tap update location and grant foreground permission. The latest location shows fresh (≤10 minutes). |
| 0:45–1:30 | Customer | Open Home/Discovery. Find the active rescue campaign, verify mobile/urgency badges and open its detail. Then return to Product/Discovery and select the same seeded product/batch. Stale mobile campaigns should not appear as nearby. |
| 1:30–2:30 | Customer | Add the campaign batch quantity to cart. Checkout as the authenticated Customer using `ON_SITE_RESCUE` and `CASH_ON_SITE`; submit once. Expect one pending order, a payment record, and batch/campaign quantity reserved atomically. |
| 2:30–3:45 | Seller | Seller Dashboard → Orders → open the new order. Move it through `CONFIRMED` → `PREPARING` → `DELIVERED`. On-site fulfillment should not create a Shipment. |
| 3:45–4:15 | Seller + Customer | Verify payment is `PAID`, batch reserved quantity is now sold, campaign reserved quantity is cleared, and rescued quantity/progress increased exactly once. Reopening/replaying `DELIVERED` must not double-commit. |
| 4:15–5:00 | Customer | Browse Home, enable Airplane mode and confirm cached content remains readable with stale/offline state. Restore network, open Discovery, set filters, rotate once and confirm query state/layout survives. Do not attempt checkout while offline. |

Keep a fallback seeded campaign/order available in case location permission, network, or
App Check throttling interrupts the live run. Never fake a successful checkout or edit
Firestore quantities manually during the demonstration.

## Handoff checklist

- Run the clean unit/build command, Android instrumentation smoke test, and Firestore
  Emulator suite; attach logs/results to the release handoff.
- Verify Firebase Console App Check metrics before enabling enforcement. Confirm debug
  tokens are absent from commits and release artifacts.
- Verify Firestore rules/indexes are deployed to the intended Firebase project and that
  `google-services.json` is the matching app config (ignored by Git).
- Demonstrate seller mobile location, authenticated on-site checkout, exactly-once delivered
  inventory/campaign commit, offline read cache, and rotation state restoration.
- Record the trusted-backend requirement for authoritative promotion/aggregate price
  validation and the fact that guest checkout is intentionally disabled.
- Keep final.md and these phase contracts as the source of truth; do not add new behavior
  while preparing the demo.
