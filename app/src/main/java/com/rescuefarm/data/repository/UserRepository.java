package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.User;

public interface UserRepository {
    LiveData<User> observeUser(String userId);
}
