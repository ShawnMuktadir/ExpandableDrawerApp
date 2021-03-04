package www.fiberathome.com.parkingapp.ui.settings.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.Language;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.SettingsViewHolder> {

    private final List<Language> dataSet;

    private final OnItemClickListener listener;

    public LanguageAdapter(List<Language> dataSet, OnItemClickListener listener) {
        this.dataSet = dataSet;
        this.listener = listener;
    }

    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_language_setting, parent, false);

        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SettingsViewHolder viewHolder, final int position) {
        Language data = dataSet.get(position);

        viewHolder.textViewLanguageName.setText(data.getName());
        viewHolder.textViewSubLanguageName.setText(data.getSubName());

        if (!data.isSelected()) viewHolder.imageViewCheckedIcon.setVisibility(View.GONE);

        viewHolder.itemView.setOnClickListener(v -> {
            listener.onItemClick(data);
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @SuppressLint("NonConstantResourceId")
    public static class SettingsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_language_name)
        TextView textViewLanguageName;

        @BindView(R.id.text_view_sub_language_name)
        TextView textViewSubLanguageName;

        @BindView(R.id.image_view_checked_icon)
        ImageView imageViewCheckedIcon;

        public SettingsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Language language);
    }
}
