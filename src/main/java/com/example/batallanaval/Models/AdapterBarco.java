package com.example.batallanaval.Models;

public class AdapterBarco implements IBarco{
    @Override
    public void registrarImpacto() {

    }

    @Override
    public boolean estaHundido() {
        return false;
    }

    @Override
    public int getLongitud() {
        return 0;
    }

    @Override
    public String getTipo() {
        return "";
    }

    @Override
    public boolean isHorizontal() {
        return false;
    }

    @Override
    public void setHorizontal(boolean horizontal) {

    }
}
