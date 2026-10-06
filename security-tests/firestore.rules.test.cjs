const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const assert = require('node:assert/strict');
const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require('@firebase/rules-unit-testing');
const {
  collection,
  deleteDoc,
  doc,
  getDoc,
  getDocs,
  runTransaction,
  serverTimestamp,
  setDoc,
  updateDoc,
  Timestamp,
} = require('firebase/firestore');

const projectId = 'demo-rescuefarm-security';
let environment;

const customerOrder = (overrides = {}) => ({
  id: 'order-customer-1', requestId: 'order-customer-1', ownerType: 'CUSTOMER',
  ownerId: 'customer-1', sellerId: 'seller-1', campaignId: '', orderCode: 'RFABC123',
  fulfillmentType: 'PICKUP', receiverName: 'Customer One', receiverPhone: '0900000000',
  receiverAddress: '', receiverLatitude: 0, receiverLongitude: 0, subtotal: 20,
  quantityDiscount: 0, shippingFee: 0, totalAmount: 20, paymentMethod: 'CASH_ON_SITE',
  paymentStatus: 'UNPAID', status: 'PENDING', note: '', campaignReservedQuantity: 0,
  batchReservations: { 'batch-1': 2 }, inventoryFinalized: false, finalizedStatus: '',
  createdAt: serverTimestamp(), updatedAt: serverTimestamp(), ...overrides,
});

test.before(async () => {
  environment = await initializeTestEnvironment({
    projectId,
    firestore: { rules: fs.readFileSync(path.join(__dirname, '..', 'firestore.rules'), 'utf8') },
  });
});

test.beforeEach(async () => {
  await environment.clearFirestore();
  await environment.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    const now = Timestamp.now();
    await setDoc(doc(db, 'users/customer-1'), {
      id: 'customer-1', email: 'one@example.test', fullName: 'Customer One', phone: '0900000001',
      avatarUrl: '', role: 'CUSTOMER', status: 'ACTIVE', customerType: 'INDIVIDUAL',
      latitude: 0, longitude: 0, createdAt: now, updatedAt: now,
    });
    await setDoc(doc(db, 'users/customer-2'), {
      id: 'customer-2', email: 'two@example.test', fullName: 'Customer Two', phone: '0900000002',
      avatarUrl: '', role: 'CUSTOMER', status: 'ACTIVE', customerType: 'INDIVIDUAL',
      latitude: 0, longitude: 0, createdAt: now, updatedAt: now,
    });
    await setDoc(doc(db, 'users/seller-1'), {
      id: 'seller-1', role: 'SELLER', status: 'ACTIVE', sellerStatus: 'APPROVED',
    });
    await setDoc(doc(db, 'categories/category-1'), { id: 'category-1', active: true });
    await setDoc(doc(db, 'products/product-1'), {
      id: 'product-1', sellerId: 'seller-1', categoryId: 'category-1', name: 'Fresh produce',
      description: '', originalPrice: 15, rescuePrice: 10, unit: 'kg', origin: '', province: '',
      imageUrls: [], averageRating: 0, reviewCount: 0, status: 'ACTIVE',
      createdAt: now, updatedAt: now,
    });
    await setDoc(doc(db, 'productBatches/batch-1'), {
      id: 'batch-1', productId: 'product-1', activeCampaignId: null,
      harvestDate: Timestamp.fromMillis(now.toMillis() - 1000),
      expiryDate: Timestamp.fromMillis(now.toMillis() + 86400000),
      initialQuantity: 5, availableQuantity: 5, reservedQuantity: 0, soldQuantity: 0,
      status: 'AVAILABLE', inventoryVersion: 1, createdAt: now, updatedAt: now,
    });
    await setDoc(doc(db, 'orders/guest-order'), customerOrder({
      id: 'guest-order', requestId: 'guest-order', ownerType: 'GUEST', ownerId: 'guest-local-id',
    }));
    await setDoc(doc(db, 'orders/existing-customer-order'), customerOrder({
      id: 'existing-customer-order', requestId: 'existing-customer-order',
    }));
    await setDoc(doc(db, 'orders/existing-customer-order/items/batch-1'), {
      id: 'batch-1', orderId: 'existing-customer-order', batchId: 'batch-1',
    });
    await setDoc(doc(db, 'payments/existing-customer-order'), {
      id: 'existing-customer-order', orderId: 'existing-customer-order', amount: 20,
    });
  });
});

test.after(async () => environment.cleanup());

test('guest orders and nested payment/shipment data are not publicly readable', async () => {
  const anonymous = environment.unauthenticatedContext().firestore();
  await assertFails(getDoc(doc(anonymous, 'orders/guest-order')));
  await assertFails(getDocs(collection(anonymous, 'orders')));
  await assertFails(getDoc(doc(anonymous, 'payments/guest-order')));
  await assertFails(getDoc(doc(anonymous, 'shipments/guest-order')));
});

test('customers can only read their own orders', async () => {
  const owner = environment.authenticatedContext('customer-1').firestore();
  const other = environment.authenticatedContext('customer-2').firestore();
  await assertSucceeds(getDoc(doc(owner, 'orders/existing-customer-order')));
  await assertFails(getDoc(doc(other, 'orders/existing-customer-order')));
});

