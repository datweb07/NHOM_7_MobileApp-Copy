# RescueFarm architecture

`final.md` is the only source of truth for business behavior and scope.

The production dependency direction is:

```text
Activity / Fragment -> ViewModel -> Repository -> Room / Firebase
                                      |
                                      +-> domain services
```

- UI renders state, forwards user events, and never queries Firebase or Room directly.
- ViewModels own screen state and orchestration but not Firestore query implementations.
- Repositories coordinate remote/local data, mapping, and cache policy.
- Inventory and pricing rules live in their dedicated services and domain methods.
- Room is primarily a read cache. Guest Cart is local-only.
- Checkout, payment, approval, confirmation, and inventory mutations must not be faked offline.

Domain objects expose public no-argument constructors where a mapper may need them, but sensitive quantities are not exposed through unconditional public setters. Firestore integration should deserialize into remote DTO/map data inside the repository and then construct domain objects through validated methods. This adds explicit mapping code but prevents Firestore convenience from bypassing inventory and campaign invariants.

The 25 classes under `domain/model` are the complete business model. Room cache entities, repositories, services, ViewModels, and UI classes are infrastructure and do not extend that list. Phase 18 freezes this contract; regression coverage is indexed in [`PHASE18_TESTING_DEMO.md`](PHASE18_TESTING_DEMO.md).
