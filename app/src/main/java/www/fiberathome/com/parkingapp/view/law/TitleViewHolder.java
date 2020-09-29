package www.fiberathome.com.parkingapp.view.law;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.law.LawItem;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

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
public class TitleViewHolder extends GroupViewHolder {
    private TextView mTextView;
    private ImageView arrow;

    public TitleViewHolder(View itemView) {
        super(itemView);

        mTextView = itemView.findViewById(R.id.textView);
        arrow = itemView.findViewById(R.id.arrow);
    }

    public void bind(LawItem item) {
        mTextView.setText(item.getTitle());
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
