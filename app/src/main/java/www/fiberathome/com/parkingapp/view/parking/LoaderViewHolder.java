package www.fiberathome.com.parkingapp.view.parking;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;

public class LoaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.linearLayoutContainer)
    public LinearLayout linearLayoutContainer;
    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    public LoaderViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

