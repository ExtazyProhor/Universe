package ru.prohor.universe.kryonet.entities;

import java.util.LinkedList;
import java.util.List;

public class PlayerState extends Message {
    public int id;
    public float x;
    public float y;
    public float angle;
    public int hp;
    public List<Bullet> bullets;

    public PlayerState(int id,float x,float y,float angle,int hp) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.hp = hp;
        this.bullets = new LinkedList<>();
    }

    public PlayerState(PlayerState copy) {
        this.id = copy.id;
        this.x = copy.x;
        this.y = copy.y;
        this.angle = copy.angle;
        this.hp = copy.hp;
        this.bullets = new LinkedList<>();
        for (Bullet b : copy.bullets) {
            this.bullets.add(new Bullet(b));
        }
    }
}
