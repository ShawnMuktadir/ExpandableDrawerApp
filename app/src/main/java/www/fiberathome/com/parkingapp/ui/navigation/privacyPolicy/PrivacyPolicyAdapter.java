package www.fiberathome.com.parkingapp.ui.navigation.privacyPolicy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.termsCondition.TermsCondition;
import www.fiberathome.com.parkingapp.databinding.PrivacyPolicyRowBinding;

public class PrivacyPolicyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private final ArrayList<TermsCondition> termsConditions;

    private boolean isTextViewClicked = false;

    public PrivacyPolicyAdapter(Context context, ArrayList<TermsCondition> termsConditions) {
        this.context = context;
        this.termsConditions = termsConditions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        PrivacyPolicyRowBinding itemBinding = PrivacyPolicyRowBinding.inflate(layoutInflater, parent, false);
        return new PrivacyPolicyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PrivacyPolicyViewHolder privacyPolicyViewHolder = (PrivacyPolicyViewHolder) holder;

        TermsCondition termsCondition = termsConditions.get(position);
        Timber.e("termsConditions adapter-> %s", new Gson().toJson(termsConditions));

        privacyPolicyViewHolder.binding.tvPrivacyHeader.setText(termsCondition.getTitle());
        privacyPolicyViewHolder.binding.tvPrivacyBody.setText(termsCondition.getDescription());
        privacyPolicyViewHolder.binding.tvPrivacyBody.setOnClickListener(v -> {
            if (isTextViewClicked) {
                //This will shrink textview to 2 lines if it is expanded.
                privacyPolicyViewHolder.binding.tvPrivacyBody.setMaxLines(2);
                isTextViewClicked = false;
            } else {
                //This will expand the textview if it is of 2 lines
                privacyPolicyViewHolder.binding.tvPrivacyBody.setMaxLines(Integer.MAX_VALUE);
                isTextViewClicked = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return termsConditions.size();
    }

    @SuppressLint("NonConstantResourceId")
    public static class PrivacyPolicyViewHolder extends RecyclerView.ViewHolder {

        PrivacyPolicyRowBinding binding;

        public PrivacyPolicyViewHolder(PrivacyPolicyRowBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
