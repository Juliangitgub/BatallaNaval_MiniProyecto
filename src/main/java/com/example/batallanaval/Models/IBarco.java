package com.example.batallanaval.Models;

public interface IBarco {
    public void registrarImpacto();
    public boolean estaHundido();
    public int getLongitud();
    public String getTipo();
    public boolean isHorizontal();
    public void setHorizontal(boolean horizontal);
}
