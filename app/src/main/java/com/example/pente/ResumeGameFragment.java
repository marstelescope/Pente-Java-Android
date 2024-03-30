/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pente.databinding.FragmentResumeGameBinding;

import java.io.File;
import java.util.ArrayList;


public class ResumeGameFragment extends Fragment {
    private FragmentResumeGameBinding binding;
    private ArrayList<String> listItems=new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentResumeGameBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /**
     Displays all .txt files in listview, sets up on click listener, passes filename in bundle
     to the next fragment
     */
    // https://stackoverflow.com/questions/4540754/how-do-you-dynamically-add-elements-to-a-listview-on-android
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listItems.clear();

        File dir = getActivity().getFilesDir();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(".txt")) {
                    listItems.add(file.getName());
                }
            }
        }

        adapter=new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
        binding.FilesView.setAdapter(adapter);

        binding.FilesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String file = listItems.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("file", file);

                NavHostFragment.findNavController(ResumeGameFragment.this)
                        .navigate(R.id.action_ResumeGameFragment_to_BoardFragment, bundle);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
