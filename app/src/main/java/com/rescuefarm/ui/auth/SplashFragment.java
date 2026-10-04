package com.rescuefarm.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.rescuefarm.R;

public class SplashFragment extends Fragment {
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(
                requireActivity(),
                new AuthViewModelFactory(requireContext())
        ).get(AuthViewModel.class);

        viewModel.getScreenState().observe(getViewLifecycleOwner(), this::routeFromState);
        AuthScreenState currentState = viewModel.getScreenState().getValue();
        if (currentState == null || currentState.getStatus() == AuthScreenState.Status.IDLE) {
            viewModel.determineLaunchRoute();
        }
    }

    private void routeFromState(AuthScreenState state) {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() == null
                || navController.getCurrentDestination().getId() != R.id.splashFragment) {
            return;
        }

        int actionId;
        switch (state.getStatus()) {
            case ONBOARDING_REQUIRED:
                actionId = R.id.action_splashFragment_to_onboardingFragment;
                break;
            case AUTHENTICATED:
            case GUEST:
                actionId = R.id.action_splashFragment_to_guestHomeFragment;
                break;
            case LOGIN_REQUIRED:
            case ERROR:
                actionId = R.id.action_splashFragment_to_loginFragment;
                break;
            default:
                return;
        }
        if (state.getStatus() == AuthScreenState.Status.AUTHENTICATED
                || state.getStatus() == AuthScreenState.Status.GUEST) {
            viewModel.clearTransientState();
        }
        navController.navigate(actionId);
    }
}
