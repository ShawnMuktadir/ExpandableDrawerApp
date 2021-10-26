package www.fiberathome.com.parkingapp.ui.parking;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;

@SuppressLint("NonConstantResourceId")
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

