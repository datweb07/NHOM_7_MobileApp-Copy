classDiagram

class User {
<<abstract>>
-String id
-String email
-String fullName
-String phone
-String avatarUrl
-UserRole role
-UserStatus status
-double latitude
-double longitude
+updateProfile()
+isActive() boolean
}

class Customer {
-CustomerType customerType
-String companyName
-String taxCode
+addFavorite()
+addToCart()
+checkout()
+review()
}

class Seller {
-String shopName
-String description
-String shopAvatarUrl
-SellerStatus sellerStatus
-double averageRating
+createProduct()
+createCampaign()
+createPost()
+processOrder()
}

class Admin {
+approveSeller()
+approveCampaign()
+moderatePost()
+blockUser()
+resolveReport()
}

User <|-- Customer
User <|-- Seller
User <|-- Admin

class Address {
-String id
-String customerId
-String receiverName
-String phone
-String fullAddress
-double latitude
-double longitude
-boolean isDefault
+setDefault()
}

class SellerApplication {
-String id
-String sellerId
-String representativeName
-String shopName
-String address
-String proofImageUrl
-ApplicationStatus status
-String rejectionReason
+submit()
+approve()
+reject()
}

Customer "1" --> "0..\*" Address : owns
Seller "1" --> "0..1" SellerApplication : submits

class Category {
-String id
-String name
-String imageUrl
-boolean active
+activate()
+deactivate()
}

class Product {
-String id
-String sellerId
-String categoryId
-String campaignId
-String name
-String description
-double originalPrice
-double rescuePrice
-String unit
-String origin
-String province
-List~String~ imageUrls
-double averageRating
-ProductStatus status
+calculateDiscountPercent() double
+isRescueProduct() boolean
+getRescueLabel() String
+calculateFinalPrice(quantity) double
}

class ProductBatch {
-String id
-String productId
-Date harvestDate
-Date expiryDate
-double initialQuantity
-double availableQuantity
-double soldQuantity
-BatchStatus status
+isExpired() boolean
+hasStock(quantity) boolean
+decreaseStock(quantity)
+restoreStock(quantity)
}

Category "1" --> "0.._" Product : classifies
Seller "1" --> "0.._" Product : owns
Product "1" _-- "1.._" ProductBatch : batches

class RescueCampaign {
-String id
-String sellerId
-String title
-String description
-RescueReason rescueReason
-UrgencyLevel urgencyLevel
-double targetQuantity
-double rescuedQuantity
-Date startDate
-Date endDate
-String location
-CampaignStatus status
+calculateProgress() double
+calculateRemaining() double
+calculateUrgency() UrgencyLevel
+getHighlightLabel() String
+submitForApproval()
}

Seller "1" --> "0.._" RescueCampaign : creates
RescueCampaign "0..1" --> "0.._" Product : highlights

class Post {
-String id
-String sellerId
-String campaignId
-String title
-String content
-List~String~ imageUrls
-List~String~ linkedProductIds
-UrgencyLevel urgencyLevel
-PostStatus status
-long viewCount
-Date createdAt
+submitForApproval()
+increaseView()
}

class Reaction {
-String id
-String postId
-String userId
-ReactionType type
+changeType()
}

class Comment {
-String id
-String postId
-String userId
-String content
-CommentStatus status
-Date createdAt
+edit()
+delete()
}

Seller "1" --> "0.._" Post : publishes
RescueCampaign "0..1" --> "0.._" Post : promotedBy
Post "1" _-- "0.._" Reaction : reactions
Post "1" _-- "0.._" Comment : comments
User "1" --> "0.._" Reaction : reacts
User "1" --> "0.._" Comment : writes

class Promotion {
-String id
-String sellerId
-String productId
-PromotionType type
-double value
-Map quantityDiscountTiers
-Date startDate
-Date endDate
-boolean active
+isValid() boolean
+calculateDiscount(quantity, price) double
}

Seller "1" --> "0.._" Promotion : creates
Product "1" --> "0.._" Promotion : receives

class Cart {
-String id
-CartOwnerType ownerType
-String ownerKey
-Date updatedAt
+addItem()
+removeItem()
+calculateSubtotal()
+getItemsBySeller()
}

class CartItem {
-String id
-String productId
-String batchId
-String sellerId
-double quantity
-double unitPrice
+calculateTotal() double
+validateStock() boolean
}

Customer "1" --> "0..1" Cart : owns
Cart "1" _-- "0.._" CartItem : contains
Product "1" --> "0.._" CartItem : references
ProductBatch "1" --> "0.._" CartItem : uses

class Order {
-String id
-OrderOwnerType ownerType
-String ownerId
-String sellerId
-String receiverName
-String receiverPhone
-String receiverAddress
-double receiverLatitude
-double receiverLongitude
-double subtotal
-double quantityDiscount
-double shippingFee
-double totalAmount
-OrderStatus status
-Date createdAt
+calculateTotal()
+canCancel() boolean
}

class OrderItem {
-String id
-String orderId
-String productId
-String batchId
-String productNameSnapshot
-String imageSnapshot
-String unitSnapshot
-double originalPriceSnapshot
-double finalPriceSnapshot
-double quantity
-double subtotal
+calculateSubtotal() double
}

Customer "1" --> "0.._" Order : places
Seller "1" --> "0.._" Order : receives
Order "1" _-- "1.._" OrderItem : contains
Product "1" --> "0..\*" OrderItem : source

class Payment {
-String id
-String orderId
-PaymentMethod method
-PaymentStatus status
-double amount
-String referenceCode
-Date paidAt
+markPending()
+markPaid()
+markFailed()
}

class Shipment {
-String id
-String orderId
-String carrierName
-String trackingCode
-ShipmentStatus status
-double distanceKm
-double shippingFee
+calculateShippingFee()
+updateStatus()
}

Order "1" _-- "1" Payment : payment
Order "1" _-- "0..1" Shipment : shipment

class Review {
-String id
-String customerId
-String productId
-String orderItemId
-int rating
-String content
-List~String~ imageUrls
-ReviewStatus status
+isValid() boolean
}

Customer "1" --> "0.._" Review : writes
Product "1" --> "0.._" Review : receives
OrderItem "1" --> "0..1" Review : verifies

class Favorite {
-String id
-String customerId
-String productId
-Date createdAt
}

Customer "1" --> "0.._" Favorite : owns
Product "1" --> "0.._" Favorite : saved

class Report {
-String id
-String reporterId
-ReportTargetType targetType
-String targetId
-String reason
-String description
-ReportStatus status
-String resolvedBy
-String resolutionNote
+resolve()
+reject()
}

User "1" --> "0.._" Report : submits
Admin "1" --> "0.._" Report : resolves

class Notification {
-String id
-String userId
-NotificationType type
-String title
-String body
-String referenceId
-boolean isRead
+markAsRead()
}

User "1" --> "0..\*" Notification : receives

class Banner {
-String id
-String title
-String imageUrl
-String campaignId
-int displayOrder
-boolean active
+publish()
+unpublish()
}

Admin "1" --> "0.._" Banner : manages
Banner "0.._" --> "0..1" RescueCampaign : promotes
