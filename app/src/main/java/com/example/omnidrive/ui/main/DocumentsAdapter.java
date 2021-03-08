package com.example.omnidrive.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omnidrive.OneDriveFile;
import com.example.omnidrive.R;

import java.util.List;


public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.ViewHolder> {

    private List<OneDriveFile> mData;
    private OneDriveFragment.OnItemClickListener listener;
    DocumentsAdapter(List<OneDriveFile> data, OneDriveFragment.OnItemClickListener listener) {
        this.mData = data;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;
        OneDriveFile file;
        ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.docName);
            iv = itemView.findViewById(R.id.docImg);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.document_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OneDriveFile file = mData.get(position);
        if (!file.isBackDirectory)
            holder.tv.setText(file.name);
        else {
            holder.tv.setText("...");
        }
        System.out.println(holder.itemView);

        holder.file = file;
        if (!file.isFile()) {
            holder.iv.setImageResource(R.drawable.folder);
            System.out.println(holder.itemView.isClickable());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(file);
                }
            });
        } else {
            holder.iv.setImageResource(R.drawable.file);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setmData(List<OneDriveFile> mData) {
        this.mData = mData;
    }
}