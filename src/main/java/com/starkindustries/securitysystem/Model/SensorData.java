package com.starkindustries.securitysystem.Model;

/**
 * Clase modelo que representa una lectura de sensor.
 */
public class SensorData {

    private String type;
    private double value;
    private long timestamp;
    private boolean critical;

    public SensorData() {}

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
