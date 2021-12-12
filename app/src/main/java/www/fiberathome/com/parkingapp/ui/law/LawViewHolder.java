package www.fiberathome.com.parkingapp.ui.law;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.response.law.Law;

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
public class LawViewHolder extends ChildViewHolder {
    private final TextView mTextView;

    public LawViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.textView);
    }

    public void bind(Law law) {
        mTextView.setText(law.getC());
    }
}
