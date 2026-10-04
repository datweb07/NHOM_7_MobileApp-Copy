package com.rescuefarm.service.auth;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.app.Activity;
import android.os.Bundle;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

public class GoogleCredentialService {
    private static final String WEB_CLIENT_ID_RESOURCE = "default_web_client_id";

    public interface Callback {
        void onIdToken(String idToken);
        void onError(String message);
    }

    public void requestGoogleIdToken(Activity activity, Callback callback) {
        int clientIdResource = activity.getResources().getIdentifier(
                WEB_CLIENT_ID_RESOURCE,
                "string",
                activity.getPackageName()
        );
        if (clientIdResource == 0) {
            callback.onError("Thiếu default_web_client_id. Hãy cập nhật google-services.json sau khi bật Google Sign-In.");
            return;
        }

        String serverClientId = activity.getString(clientIdResource);
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .build();
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        CredentialManager credentialManager = CredentialManager.create(activity);
        credentialManager.getCredentialAsync(
                activity,
                request,
                new CancellationSignal(),
                ContextCompat.getMainExecutor(activity),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleCredential(result.getCredential(), callback);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException exception) {
                        callback.onError("Không thể lấy tài khoản Google. Bạn có thể thử lại hoặc dùng email.");
                    }
                }
        );
    }

    private void handleCredential(Credential credential, Callback callback) {
        if (!(credential instanceof CustomCredential)
                || !TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
            callback.onError("Credential nhận được không phải Google ID token.");
            return;
        }

        try {
            Bundle credentialData = ((CustomCredential) credential).getData();
            GoogleIdTokenCredential googleCredential =
                    GoogleIdTokenCredential.createFrom(credentialData);
            callback.onIdToken(googleCredential.getIdToken());
        } catch (RuntimeException exception) {
            callback.onError("Không thể đọc Google ID token. Hãy cập nhật ứng dụng và thử lại.");
        }
    }
}
