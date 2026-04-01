import java.util.Objects;

public class Production {
    private String leftSide;
    private String rightSide;

    public Production(String leftSide, String rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public String getLeftSide() { return leftSide; }
    public String getRightSide() { return rightSide; }

    @Override
    public String toString() { return leftSide + " -> " + rightSide; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(leftSide, that.leftSide) && Objects.equals(rightSide, that.rightSide);
    }

    @Override
    public int hashCode() { return Objects.hash(leftSide, rightSide); }
}