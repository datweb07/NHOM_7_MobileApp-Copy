package com.rescuefarm.ui.admin;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.rescuefarm.data.repository.AdminRepositoryFactory;

public final class AdminViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    public AdminViewModelFactory(Context context) { this.context = context.getApplicationContext(); }
    @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AdminViewModel(AdminRepositoryFactory.create(context));
    }
}
