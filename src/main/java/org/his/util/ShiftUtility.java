package org.his.util;

import org.his.entity.user.Doctor;
import org.his.entity.user.Nurse;

public class ShiftUtility {

    public static boolean isDoctorOnShift(Doctor doctor, int hour, int currentDayOfWeek) {
        return switch (currentDayOfWeek) {
            case 1 ->
                    (doctor.getMon() == 1 && hour >= 0 && hour < 9) || (doctor.getMon() == 2 && hour >= 9 && hour < 17)
                            || (doctor.getMon() == 3 && hour >= 17 && hour < 24);
            case 2 ->
                    (doctor.getTue() == 1 && hour >= 0 && hour < 9) || (doctor.getTue() == 2 && hour >= 9 && hour < 17)
                            || (doctor.getTue() == 3 && hour >= 17 && hour < 24);
            case 3 ->
                    (doctor.getWed() == 1 && hour >= 0 && hour < 9) || (doctor.getWed() == 2 && hour >= 9 && hour < 17)
                            || (doctor.getWed() == 3 && hour >= 17 && hour < 24);
            case 4 ->
                    (doctor.getThu() == 1 && hour >= 0 && hour < 9) || (doctor.getThu() == 2 && hour >= 9 && hour < 17)
                            || (doctor.getThu() == 3 && hour >= 17 && hour < 24);
            case 5 ->
                    (doctor.getFri() == 1 && hour >= 0 && hour < 9) || (doctor.getFri() == 2 && hour >= 9 && hour < 17)
                            || (doctor.getFri() == 3 && hour >= 17 && hour < 24);
            case 6 ->
                    (doctor.getSat() == 1 && hour >= 0 && hour < 9) || (doctor.getSat() == 2 && hour >= 9 && hour < 17)
                            || (doctor.getSat() == 3 && hour >= 17 && hour < 24);
            case 7 ->
                    (doctor.getSun() == 1 && hour >= 0 && hour < 9) || (doctor.getSun() == 2 && hour >= 9 && hour < 17)
                            || (doctor.getSun() == 3 && hour >= 17 && hour < 24);
            default -> false;
        };
    }

    public static boolean isNurseOnShift(Nurse nurse, int hour, int currentDayOfWeek) {
        return switch (currentDayOfWeek) {
            case 1 ->
                    (nurse.getMon() == 1 && hour >= 0 && hour < 9) || (nurse.getMon() == 2 && hour >= 9 && hour < 17)
                            || (nurse.getMon() == 3 && hour >= 17 && hour < 24);
            case 2 ->
                    (nurse.getTue() == 1 && hour >= 0 && hour < 9) || (nurse.getTue() == 2 && hour >= 9 && hour < 17)
                            || (nurse.getTue() == 3 && hour >= 17 && hour < 24);
            case 3 ->
                    (nurse.getWed() == 1 && hour >= 0 && hour < 9) || (nurse.getWed() == 2 && hour >= 9 && hour < 17)
                            || (nurse.getWed() == 3 && hour >= 17 && hour < 24);
            case 4 ->
                    (nurse.getThu() == 1 && hour >= 0 && hour < 9) || (nurse.getThu() == 2 && hour >= 9 && hour < 17)
                            || (nurse.getThu() == 3 && hour >= 17 && hour < 24);
            case 5 ->
                    (nurse.getFri() == 1 && hour >= 0 && hour < 9) || (nurse.getFri() == 2 && hour >= 9 && hour < 17)
                            || (nurse.getFri() == 3 && hour >= 17 && hour < 24);
            case 6 ->
                    (nurse.getSat() == 1 && hour >= 0 && hour < 9) || (nurse.getSat() == 2 && hour >= 9 && hour < 17)
                            || (nurse.getSat() == 3 && hour >= 17 && hour < 24);
            case 7 ->
                    (nurse.getSun() == 1 && hour >= 0 && hour < 9) || (nurse.getSun() == 2 && hour >= 9 && hour < 17)
                            || (nurse.getSun() == 3 && hour >= 17 && hour < 24);
            default -> false;
        };
    }
}
