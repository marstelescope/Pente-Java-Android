/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;
import static android.view.View.INVISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import com.example.pente.databinding.FragmentBoardBinding;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BoardFragment extends Fragment {
    private FragmentBoardBinding binding;
    private Board board = new Board();
    private Player[] roster = new Player[2];
    private String gameFile;
    private int compScore;
    private int humanScore;
    private int coinTossIndex;
    public BoardFragment(){
        compScore = 0;
        humanScore = 0;
        coinTossIndex = -1;
        gameFile = "";
    }

    /**
     Call to create board buttons, receive bundle from previous fragment,
     load game and current player if information available
     @return view created
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        roster[0] = new Human(board);
        roster[1] = new Computer(board);
        int index = 0;

        binding = FragmentBoardBinding.inflate(inflater, container, false);
        createButtons(inflater);

        Bundle bundle = getArguments();
        String file = bundle.getString("file");
        String newfile = bundle.getString("newfile");
        String firstplayer = bundle.getString("firstplayer");

        if (firstplayer != null){
            if (firstplayer.equals("Computer")){
                coinTossIndex = 1;
            }
            else {
                coinTossIndex = 0;
            }
        }
        if (file != null) {
            gameFile = file;
            BufferedReader reader = null;
            try {
                FileInputStream fis = getActivity().openFileInput(file);
                reader = new BufferedReader(new InputStreamReader(fis));
                board.load(reader);
                index = (board.isCompNext() ? 1 : 0);
                roster[index].setColor(board.isBlackNext() ? 'B' : 'W');
                roster[index ^ 1].setColor(board.isBlackNext() ? 'W' : 'B');

            } catch (IOException e) {
                Log.d("error","File error.");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
        }
        else{
            gameFile = newfile;
        }

        newRound();

        return binding.getRoot();
    }

    /**
     Create board buttons in the board grid layout, set their position tags
     */
    private void createButtons(@NonNull LayoutInflater inflater){
        int buttonSize = getResources().getDimensionPixelSize(R.dimen.button_size);

        for (int row = 0; row < 19; row++) {
            for (int col = 0; col < 19; col++) {

                Button btn = (Button) inflater.inflate(R.layout.button_layout, binding.boardGrid, false);

                String buttonID = String.format(Locale.getDefault(), "%c%d", (char) ('A' + col), 19 - row);
                btn.setTag(buttonID);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = buttonSize;
                params.height = buttonSize;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.setMargins(2, 2, 2, 2);

                btn.setLayoutParams(params);

                binding.boardGrid.addView(btn);
            }
        }
    }

    /**
     Refresh board view display based on board model colors by position
     */
    private void refreshBoard(){
        for (int row = 0; row < 19; row++){
            for (int col = 0; col < 19; col++){
                char c = board.getColorByPos(row, col);
                String buttonID = String.format(Locale.getDefault(), "%c%d", (char) ('A' + col), 19 - row);
                Button btn = binding.boardGrid.findViewWithTag(buttonID);
                if (c == 'W'){
                    btn.setBackgroundColor(Color.WHITE);
                }
                else if (c == 'B'){
                    btn.setBackgroundColor(Color.BLACK);
                }
                else {
                    btn.setBackgroundColor(Color.parseColor("#C9D17E"));
                }
            }
        }
    }

    /**
     Updates text displays by clearing strategy text box and updating captures text box
     */
    private void updateDisplays(){
        binding.strategyView.setText("");
        binding.statsView.setText("Computer captures: " + board.getCompCap()
                                    + "\n\nHuman captures: " + board.getHumanCap());
    }

    /**
     Checks if game is over from the board model, opens end of game dialog if so
     @return true if game is over, false otherwise
     */
    private boolean gameOver(){
        String message = board.gameOver();
        if (!message.isEmpty()){
            showDialog(message);
            return true;
        }
        return false;
    }

    /**
     Displays dialog when game is over with scores and winner, prompts user if
     they want to play another round
     @param message - String, condition that ended the game
     */
    // https://www.youtube.com/watch?v=sp9j0e-Kzc8
    private void showDialog(String message) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.winner_fragment);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView txt = dialog.findViewById(R.id.winnertxt);
        txt.setText(message);

        humanScore += board.getHumanScore();
        compScore += board.getCompScore();
        String winner = "";
        if (board.getHumanScore() > board.getCompScore()) {
            winner = "human player!";
        } else if (board.getHumanScore() < board.getCompScore()) {
            winner = "computer player!";
        }
        TextView scorestxt = dialog.findViewById(R.id.scoresView);
        String announcement = "Round scores:" +
                "\n Human: " + board.getHumanScore() +
                "\n Computer: " + board.getCompScore()  +
                "\n Winner of this round: "+ winner +
                "\n\n Tournament scores: " +
                "\n Human: " + humanScore  +
                "\n Computer: " + compScore;
        String prompt = "\n\n Play another round? ";
        scorestxt.setText(announcement + prompt);

        board.boardInit();
        board.setHumanScore(humanScore);
        board.setCompScore(compScore);

        dialog.findViewById(R.id.yesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRound();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.noButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tournamentwinner = "";
                if (compScore > humanScore) {
                    tournamentwinner = "\n\nThe winner of the tournament is the computer player!";
                } else if (compScore < humanScore) {
                    tournamentwinner = "\n\nThe winner of the tournament is the human player!";
                } else {
                    tournamentwinner = "\n\nThe tournament ends in a draw!";
                }
                scorestxt.setText(announcement + tournamentwinner);
                dialog.findViewById(R.id.yesButton).setVisibility(INVISIBLE);
                Button exitbtn = dialog.findViewById(R.id.noButton);
                exitbtn.setText("Exit");

                exitbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save();
                        dialog.dismiss();
                        NavHostFragment.findNavController(BoardFragment.this)
                                .navigate(R.id.action_BoardFragment_to_MainFragment);
                    }
                });
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     Simulates computer thinking and placing its move, updates text box
     */
    private void compMove(){
        binding.computerMoveView.setText("Computer move...");

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String move = roster[1].makeTurn("");
                String[] words = move.split("\\s+");
                String compMoveTxt =  "The computer placed a " + board.getCompColorName() +
                        " stone on " + words[words.length - 1] + ".\nStrategy: " + move;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        refreshBoard();
                        updateDisplays();
                        if (!gameOver()){
                            binding.computerMoveView.setText(compMoveTxt + "\n\nYour turn to place " + board.getHumanColorName() + " stone.");
                        }
                        else{
                            binding.computerMoveView.setText(compMoveTxt);
                        }

                    }
                });

            }
        }, 1500);

    }

    /**
     Sets on click listeners for all board buttons, as well as help and exit button.
     When board button is clicked, displays and board are updated and refreshed, and
     if game is not over, computer move is called
     */
    // https://technotalkative.com/android-findviewbyid-in-a-loop/
    private void setListeners(){
        for (int row = 0; row < 19; row++){
            for (int col = 0; col < 19; col++){
                String buttonID = String.format(Locale.getDefault(), "%c%d", (char) ('A' + col), 19 - row);
                Button btn = binding.boardGrid.findViewWithTag(buttonID);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String move = roster[0].makeTurn(buttonID);
                        if (!move.isEmpty()){
                            // Print error message
                            binding.computerMoveView.setText(move);
                        }
                        else {

                            refreshBoard();
                            updateDisplays();
                            binding.firstTurnView.setText("");
                            if (!gameOver()) {
                                compMove();
                            }
                        }
                    }
                });
            }
        }

        binding.helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String move = roster[0].suggestMove();
                binding.strategyView.setText("Strategy: " + move);
            }
        });
        binding.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board.setCompScore(compScore);
                board.setHumanScore(humanScore);
                save();
                NavHostFragment.findNavController(BoardFragment.this)
                        .navigate(R.id.action_BoardFragment_to_MainFragment);
            }

        });
    }

    /**
     Sets up file output stream to save board to gameFile
     */
    private void save(){
        try {
            FileOutputStream fos = getActivity().openFileOutput(gameFile, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            board.save(writer);
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     Simulates new round by storing tournament scores locally, checking who plays first,
     and setting button listeners
     */
    private void newRound(){
        int index = 0;
        refreshBoard();
        updateDisplays();

        humanScore = board.getHumanScore();
        compScore = board.getCompScore();

        if (board.countNonEmpty() == 0) {
            if (compScore == humanScore) {
                if (coinTossIndex < 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("file", gameFile);
                    save();
                    NavHostFragment.findNavController(BoardFragment.this)
                            .navigate(R.id.action_BoardFragment_to_CoinTossFragment, bundle);
                } else {
                    index = coinTossIndex;
                }
            } else if (compScore > humanScore) {
                binding.firstTurnView.setText("Computer plays first because the computer has the highest score.");
                index = 1;
            } else {
                binding.firstTurnView.setText("Human plays first because the human has the highest score.");
            }

            // Roster, next player, and color updated
            roster[index].setColor('W');
            roster[index ^ 1].setColor('B');
            board.setCompNext(index == 1);
            board.setBlackNext(false);
        }

        board.setCompScore(0);
        board.setHumanScore(0);

        if (board.isCompNext()){
            compMove();
        }
        else{
            binding.computerMoveView.setText("Your turn to place " + board.getHumanColorName() + " stone.");
        }

        setListeners();
    }
}