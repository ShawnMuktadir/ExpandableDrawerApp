package www.fiberathome.com.parkingapp.ui.navigation.law;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.fiberathome.com.parkingapp.data.model.response.law.LawData;
import www.fiberathome.com.parkingapp.databinding.RowLawBinding;

public class LawAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private ArrayList<LawData> lawDataArrayList;

    private boolean isTextViewClicked = false;

    public LawAdapter(Context context, ArrayList<LawData> lawDataArrayList) {
        this.context = context;
        this.lawDataArrayList = lawDataArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        RowLawBinding itemBinding = RowLawBinding.inflate(layoutInflater, parent, false);
        return new LawViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LawViewHolder lawViewHolder = (LawViewHolder) holder;
        LawData lawData = lawDataArrayList.get(position);

        lawViewHolder.binding.tvLawHeader.setText(lawData.getTitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lawViewHolder.binding.tvLawBody.setText(Html.fromHtml(lawData.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            lawViewHolder.binding.tvLawBody.setText(Html.fromHtml(lawData.getDescription()));
        }

        lawViewHolder.binding.tvLawBody.setOnClickListener(v -> {
            if (isTextViewClicked) {
                //This will shrink textview to 2 lines if it is expanded.
                lawViewHolder.binding.tvLawBody.setMaxLines(2);
                isTextViewClicked = false;
            } else {
                //This will expand the textview if it is of 2 lines
                lawViewHolder.binding.tvLawBody.setMaxLines(Integer.MAX_VALUE);
                isTextViewClicked = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return lawDataArrayList.size();
    }

    //called by law fragment
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<LawData> filteredList) {
        lawDataArrayList = filteredList;
        notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    public static class LawViewHolder extends RecyclerView.ViewHolder {

        RowLawBinding binding;

        public LawViewHolder(RowLawBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
