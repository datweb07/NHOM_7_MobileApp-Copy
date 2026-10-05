package com.rescuefarm.ui.feed;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.rescuefarm.data.repository.AuthRepositoryFactory;
import com.rescuefarm.data.repository.PostRepositoryFactory;

public class PostViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    public PostViewModelFactory(Context context) { this.context = context.getApplicationContext(); }
    @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(PostViewModel.class)) throw new IllegalArgumentException("Unknown ViewModel class");
        return (T) new PostViewModel(PostRepositoryFactory.create(context), AuthRepositoryFactory.create(context));
    }
}
