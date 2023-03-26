
package entidades;

public class Peticion {
    public String alcance;
    public String tipo;
    public String datos;
    
    public Peticion(){}

    public Peticion(String alcance, String tipo, String datos) {
        this.alcance = alcance;
        this.tipo = tipo;
        this.datos = datos;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    @Override
    public String toString() {
        return "Peticion{" +
                "alcance='" + alcance + '\'' +
                ", tipo='" + tipo + '\'' +
                ", datos=" + datos +
                '}';
    }
}
