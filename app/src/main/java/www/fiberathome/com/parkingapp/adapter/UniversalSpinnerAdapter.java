package www.fiberathome.com.parkingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.model.Spinner;

public class UniversalSpinnerAdapter extends ArrayAdapter<Spinner> {

    private Context context;
    private List<Spinner> dataList;

    public UniversalSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Spinner> dataList) {
        super(context, resource, dataList);
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Spinner getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dataList.get(i).getId();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflter = LayoutInflater.from(context);
        convertView = inflter.inflate(R.layout.row_spinner_item, null);

        Spinner data = dataList.get(position);
        TextView textViewSectionName = convertView.findViewById(R.id.tv_name);
        textViewSectionName.setText(data.getValue());

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflter = LayoutInflater.from(context);
        convertView = inflter.inflate(R.layout.row_general_spinner_item_zero, null);

        Spinner data = dataList.get(position);
        TextView textViewSectionName = convertView.findViewById(R.id.general_spinner_tv_name);
        textViewSectionName.setText(data.getValue());

        return convertView;
    }

    public void setDataList(List<Spinner> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    /*public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }*/

    /*public void addAll(List<PersonType> dataList) {
        dataList.addAll(dataList);
        notifyDataSetChanged();
    }*/
}
