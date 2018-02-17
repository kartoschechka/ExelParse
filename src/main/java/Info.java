import java.util.ArrayList;
import java.util.List;

public class Info {
    private String date;
    private String group;
    private int numberOfPair;
    private List<Integer> numberSubgroup;

    public void addNumberSubgroup(int i) {
        this.numberSubgroup.add(i);
    }

    public void addType(String type) {
        this.type.add(type);
    }

    public void addDiscipline(String discipline) {
        this.discipline.add(discipline);
    }

    public void addTeacher(String teacher) {
        this.teacher.add(teacher);
    }

    public void addAudience(String audience) {
        this.audience.add(audience);
    }

    private List<String> type;
    private List<String> discipline;
    private List<String> teacher;
    private List<String> audience;

    public String getDate() {
        return date;
    }

    public String getGroup() {
        return group;
    }

    public int getNumberOfPair() {
        return numberOfPair;
    }

    public List<Integer> getNumberSubgroup() {
        return numberSubgroup;
    }

    public String getType(int i) {
        if (type.get (i).equals ("NULL")){
            return "NULL";
        } else  return "'" + type.get (i)+"'";
    }

    public String getDiscipline(int i) {
        if (discipline.get (i).equals ("NULL")){
            return "NULL";
        } else  return "'" + discipline.get (i)+"'";
    }

    public String getTeacher(int i) {
        if (teacher.get (i).equals ("NULL")){
            return "NULL";
        } else  return "'" + teacher.get (i)+"'";
    }

    public String getAudience(int i) {
        if (i>=audience.size ()) return "NULL";
        if (audience.get (i).equals ("NULL")){
            return "NULL";
        } else  return "'" + audience.get (i)+"'";
    }

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

    @Override
    public String toString() {
        return "Info{" +
                "date='" + date + '\'' +
                ", group='" + group + '\'' +
                ", numberOfPair=" + numberOfPair +
                ", numberSubgroup=" + numberSubgroup +
                ", type=" + type +
                ", discipline=" + discipline +
                ", teacher=" + teacher +
                ", audience=" + audience +
                '}';
    }
}
