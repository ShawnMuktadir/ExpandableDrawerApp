package www.fiberathome.com.parkingapp.ui.privacyPolicy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.termsCondition.TermsCondition;

public class PrivacyPolicyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<TermsCondition> termsConditions;

    public PrivacyPolicyAdapter(Context context, ArrayList<TermsCondition> termsConditions) {
        this.context = context;
        this.termsConditions = termsConditions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.privacy_policy_row, parent, false);
        context = parent.getContext();
        return new PrivacyPolicyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PrivacyPolicyViewHolder privacyPolicyViewHolder = (PrivacyPolicyViewHolder) holder;
        TermsCondition termsCondition = termsConditions.get(position);
        Timber.e("termsConditions adapter-> %s", new Gson().toJson(termsConditions));

        if (!termsCondition.getTermsConditionDomain().equals("")) {
            privacyPolicyViewHolder.tvPrivacyHeader.setText(termsCondition.getTermsConditionDomain());
        } else {
            privacyPolicyViewHolder.tvPrivacyHeader.setVisibility(View.GONE);
        }
        privacyPolicyViewHolder.tvPrivacyHeader.setText(termsCondition.getTermsConditionDomain());
        privacyPolicyViewHolder.tvPrivacyBody.setText(termsCondition.getTermsConditionBody());
    }

    @Override
    public int getItemCount() {
        return termsConditions.size();
    }

    public static class PrivacyPolicyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvPrivacyHeader)
        TextView tvPrivacyHeader;
        @BindView(R.id.tvPrivacyBody)
        TextView tvPrivacyBody;

        public PrivacyPolicyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
