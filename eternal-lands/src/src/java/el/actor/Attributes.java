package el.actor;

public class Attributes {
    public static class Attribute {
        public int current;
        public int base;
    }

    public static class BaseAttributes {
        public Attribute phy = new Attribute();
        public Attribute coo = new Attribute();
        public Attribute rea = new Attribute();
        public Attribute wil = new Attribute();
        public Attribute ins = new Attribute();
        public Attribute vit = new Attribute();
    }

    public static class NexusAttributes {
        public Attribute human = new Attribute();
        public Attribute animal = new Attribute();
        public Attribute vegetal = new Attribute();
        public Attribute inorganic = new Attribute();
        public Attribute artificial = new Attribute();
        public Attribute magic = new Attribute();
    }

    public static class SkillAttributes {
        public Attribute defense = new Attribute();
        public Attribute attack = new Attribute();
        public Attribute harvesting = new Attribute();
        public Attribute manufacturing = new Attribute();
        public Attribute alchemy = new Attribute();
        public Attribute magic = new Attribute();
        public Attribute potion = new Attribute();
        public Attribute summoning = new Attribute();
        public Attribute crafting = new Attribute();
        public Attribute engineering = new Attribute();
        public Attribute tailoring = new Attribute();
        public Attribute ranging = new Attribute();
        public Attribute overall = new Attribute();
    }

    public static class CrossAttributes {
        public Attribute might = new Attribute();
        public Attribute matter = new Attribute();
        public Attribute toughness = new Attribute();
        public Attribute charm = new Attribute();
        public Attribute reaction = new Attribute();
        public Attribute perception = new Attribute();
        public Attribute rationality = new Attribute();
        public Attribute dexterity = new Attribute();
        public Attribute ethereality = new Attribute();
    }
}
