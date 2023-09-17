package generator.associations;

public class Association {
    public String id;
    public Member memberOne;
    public Member memberTwo;

    public Association(String id) {
        this.id = id;
    }

    public void AssignMemberOneId(String memberOneId) {
        this.memberOne = new Member(memberOneId, null, null);
    }

    public void AssignMemberTwoId(String memberTwoId) {
        this.memberTwo = new Member(memberTwoId, null, null);
    }

    @Override
    public String toString() {
        return "Association{" +
                "id='" + id + '\'' +
                ", memberOne=" + memberOne +
                ", memberTwo=" + memberTwo +
                '}';
    }
}
