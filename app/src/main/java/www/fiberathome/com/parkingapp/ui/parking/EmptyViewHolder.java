package www.fiberathome.com.parkingapp.ui.parking;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import www.fiberathome.com.parkingapp.databinding.ItemListEmptyBinding;

@SuppressLint("NonConstantResourceId")
public class EmptyViewHolder extends RecyclerView.ViewHolder {
    public ItemListEmptyBinding binding;

    public EmptyViewHolder(ItemListEmptyBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}

