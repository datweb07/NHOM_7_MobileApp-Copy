package com.rescuefarm.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.rescuefarm.R;

public class GuestHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_guest_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView foundationMessage = view.findViewById(R.id.foundationMessage);
        HomeViewModel viewModel = new ViewModelProvider(
                this,
                new HomeViewModelFactory(requireContext())
        ).get(HomeViewModel.class);

        viewModel.getHomeState().observe(getViewLifecycleOwner(), state -> {
            if (state.getMessage() != null) {
                foundationMessage.setText(state.getMessage());
            } else {
                foundationMessage.setText(R.string.foundation_message);
            }
        });

        View loginButton = view.findViewById(R.id.loginButton);
        View profileButton = view.findViewById(R.id.profileButton);
        loginButton.setVisibility(viewModel.isAuthenticated() ? View.GONE : View.VISIBLE);
        profileButton.setVisibility(viewModel.isAuthenticated() ? View.VISIBLE : View.GONE);

        loginButton.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                        R.id.action_guestHomeFragment_to_loginFragment
                )
        );
        profileButton.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                        R.id.action_guestHomeFragment_to_profileFragment
                )
        );
        view.findViewById(R.id.catalogButton).setOnClickListener(
                Navigation.createNavigateOnClickListener(
                        R.id.action_guestHomeFragment_to_productListFragment
                )
        );
    }
}
