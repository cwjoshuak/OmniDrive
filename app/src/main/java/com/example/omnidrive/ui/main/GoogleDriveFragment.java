package com.example.omnidrive.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omnidrive.OneDriveFile;
import com.example.omnidrive.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveFragment extends Fragment {

    private RecyclerView documentView;
    private LinearLayoutManager llm;

    private List<OneDriveFile> baseList = new ArrayList<>((Arrays.asList(
            new OneDriveFile("against AI.doc"),
            new OneDriveFile("euler.java"),
            new OneDriveFile("main.cpp"),
            new OneDriveFile("main1.cpp"),
            new OneDriveFile("main2.cpp"),
            new OneDriveFile("main2-final.cpp"),
            new OneDriveFile("main2-final-final.cpp"),
            new OneDriveFile("main2-final-final-for-sure.cpp"),
            new OneDriveFile("numpy_implementation.py"),
            new OneDriveFile("pride-and-prejudice.txt"),
            new OneDriveFile("stop_words.txt.txt"),
            new OneDriveFile("Academic Transcript.pdf"),
            new OneDriveFile("Copy of Final Review.pdf"),
            new OneDriveFile("IMG_0473-HDR.png"),
            new OneDriveFile("IMG_0474.png"),
            new OneDriveFile("IMG_0475.png"),
            new OneDriveFile("IMG_0477.png"),
            new OneDriveFile("INF 169 HW1.docx"),
            new OneDriveFile("Prime numbers V3.xlsx"),
            new OneDriveFile("Workbook 1.xlsx")
            )));
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.google_drive_fragment, container, false);
        documentView = view.findViewById(R.id.googleDriveRecyclerView);
        llm = new LinearLayoutManager(getActivity());
        documentView.setLayoutManager(llm);
        documentView.setAdapter(new DocumentsAdapter(new ArrayList<>(), null));
        documentView.setVisibility(View.GONE);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        documentView.setVisibility(View.VISIBLE);
        TextView tv = activity.findViewById(R.id.googledriveTextView);
        ImageView iv = activity.findViewById(R.id.googledriveImageView);
        tv.setVisibility(View.GONE);
        iv.setVisibility(View.GONE);

        ((DocumentsAdapter) documentView.getAdapter()).setmData(baseList);
    }
}
