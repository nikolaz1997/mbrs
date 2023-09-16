package generator.associations;

public class Member {
    public String id;
    public String dataType;
    public AssociationType associationType;

    public Member(String id, String dataType, AssociationType associationType) {
        this.id = id;
        this.dataType = dataType;
        this.associationType = associationType;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", dataType='" + dataType + '\'' +
                ", associationType=" + associationType +
                '}';
    }
}
