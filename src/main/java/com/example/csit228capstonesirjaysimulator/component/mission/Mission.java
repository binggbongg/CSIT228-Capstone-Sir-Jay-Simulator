package com.example.csit228capstonesirjaysimulator.component.mission;

public class Mission<T extends Comparable<T>> {

    private final int    missionId;
    private final String description;
    private final T      target;
    private       T      current;
    private       boolean completed;

    public Mission(int missionId, String description, T target, T initial) {
        this.missionId   = missionId;
        this.description = description;
        this.target      = target;
        this.current     = initial;
        this.completed   = false;
    }


    public synchronized void setCurrent(T value) {
        this.current = value;
        evaluateCompletion();
    }

    public synchronized void increment() {
        if (current instanceof Integer) {
            current = (T) Integer.valueOf(((Integer) current) + 1);
            evaluateCompletion();
        }
    }

    public synchronized void complete() {
        if (current instanceof Boolean) {
            current = (T) Boolean.TRUE;
            evaluateCompletion();
        }
    }

    private void evaluateCompletion() {
        completed = current.compareTo(target) >= 0;
    }

    public int     getMissionId()   { return missionId;   }
    public String  getDescription() { return description; }
    public T       getTarget()      { return target;      }
    public synchronized T getCurrent()     { return current;     }
    public synchronized boolean isCompleted()   { return completed;   }


    public synchronized String getProgressText() {
        if (current instanceof Boolean) {
            return completed ? "/" : "X";
        }
        return current + " / " + target;
    }

    @Override
    public String toString() {
        return "[Mission#" + missionId + "] " + description
                + " (" + getProgressText() + ")"
                + (completed ? " DONE" : "");
    }
}

