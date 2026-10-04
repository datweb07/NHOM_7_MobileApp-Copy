package com.rescuefarm.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rescuefarm.ui.common.UiState;
import com.rescuefarm.data.repository.AuthRepository;

public class HomeViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<UiState<Boolean>> homeState =
            new MutableLiveData<>(UiState.success(true));

    public HomeViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<UiState<Boolean>> getHomeState() {
        return homeState;
    }

    public boolean isAuthenticated() { return authRepository.isAuthenticated(); }
}
