package com.starkindustries.securitysystem.Model;

/**
 * Representa la información genérica enviada por cualquier sensor del sistema.
 */
public class SensorData {

    private String type;       // Tipo de sensor: movimiento, temperatura, acceso
    private double value;      // Valor medido
    private long timestamp;    // Momento de la lectura en milisegundos
    private boolean critical;  // Indica si el evento es crítico o no

    public SensorData() {
    }

    public SensorData(String type, double value, long timestamp, boolean critical) {
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.critical = critical;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "type='" + type + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", critical=" + critical +
                '}';
    }
}
