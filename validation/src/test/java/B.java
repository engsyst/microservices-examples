import java.math.BigDecimal;

class B {
    String name; // always must be present, not null, cannot contain leading and trailing whitespaces, between words can be only single space character;
    String description; //  may be absent or null
}

class A extends B {
    BigDecimal price; // >= 0.0
}

class C extends B {
    boolean available; // if amount == 0 must be false
    int amount; // >=1 but if available == true must be zero
}

