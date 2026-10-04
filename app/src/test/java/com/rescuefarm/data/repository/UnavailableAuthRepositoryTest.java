package com.rescuefarm.data.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class UnavailableAuthRepositoryTest {
    @Test
    public void signInWithoutFirebase_returnsSetupError() {
        UnavailableAuthRepository repository = new UnavailableAuthRepository();
        final AuthRepository.ErrorCode[] receivedError = new AuthRepository.ErrorCode[1];

        repository.signIn("customer@example.com", "password", new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(com.rescuefarm.domain.model.User user) { }

            @Override
            public void onError(AuthRepository.ErrorCode errorCode, String message) {
                receivedError[0] = errorCode;
            }
        });

        assertFalse(repository.isAvailable());
        assertEquals(AuthRepository.ErrorCode.NOT_CONFIGURED, receivedError[0]);
    }
}
