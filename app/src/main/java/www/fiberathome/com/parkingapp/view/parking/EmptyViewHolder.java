package www.fiberathome.com.parkingapp.view.parking;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;

public class EmptyViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageViewSearchPlace)
    public ImageView imageViewSearchPlace;
    @BindView(R.id.tvEmptyView)
    public TextView tvEmptyView;

    public EmptyViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
