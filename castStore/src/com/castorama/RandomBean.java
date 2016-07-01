package com.castorama;
import java.io.Serializable;
import java.util.Random;

public class RandomBean implements Serializable {

    public int getNextInt() {
        int value = ((new Random()).nextInt() % 2) + 1;
        return (value != 0) ? value : 1;
    }

}
