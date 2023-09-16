package generator.entities;

public class EntityProperty {
    public String type;
    public String name;
    public String association;

    public EntityProperty(String type, String name, String association) {
        this.type = type;
        this.name = name;
        this.association = association;
    }

    @Override
    public String toString() {
        return "EntityProperty{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", association='" + association + '\'' +
                '}';
    }
}
