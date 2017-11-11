package li.collect.dao;

/**
 * 创建时间: 2017/11/2
 * 创建人: Administrator
 * 功能描述:
 */

public class EventModule {
    private String eventType;
    private long time;

    public EventModule() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
