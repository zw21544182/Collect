package li.collect.dao;

import java.util.List;

/**
 * 创建时间: 2017/11/2
 * 创建人: Administrator
 * 功能描述:
 */

public class SensorModule  {
    private String sensorType;
    private List<Float> floats;
    private long time;

    public SensorModule() {
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public List<Float> getFloats() {
        return floats;
    }

    public void setFloats(List<Float> floats) {
        this.floats = floats;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
