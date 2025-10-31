package ru.prohor.universe.kryonet.entities;

public class Bullet extends Message {
    public float x;
    public float y;
    public float sin;
    public float cos;

    public Bullet(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.sin = (float) Math.sin(angle);
        this.cos = (float) Math.cos(angle);
    }

    public Bullet(Bullet copy) {
        this.x = copy.x;
        this.y = copy.y;
        this.sin = copy.sin;
        this.cos = copy.cos;
    }
}