test('guest cannot create an unauthenticated order or inventory reservation', async () => {
  const anonymous = environment.unauthenticatedContext().firestore();
  const orderRef = doc(anonymous, 'orders/forged-guest-order');
  await assertFails(setDoc(orderRef, customerOrder({
    id: 'forged-guest-order', requestId: 'forged-guest-order', ownerType: 'GUEST',
    ownerId: 'any-client-selected-id',
  })));
});

test('valid customer checkout atomically creates order and reserves owned batch', async () => {
  const db = environment.authenticatedContext('customer-1').firestore();
  const orderId = 'valid-order-1';
  await assertSucceeds(runTransaction(db, async (transaction) => {
    transaction.set(doc(db, `orders/${orderId}`), customerOrder({
      id: orderId, requestId: orderId,
    }));
    transaction.set(doc(db, `orders/${orderId}/items/batch-1`), {
      id: 'batch-1', orderId, batchId: 'batch-1', productId: 'product-1',
      productNameSnapshot: 'Fresh produce', productImageSnapshot: '', unitSnapshot: 'kg',
      originalPriceSnapshot: 15, rescuePriceSnapshot: 10, finalPriceSnapshot: 10,
      quantity: 2, subtotal: 20,
    });
    transaction.set(doc(db, `payments/${orderId}`), {
      id: orderId, orderId, method: 'CASH_ON_SITE', status: 'UNPAID', amount: 20,
      referenceCode: '', paidAt: null, createdAt: serverTimestamp(), updatedAt: serverTimestamp(),
    });
    transaction.update(doc(db, 'productBatches/batch-1'), {
      availableQuantity: 3, reservedQuantity: 2, status: 'RESERVED_PARTIAL',
      inventoryVersion: 2, lastReservationOrderId: orderId,
      lastReservationQuantity: 2, updatedAt: serverTimestamp(),
    });
  }));
});

test('checkout cannot reserve a batch that belongs to a different seller', async () => {
  const db = environment.authenticatedContext('customer-1').firestore();
  await assertFails(runTransaction(db, async (transaction) => {
    transaction.set(doc(db, 'orders/forged-cross-seller-order'), customerOrder({
      id: 'forged-cross-seller-order', requestId: 'forged-cross-seller-order',
      sellerId: 'seller-other',
    }));
    transaction.set(doc(db, 'orders/forged-cross-seller-order/items/batch-1'), {
      id: 'batch-1', orderId: 'forged-cross-seller-order', batchId: 'batch-1', productId: 'product-1',
      productNameSnapshot: 'Fresh produce', productImageSnapshot: '', unitSnapshot: 'kg',
      originalPriceSnapshot: 15, rescuePriceSnapshot: 10, finalPriceSnapshot: 10,
      quantity: 2, subtotal: 20,
    });
    transaction.set(doc(db, 'payments/forged-cross-seller-order'), {
      id: 'forged-cross-seller-order', orderId: 'forged-cross-seller-order',
      method: 'CASH_ON_SITE', status: 'UNPAID', amount: 20, referenceCode: '', paidAt: null,
      createdAt: serverTimestamp(), updatedAt: serverTimestamp(),
    });
    transaction.update(doc(db, 'productBatches/batch-1'), {
      availableQuantity: 3, reservedQuantity: 2, status: 'RESERVED_PARTIAL',
      inventoryVersion: 2, lastReservationOrderId: 'forged-cross-seller-order',
      lastReservationQuantity: 2, updatedAt: serverTimestamp(),
    });
  }));
});

test('seller cannot self-approve their role', async () => {
  const seller = environment.authenticatedContext('seller-1').firestore();
  await assertFails(updateDoc(doc(seller, 'users/seller-1'), {
    sellerStatus: 'APPROVED',
  }));
});

test('seller cannot fabricate an inventory reservation outside checkout', async () => {
  const seller = environment.authenticatedContext('seller-1').firestore();
  await assertFails(updateDoc(doc(seller, 'productBatches/batch-1'), {
    availableQuantity: 4, reservedQuantity: 1, status: 'RESERVED_PARTIAL',
    inventoryVersion: 2, updatedAt: serverTimestamp(),
  }));
});

test('customer cannot write admin moderation metadata to their profile', async () => {
  const customer = environment.authenticatedContext('customer-1').firestore();
  await assertFails(updateDoc(doc(customer, 'users/customer-1'), {
    moderatedBy: 'customer-1', moderatedAt: serverTimestamp(),
    moderationReason: 'self-approved', updatedAt: serverTimestamp(),
  }));
});

test('transactional order, item, and payment records cannot be hard-deleted', async () => {
  const customer = environment.authenticatedContext('customer-1').firestore();
  await assertFails(deleteDoc(doc(customer, 'orders/existing-customer-order')));
  await assertFails(deleteDoc(doc(customer, 'orders/existing-customer-order/items/batch-1')));
  await assertFails(deleteDoc(doc(customer, 'payments/existing-customer-order')));
});

test('unknown and credential-like product fields are rejected', async () => {
  const seller = environment.authenticatedContext('seller-1').firestore();
  await assertFails(setDoc(doc(seller, 'products/product-secret'), {
    id: 'product-secret', sellerId: 'seller-1', categoryId: 'category-1', name: 'Fresh produce',
    description: '', originalPrice: 15, rescuePrice: 10, unit: 'kg', origin: '', province: '',
    imageUrls: [], averageRating: 0, reviewCount: 0, status: 'ACTIVE',
    password: 'should-never-be-stored', createdAt: serverTimestamp(), updatedAt: serverTimestamp(),
  }));
});
