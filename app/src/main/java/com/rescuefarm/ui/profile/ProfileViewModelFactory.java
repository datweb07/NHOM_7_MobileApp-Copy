package com.rescuefarm.ui.profile;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.rescuefarm.data.repository.AuthRepositoryFactory;
import com.rescuefarm.data.repository.UserRepositoryFactory;
import com.rescuefarm.service.location.LocationProviderFactory;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    public ProfileViewModelFactory(Context context) { this.context = context.getApplicationContext(); }

    @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(ProfileViewModel.class)) {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
        return (T) new ProfileViewModel(AuthRepositoryFactory.create(context),
                UserRepositoryFactory.create(context), LocationProviderFactory.create(context));
    }
}
