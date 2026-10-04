package com.rescuefarm.data.remote.firebase;

import androidx.lifecycle.LiveData;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.rescuefarm.data.repository.UserRepository;
import com.rescuefarm.domain.enums.CustomerType;
import com.rescuefarm.domain.enums.UserRole;
import com.rescuefarm.domain.model.Admin;
import com.rescuefarm.domain.model.Customer;
import com.rescuefarm.domain.model.Seller;
import com.rescuefarm.domain.model.User;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUserRepository implements UserRepository {
    private static final String USERS_COLLECTION = "users";
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String PENDING_SELLER_STATUS = "PENDING_APPROVAL";

    private final FirebaseFirestore firestore;

    public FirebaseUserRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public LiveData<User> observeUser(String userId) {
        return new LiveData<User>() {
            private ListenerRegistration registration;

            @Override
            protected void onActive() {
                registration = firestore.collection(USERS_COLLECTION)
                        .document(userId)
                        .addSnapshotListener((snapshot, exception) -> {
                            if (exception != null || snapshot == null || !snapshot.exists()) {
                                postValue(null);
                                return;
                            }
                            if (!ACTIVE_STATUS.equals(snapshot.getString("status"))) {
                                postValue(null);
                                return;
                            }
                            postValue(mapActiveUser(snapshot));
                        });
            }

            @Override
            protected void onInactive() {
                if (registration != null) {
                    registration.remove();
                    registration = null;
                }
            }
        };
    }

    @Override
    public void getUser(String userId, UserCallback callback) {
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        callback.onError(ProfileError.NOT_FOUND, "Không tìm thấy hồ sơ users.");
                        return;
                    }
                    if (!ACTIVE_STATUS.equals(snapshot.getString("status"))) {
                        callback.onError(ProfileError.DISABLED, "Tài khoản đã bị vô hiệu hóa.");
                        return;
                    }
                    User user = mapActiveUser(snapshot);
                    if (user == null) {
                        callback.onError(ProfileError.UNKNOWN, "Hồ sơ người dùng có role không hợp lệ.");
                        return;
                    }
                    callback.onSuccess(user);
                })
                .addOnFailureListener(exception -> notifyFailure(exception, callback));
    }

    @Override
    public void createRegistrationProfile(
            String userId,
            String email,
            String fullName,
            String phone,
            String avatarUrl,
            UserRole role,
            UserCallback callback
    ) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userId);
        profile.put("email", email == null ? "" : email);
        profile.put("fullName", fullName.trim());
        profile.put("phone", phone == null ? "" : phone.trim());
        profile.put("avatarUrl", avatarUrl == null ? "" : avatarUrl);
        profile.put("role", role.name());
        profile.put("status", ACTIVE_STATUS);
        profile.put("createdAt", FieldValue.serverTimestamp());
        profile.put("updatedAt", FieldValue.serverTimestamp());
        if (role == UserRole.SELLER) {
            profile.put("sellerStatus", PENDING_SELLER_STATUS);
        }

        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .set(profile)
                .addOnSuccessListener(unused -> callback.onSuccess(
                        createDomainUser(userId, email, fullName, role)
                ))
                .addOnFailureListener(exception -> notifyFailure(exception, callback));
    }

    private User mapActiveUser(DocumentSnapshot snapshot) {
        String roleValue = snapshot.getString("role");
        UserRole role;
        try {
            role = UserRole.valueOf(roleValue == null ? "" : roleValue);
        } catch (IllegalArgumentException exception) {
            return null;
        }
        String email = snapshot.getString("email");
        String fullName = snapshot.getString("fullName");
        return createDomainUser(
                snapshot.getId(),
                email == null ? "" : email,
                fullName == null ? "Người dùng RescueFarm" : fullName,
                role
        );
    }

    private User createDomainUser(String id, String email, String fullName, UserRole role) {
        if (role == UserRole.ADMIN) { return new Admin(id, email, fullName); }
        if (role == UserRole.SELLER) { return new Seller(id, email, fullName, fullName); }
        return new Customer(id, email, fullName, CustomerType.INDIVIDUAL);
    }

    private void notifyFailure(Exception exception, UserCallback callback) {
        if (exception instanceof FirebaseNetworkException
                || (exception instanceof FirebaseFirestoreException
                && ((FirebaseFirestoreException) exception).getCode()
                == FirebaseFirestoreException.Code.UNAVAILABLE)) {
            callback.onError(ProfileError.NETWORK, "Không thể kết nối Firestore.");
            return;
        }
        callback.onError(ProfileError.UNKNOWN, "Không thể đọc hoặc ghi hồ sơ người dùng.");
    }
}
