package www.fiberathome.com.parkingapp.ui.settings.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import www.fiberathome.com.parkingapp.databinding.RowSettingBinding;
import www.fiberathome.com.parkingapp.model.Settings;
import www.fiberathome.com.parkingapp.ui.settings.SettingsActivity;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingsViewHolder> {

    private final List<Settings> dataSet;

    private final OnItemClickListener listener;

    public SettingAdapter(List<Settings> dataSet, OnItemClickListener listener) {
        this.dataSet = dataSet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        SettingsActivity context = (SettingsActivity) parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        RowSettingBinding itemBinding = RowSettingBinding.inflate(layoutInflater, parent, false);
        return new SettingsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final SettingsViewHolder viewHolder, final int position) {
        Settings data = dataSet.get(position);

        viewHolder.binding.textViewSettingName.setText(data.getName());

        if (data.getImage() == 0) viewHolder.binding.imageViewSettingIcon.setVisibility(View.INVISIBLE);
        else viewHolder.binding.imageViewSettingIcon.setImageResource(data.getImage());

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(viewHolder.itemView, position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @SuppressLint("NonConstantResourceId")
    public static class SettingsViewHolder extends RecyclerView.ViewHolder {
        RowSettingBinding binding;

        public SettingsViewHolder(RowSettingBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
