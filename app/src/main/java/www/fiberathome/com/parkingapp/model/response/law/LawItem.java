package www.fiberathome.com.parkingapp.model.response.law;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class LawItem extends ExpandableGroup<Law> {
    public LawItem(String title, List<Law> items) {
        super(title, items);
    }

}
