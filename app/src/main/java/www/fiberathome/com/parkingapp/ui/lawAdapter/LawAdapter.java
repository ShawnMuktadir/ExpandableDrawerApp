package www.fiberathome.com.parkingapp.ui.lawAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.law.Law;
import www.fiberathome.com.parkingapp.model.law.LawItem;

/*
 * Copyright (C) 2018 Levi Rizki Saputra (levirs565@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by LEVI on 22/09/2018.
 */
public class LawAdapter extends ExpandableRecyclerViewAdapter<TitleViewHolder, LawViewHolder> implements Filterable {
    List<LawItem> copylawItemList;
    private String TAG = getClass().getSimpleName();

    @SuppressWarnings("unchecked")
    public LawAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
        copylawItemList = new ArrayList<LawItem>((Collection<? extends LawItem>) groups);
        Log.d(TAG, "LawAdapter: " + copylawItemList.size());
    }

    @Override
    public TitleViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_recyclerview_title, parent, false);
        return new TitleViewHolder(v);
    }

    @Override
    public LawViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_recyclerview_laws, parent, false);
        return new LawViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(LawViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Law law = (Law) group.getItems().get(childIndex);
        holder.bind(law);
    }

    @Override
    public void onBindGroupViewHolder(TitleViewHolder holder, int flatPosition, ExpandableGroup group) {
        final LawItem lawItem = (LawItem) group;
        holder.bind(lawItem);
    }


    @Override
    public Filter getFilter() {
        return queryFilter;
    }

    private Filter queryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<LawItem> queryLawItemList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                queryLawItemList.addAll(copylawItemList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (LawItem item : copylawItemList) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        queryLawItemList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = queryLawItemList;

            return results;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List groups = (ArrayList<? extends ExpandableGroup>) results.values; // has the filtered values
            getGroups().clear();
            getGroups().addAll(groups);

            notifyDataSetChanged();
        }
    };
}

