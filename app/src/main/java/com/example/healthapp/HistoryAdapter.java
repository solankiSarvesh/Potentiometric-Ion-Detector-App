package com.example.healthapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(int recordId);
    }

    private List<HistoryRecord> historyList;
    private OnDeleteClickListener deleteClickListener;

    public HistoryAdapter(List<HistoryRecord> historyList, OnDeleteClickListener listener) {
        this.historyList = historyList;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryRecord record = historyList.get(position);

        holder.tvPatientName.setText("Patient: " + record.getPatientName());
        holder.tvSampleDate.setText("Sample ID: " + record.getSampleId() + " | Date: " + record.getDate());
        holder.tvValues.setText(String.format("K⁺: %.2f, Na⁺: %.2f, pH: %.2f",
                record.getPotassium(), record.getSodium(), record.getPh()));
        holder.tvResult.setText("Result: " + record.getResult());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(record.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvPatientName, tvSampleDate, tvValues, tvResult;
        ImageButton btnDelete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvSampleDate = itemView.findViewById(R.id.tvSampleDate);
            tvValues = itemView.findViewById(R.id.tvValues);
            tvResult = itemView.findViewById(R.id.tvResult);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
