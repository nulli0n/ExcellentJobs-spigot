package su.nightexpress.excellentjobs.data.impl;

public class JobOrderCount {

    private int current;
    private int required;

    public JobOrderCount(int required) {
        this(0, required);
    }

    public JobOrderCount(int current, int required) {
        this.current = current;
        this.required = required;
    }

    public boolean isCompleted() {
        return this.getCurrent() >= this.getRequired();
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }
}
