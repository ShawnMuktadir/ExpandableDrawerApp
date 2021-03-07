package www.fiberathome.com.parkingapp.model;

public class Language {

    private String name;
    private String subName;
    private String isoCode;
    private boolean isSelected;
    private boolean isLangSelected;

    public Language(String name, String subName, String isoCode, boolean isSelected, boolean isLangSelected) {
        this.name = name;
        this.subName = subName;
        this.isoCode = isoCode;
        this.isSelected = isSelected;
        this.isLangSelected = isLangSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isLangSelected() {
        return isLangSelected;
    }

    public void setLangSelected(boolean langSelected) {
        isLangSelected = langSelected;
    }
}
