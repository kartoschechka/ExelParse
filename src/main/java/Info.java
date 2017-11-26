import java.util.ArrayList;
import java.util.List;

public class Info {
    private String date;
    private String group;
    private int numberOfPair;
    private List<Integer> numberSubgroup;

    public String getDate() {
        return date;
    }

    public String getGroup() {
        return group;
    }

    public int getNumberOfPair() {
        return numberOfPair;
    }

    public Integer getNumberSubgroup(int i) {
        return numberSubgroup.get(i);
    }

    public void addNumberSubgroup(int i) {
        this.numberSubgroup.add(i);
    }

    public String getType(int i) {
        return type.get(i);
    }

    public void addType(String type) {
        this.type.add(type);
    }

    public String getDiscipline(int i) {
        return discipline.get(i);
    }

    public void addDiscipline(String discipline) {
        this.discipline.add(discipline);
    }

    public String getTeacher(int i) {
        return teacher.get(i);
    }

    public void addTeacher(String teacher) {
        this.teacher.add(teacher);
    }

    public String getAudience(int i) {
        return audience.get(i);
    }

    public void addAudience(String audience) {
        this.audience.add(audience);
    }

    private List<String> type;
    private List<String> discipline;
    private List<String> teacher;
    private List<String> audience;

    public Info(String date, String group, int numberOfPair) {
        this.date = date;
        this.group = group;
        this.numberOfPair = numberOfPair;
        type = new ArrayList<String>();
        numberSubgroup = new ArrayList<Integer>();
        discipline = new ArrayList<String>();
        teacher = new ArrayList<String>();
        audience = new ArrayList<String>();
    }
}
