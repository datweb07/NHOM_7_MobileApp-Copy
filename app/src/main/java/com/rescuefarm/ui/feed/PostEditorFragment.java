package com.rescuefarm.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.textfield.TextInputEditText;
import com.rescuefarm.R;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.Post;
import java.util.Arrays;

public class PostEditorFragment extends Fragment {
    private PostViewModel viewModel; private TextInputEditText title, content, campaign, images, products;
    private Spinner urgency; private String postId = ""; private boolean populated;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_post_editor, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        title = view.findViewById(R.id.postTitleInput); content = view.findViewById(R.id.postContentInput);
        campaign = view.findViewById(R.id.postCampaignInput); images = view.findViewById(R.id.postImagesInput);
        products = view.findViewById(R.id.postProductsInput); urgency = view.findViewById(R.id.postUrgencySpinner);
        urgency.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(UrgencyLevel.values())));
        viewModel = new ViewModelProvider(this, new PostViewModelFactory(requireContext())).get(PostViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), value -> {
            if (value.getStatus() == PostScreenState.Status.POST && !populated) populate(value.getPost());
            else if (value.getStatus() == PostScreenState.Status.SAVED) {
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigateUp();
            } else if (value.getStatus() == PostScreenState.Status.ERROR) {
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        view.findViewById(R.id.savePostDraftButton).setOnClickListener(v -> save(false));
        view.findViewById(R.id.submitPostButton).setOnClickListener(v -> save(true));
        postId = getArguments() == null ? "" : getArguments().getString("postId", "");
        if (!postId.isEmpty()) viewModel.loadPost(postId);
    }
    private void populate(Post post) {
        populated = true; title.setText(post.getTitle()); content.setText(post.getContent());
        campaign.setText(post.getCampaignId()); images.setText(String.join("\n", post.getImageUrls()));
        products.setText(String.join("\n", post.getLinkedProductIds())); urgency.setSelection(post.getUrgencyLevel().ordinal());
    }
    private void save(boolean submit) {
        viewModel.savePost(postId, text(campaign), text(title), text(content), text(images),
                text(products), (UrgencyLevel) urgency.getSelectedItem(), submit);
    }
    private static String text(TextInputEditText input) { return input.getText() == null ? "" : input.getText().toString(); }
}
