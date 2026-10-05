# Phase 16 — Responsive layout and state restoration

## Setup

No service configuration or dependency change is needed. Rotate the emulator/device or
use Android Studio's **Don't keep activities** developer option to exercise Activity
recreation and process restoration.

## Layouts

Landscape resources are provided for Discovery, Product Detail, and Checkout. Other
screens keep their scrollable portrait layout, which remains usable at short landscape
heights. Landscape spacing is reduced in `values-land/dimens.xml`.

## State and duplicate actions

- Discovery filter values are stored in `SavedStateHandle`; text and spinner widgets also
  retain their Android view state.
- Checkout keeps a stable idempotency request ID in `SavedStateHandle`, ignores a second
  submit while a request is running, and consumes success navigation only once.
- Form input and spinner selections use their view IDs and native view-state restoration.
- Password fields are not copied into `SavedStateHandle`.
- Home refresh ignores another refresh call while its existing request set is running.

## Manual check

1. Open Discovery, enter a keyword, choose a category/reason/sort, apply, then rotate.
   Confirm inputs, selections, and results remain consistent.
2. Open Checkout, fill receiver fields and choose fulfillment/payment, then rotate before
   submit. Confirm the form and selections remain.
3. Submit once, rotate while loading, and confirm the request is not started again and
   success navigates to confirmation only once.
4. Rotate Home, Product Detail, and the other long forms. Confirm each screen remains
   scrollable and no controls overlap at landscape height.
