package com.example.pente;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pente.databinding.FragmentNewGameBinding;

public class NewGameFragment extends Fragment {

    private FragmentNewGameBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentNewGameBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /**
     Sets on click listener to start button, ensures filename isn't an empty string
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable inputText = binding.InputBox.getText();
                if (inputText != null && !inputText.toString().trim().equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("newfile", inputText.toString().trim());

                    NavHostFragment.findNavController(NewGameFragment.this)
                            .navigate(R.id.action_NewGameFragment_to_CoinTossFragment, bundle);
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}