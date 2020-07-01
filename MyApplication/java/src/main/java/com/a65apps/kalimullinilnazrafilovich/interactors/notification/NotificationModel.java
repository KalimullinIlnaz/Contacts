package com.a65apps.kalimullinilnazrafilovich.interactors.notification;

import com.a65apps.kalimullinilnazrafilovich.entities.BirthdayNotification;
import com.a65apps.kalimullinilnazrafilovich.entities.Contact;
import com.a65apps.kalimullinilnazrafilovich.interactors.calendar.BirthdayCalendar;
import com.a65apps.kalimullinilnazrafilovich.interactors.time.CurrentTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotificationModel implements NotificationInteractor {
    private final NotificationRepository notificationRepository;

    private final CurrentTime currentTime;
    private GregorianCalendar birthdayGregorianCalendar;

    public NotificationModel(
            NotificationRepository notificationRepository,
            CurrentTime currentTime,
            BirthdayCalendar birthdayCalendar) {
        this.notificationRepository = notificationRepository;
        this.currentTime = currentTime;
        this.birthdayGregorianCalendar = birthdayCalendar.getBirthdayCalendar();
    }

    @Override
    public BirthdayNotification onBirthdayNotification(Contact contact) {
        if (birthdayGregorianCalendar.isLeapYear(birthdayGregorianCalendar.get(GregorianCalendar.YEAR))) {
            return setBirthdayReminderForLeapYear(contact);
        } else {
            return setBirthdayReminder(contact);
        }
    }

    @Override
    public BirthdayNotification offBirthdayNotification(Contact contact) {
        return notificationRepository.removeBirthdayReminder(contact);
    }

    @Override
    public BirthdayNotification getNotificationWorkStatus(Contact contact) {
        return notificationRepository.getBirthdayNotificationEntity(contact);
    }

    private BirthdayNotification setBirthdayReminder(Contact contact) {
        GregorianCalendar gregorianCalendar = createGregorianCalendarForContact(contact);

        if (currentTime.now() > gregorianCalendar.getTime().getTime()) {
            if (!(gregorianCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY &&
                    gregorianCalendar.get(Calendar.DATE) == 29)) {
                gregorianCalendar.add(Calendar.YEAR, 1);
            }
        }


    /*    if (currentTime.now() > gregorianCalendar.getTime().getTime()) {
            if (gregorianCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY &&
                    gregorianCalendar.get(Calendar.DATE) == 29) {
                while (!gregorianCalendar.isLeapYear(Calendar.YEAR)) {
                    gregorianCalendar.add(Calendar.YEAR, 1);
                }
            } else {
                gregorianCalendar.add(Calendar.YEAR, 1);
            }
        }*/

        return notificationRepository.setBirthdayReminder(contact, gregorianCalendar);
    }

    private BirthdayNotification setBirthdayReminderForLeapYear(Contact contact) {
        GregorianCalendar gregorianCalendar = createGregorianCalendarForContact(contact);

        if (currentTime.now() > gregorianCalendar.getTime().getTime()) {
            gregorianCalendar.add(Calendar.YEAR, 4);
        }

        return notificationRepository.setBirthdayReminder(contact, gregorianCalendar);
    }


    private GregorianCalendar createGregorianCalendarForContact(Contact contact) {
        if (contact.getDateOfBirth().get(Calendar.DAY_OF_MONTH) == 29){
            birthdayGregorianCalendar.set(GregorianCalendar.YEAR,birthdayGregorianCalendar.get(Calendar.YEAR));
            while (!birthdayGregorianCalendar.isLeapYear(birthdayGregorianCalendar.get(GregorianCalendar.YEAR))) {
                birthdayGregorianCalendar.add(Calendar.YEAR, 1);
            }
        }else {
            birthdayGregorianCalendar.set(GregorianCalendar.YEAR,birthdayGregorianCalendar.get(Calendar.YEAR));
        }

        birthdayGregorianCalendar.set(GregorianCalendar.DAY_OF_MONTH, contact.getDateOfBirth().get(GregorianCalendar.DAY_OF_MONTH));
        birthdayGregorianCalendar.set(GregorianCalendar.MONTH, contact.getDateOfBirth().get(GregorianCalendar.MONTH));

        return birthdayGregorianCalendar;
    }
}
