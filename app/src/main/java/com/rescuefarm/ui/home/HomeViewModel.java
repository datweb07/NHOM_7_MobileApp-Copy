package com.rescuefarm.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rescuefarm.ui.common.UiState;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<UiState<Boolean>> homeState =
            new MutableLiveData<>(UiState.success(true));

    public LiveData<UiState<Boolean>> getHomeState() {
        return homeState;
    }
}
