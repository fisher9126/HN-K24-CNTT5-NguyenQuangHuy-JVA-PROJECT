package booking.entity;

import java.time.LocalDateTime;

public class ScheduleTime implements Comparable<ScheduleTime>{
    private LocalDateTime start;
    private LocalDateTime end;

    public ScheduleTime() {
    }

    public ScheduleTime(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public int compareTo(ScheduleTime o) {
        return start.compareTo(o.start);
    }

    @Override
    public String toString() {
        return "ScheduleTime{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
