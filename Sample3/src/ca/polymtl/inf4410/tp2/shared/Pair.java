package ca.polymtl.inf4410.tp2.shared;
import java.io.Serializable;
import java.lang.Object;
import java.util.*;

// Cettee classe represente une operation
public class Pair implements Serializable{

    private final String Operation;
    private final int Operande;

    public Pair(String Operation, int Operande) {
        this.Operation = Operation;
        this.Operande = Operande;
    }

    public String getOperation() {
        return Operation;
    }

    public int getOperande() {
        return Operande;
    }

}
