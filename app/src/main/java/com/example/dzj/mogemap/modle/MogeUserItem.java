package org.studyplatform.model;

public class MogeUserItem implements Comparable<MogeUserItem> {
    private int id;
    private String name;
    private String head;
    private Double distance;

    public int getId() {
        return id;
    }

    public double getDistance() {
        return distance;
    }

    public String getHead() {
        return head;
    }

    public String getName() {
        return name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(MogeUserItem o) {
        // TODO Auto-generated method stub
        Double a = this.getDistance();
        Double b = o.getDistance();
        return a.compareTo(b);
    }
    public String toString() {
        return "id="+id+" distance="+distance;
    }
}
