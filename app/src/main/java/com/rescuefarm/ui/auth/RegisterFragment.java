package com.rescuefarm.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.rescuefarm.R;
import com.rescuefarm.domain.enums.UserRole;

public class RegisterFragment extends Fragment {
    private AuthViewModel viewModel;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(
                requireActivity(),
                new AuthViewModelFactory(requireContext())
        ).get(AuthViewModel.class);
        progressBar = view.findViewById(R.id.authProgress);

        EditText fullNameInput = view.findViewById(R.id.fullNameInput);
        EditText emailInput = view.findViewById(R.id.emailInput);
        EditText phoneInput = view.findViewById(R.id.phoneInput);
        EditText passwordInput = view.findViewById(R.id.passwordInput);
        RadioButton sellerRoleButton = view.findViewById(R.id.sellerRoleButton);

        view.findViewById(R.id.createAccountButton).setOnClickListener(unused -> {
            UserRole selectedRole = sellerRoleButton.isChecked()
                    ? UserRole.SELLER
                    : UserRole.CUSTOMER;
            viewModel.register(
                    textOf(emailInput),
                    textOf(passwordInput),
                    textOf(fullNameInput),
                    textOf(phoneInput),
                    selectedRole
            );
        });
        view.findViewById(R.id.backToLoginButton).setOnClickListener(unused ->
                NavHostFragment.findNavController(this).navigateUp()
        );
        viewModel.getScreenState().observe(getViewLifecycleOwner(), state -> renderState(view, state));
    }

    private void renderState(View view, AuthScreenState state) {
        progressBar.setVisibility(
                state.getStatus() == AuthScreenState.Status.LOADING ? View.VISIBLE : View.GONE
        );
        if (state.getStatus() == AuthScreenState.Status.ERROR && state.getMessage() != null) {
            Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            viewModel.clearTransientState();
        } else if (state.getStatus() == AuthScreenState.Status.AUTHENTICATED) {
            viewModel.clearTransientState();
            NavHostFragment.findNavController(this).navigate(
                    R.id.action_registerFragment_to_guestHomeFragment
            );
        }
    }

    private static String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }
}
