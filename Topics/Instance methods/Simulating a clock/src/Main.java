
class Clock {
    final int halfDay = 12;
    final int fullHour = 60;
    int hours = halfDay;
    int minutes = 0;

    void next() {
        minutes++;
        if (minutes == fullHour) {
            minutes = 0;
            hours++;
            hours = hours > halfDay ? 1 : hours;
        }
    }
}