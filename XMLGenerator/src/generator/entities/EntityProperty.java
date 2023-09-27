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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getDecorator() {
        return decorator;
    }

    public void setDecorator(String decorator) {
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
