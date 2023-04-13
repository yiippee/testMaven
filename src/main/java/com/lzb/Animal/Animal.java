package com.lzb.Animal;

public interface Animal {
    public void eat();
    public void travel();

    public int a = 888;
}

class MammalInt implements Animal{

    public void eat(){
        System.out.println("Mammal eats");
    }

    public void travel(){
        System.out.println("Mammal travels");
    }

    public int noOfLegs(){
        return 0;
    }

    public static void main(String args[]){
        MammalInt m = new MammalInt();
        m.eat();
        m.travel();

        int a = m.a;
        System.out.printf("a = %d\n", a);
    }
}