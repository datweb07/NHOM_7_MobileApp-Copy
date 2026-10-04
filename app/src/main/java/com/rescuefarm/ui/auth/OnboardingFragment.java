package com.rescuefarm.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.rescuefarm.R;

public class OnboardingFragment extends Fragment {
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(
                requireActivity(),
                new AuthViewModelFactory(requireContext())
        ).get(AuthViewModel.class);

        view.findViewById(R.id.getStartedButton).setOnClickListener(unused ->
                viewModel.completeOnboarding()
        );
        view.findViewById(R.id.continueAsGuestButton).setOnClickListener(unused ->
                viewModel.continueAsGuest()
        );
        viewModel.getScreenState().observe(getViewLifecycleOwner(), state -> {
            if (state.getStatus() == AuthScreenState.Status.LOGIN_REQUIRED) {
                NavHostFragment.findNavController(this).navigate(
                        R.id.action_onboardingFragment_to_loginFragment
                );
            } else if (state.getStatus() == AuthScreenState.Status.GUEST) {
                viewModel.clearTransientState();
                NavHostFragment.findNavController(this).navigate(
                        R.id.action_onboardingFragment_to_guestHomeFragment
                );
            }
        });
    }
}
