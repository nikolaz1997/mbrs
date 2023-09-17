package generator.entities;

public class EntityProperty {
    public String type;
    public String name;
    public String association;
    public String decorator;

    public EntityProperty(String type, String name, String association, String decorator) {
        this.type = type;
        this.name = name;
        this.association = association;
        this.decorator = decorator;
    }

    @Override
    public String toString() {
        return "EntityProperty{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", association='" + association + '\'' +
                ", decorator='" + decorator + '\'' +
                '}';
    }
}
