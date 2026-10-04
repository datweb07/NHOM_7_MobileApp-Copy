package com.rescuefarm.service.media;

import java.util.concurrent.CompletableFuture;

public interface MediaStorage {
    CompletableFuture<String> uploadImage(String localUri);
    CompletableFuture<Void> deleteImage(String remoteUrl);
}
