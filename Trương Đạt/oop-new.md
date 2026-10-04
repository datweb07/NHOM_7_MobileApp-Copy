classDiagram

class User {
<<abstract>>
-String id
-String email
-UserRole role
-UserStatus status
+updateProfile()
+isActive() boolean
}

class Customer {
+addToCart()
+checkout()
+review()
}

class Seller {
+createProduct()
+createCampaign()
+processOrder()
}

class Admin {
+approveCampaign()
+moderateContent()
+blockUser()
}

User <|-- Customer
User <|-- Seller
User <|-- Admin

class Product {
-double originalPrice
-double rescuePrice
-ProductStatus status
+calculateFinalPrice(quantity) double
+getRescueLabel() String
}

class ProductBatch {
-double availableQuantity
-Date expiryDate
+hasStock(quantity) boolean
+decreaseStock(quantity)
+restoreStock(quantity)
}

Product "1" _-- "1.._" ProductBatch

class RescueCampaign {
-RescueReason rescueReason
-UrgencyLevel urgencyLevel
-double targetQuantity
-double rescuedQuantity
-Date endDate
+calculateProgress() double
+calculateUrgency() UrgencyLevel
+getHighlightLabel() String
}

RescueCampaign "0..1" --> "0..\*" Product

class Promotion {
-PromotionType type
-Map quantityDiscountTiers
+calculateDiscount(quantity, price) double
}

Product "1" --> "0..\*" Promotion

class Cart {
+addItem()
+removeItem()
+getItemsBySeller()
+calculateSubtotal()
}

class CartItem {
-double quantity
-double unitPrice
+calculateTotal() double
}

Cart "1" _-- "0.._" CartItem

class Order {
-String sellerId
-OrderStatus status
-double totalAmount
+calculateTotal()
+canCancel() boolean
}

class OrderItem {
-String productNameSnapshot
-double finalPriceSnapshot
-double quantity
+calculateSubtotal() double
}

Order "1" _-- "1.._" OrderItem

class Payment {
-PaymentMethod method
-PaymentStatus status
+markPaid()
+markFailed()
}

class Shipment {
-ShipmentStatus status
-double distanceKm
+calculateShippingFee()
+updateStatus()
}

Order "1" _-- "1" Payment
Order "1" _-- "0..1" Shipment

class Repository {
<<interface>>
+getAll()
+getById(id)
+save(entity)
+update(entity)
}

class FirebaseRepository {
+getAll()
+getById(id)
+save(entity)
+update(entity)
}

Repository <|.. FirebaseRepository

class ImageStorageService {
<<interface>>
+uploadImage(uri)
+deleteImage(url)
}

class CloudinaryImageService {
+uploadImage(uri)
+deleteImage(url)
}

ImageStorageService <|.. CloudinaryImageService
