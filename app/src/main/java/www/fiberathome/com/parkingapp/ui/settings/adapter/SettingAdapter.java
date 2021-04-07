package www.fiberathome.com.parkingapp.ui.settings.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.Settings;

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_setting, parent, false);

        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SettingsViewHolder viewHolder, final int position) {
        Settings data = dataSet.get(position);

        viewHolder.textViewName.setText(data.getName());

        if (data.getImage() == 0) viewHolder.imageViewIcon.setVisibility(View.INVISIBLE);
        else viewHolder.imageViewIcon.setImageResource(data.getImage());

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(viewHolder.itemView, position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @SuppressLint("NonConstantResourceId")
    public static class SettingsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_setting_name)
        TextView textViewName;

        @BindView(R.id.image_view_setting_icon)
        ImageView imageViewIcon;

        public SettingsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
