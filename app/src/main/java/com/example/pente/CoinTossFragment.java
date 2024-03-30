/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pente.databinding.FragmentCoinTossBinding;
import com.example.pente.databinding.FragmentNewGameBinding;

import java.io.Console;
import java.util.Random;


public class CoinTossFragment extends Fragment {
    private FragmentCoinTossBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCoinTossBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private String firstplayer;


    /**
     Sets on click listener for button which simulates coin toss and updates button
     to carry out navigation (on second click) with bundle parameters
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        String newfile = bundle.getString("newfile");
        String file = bundle.getString("file");

        binding.coinTossButton.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View view) {
                count +=1;
                if (count == 1){
                    binding.tossResultText.setVisibility(VISIBLE);
                    Random random = new Random();
                    String winner = (random.nextInt(2) == 0 ? "Heads" : "Tails");
                    String guess = (binding.coinSwitch.isChecked() ? "Tails" : "Heads");
                    if (winner.equals(guess)){
                        binding.tossResultText.setText("The coin landed on: " + winner +
                                                            "!\n Human goes first!");
                        firstplayer = "Human";
                    }
                    else {
                        binding.tossResultText.setText("The coin landed on: " + winner +
                                "!\n Computer goes first!");
                        firstplayer = "Computer";
                    }
                    binding.coinTossButton.setText("Start game");
                }
                if (count == 2) {
                    binding.coinTossButton.setText("");
                    binding.tossResultText.setVisibility(View.INVISIBLE);
                    Bundle bundle = new Bundle();
                    if (newfile != null) {
                        bundle.putString("newfile", newfile);
                    }
                    else if (file != null){
                        bundle.putString("file", file);
                    }
                    bundle.putString("firstplayer", firstplayer);

                    NavHostFragment.findNavController(CoinTossFragment.this)
                            .navigate(R.id.action_CoinTossFragment_to_BoardFragment, bundle);
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
